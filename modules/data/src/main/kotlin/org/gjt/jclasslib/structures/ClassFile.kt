/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures

import org.gjt.jclasslib.structures.constants.ConstantPlaceholder
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info
import java.io.DataInput
import java.io.DataOutput
import java.util.*

/**
 * The class file structure in which all other structures are hooked up.
 */
class ClassFile : Structure(), AttributeContainer {

    /**
     * Minor version of the class file format.
     */
    var minorVersion: Int = 0

    /**
     * Major version of the class file format.
     */
    var majorVersion: Int = 0

    /**
     * Constant pool entries.
     */
    var constantPool: Array<Constant> = emptyArraySingleton()
        set(constantPool) {
            field = constantPool
            constantPool.forEachIndexed { i, cpInfo ->
                constantPoolEntryToIndex.put(cpInfo, i)
            }
        }

    private val constantPoolEntryToIndex = HashMap<Constant, Int>()

    /**
     * Access flags of this class.
     */
    var accessFlags: Int = 0

    /**
     * Constant pool index of this class.
     */
    var thisClass: Int = 0

    /**
     * Constant pool index of the super class of this class.
     */
    var superClass: Int = 0

    /**
     * Constant pool entries of all interfaces.
     */
    var interfaces: IntArray = IntArray(0)

    /**
     * FieldInfo structures for the fields of this class.
     */
    var fields: Array<FieldInfo> = emptyArraySingleton()

    /**
     * MethodInfo structures for the methods of this class.
     */
    var methods: Array<MethodInfo> = emptyArraySingleton()

    override var attributes: Array<AttributeInfo> = emptyArraySingleton()

    /**
     * Verbose major version of the class file format.
     */
    val majorVersionVerbose: String
        get() = when (majorVersion) {
            45 -> "1.1"
            46 -> "1.2"
            47 -> "1.3"
            48 -> "1.4"
            49 -> "1.5"
            50 -> "1.6"
            51 -> "1.7"
            52 -> "1.8"
            53 -> "1.9"
            else -> "unknown value $majorVersion"
        }

    /**
     * Index of an equivalent constant pool entry, or -1 if no equivalent constant pool entry can be found.
     *
     * @param constant the constant pool entry
     */
    fun getConstantPoolIndex(constant: Constant): Int {
        return constantPoolEntryToIndex[constant] ?: -1
    }

    /**
     * Set all constant pool entries where the new array
     * of constant pool entries must start with the old constant pool. If
     * you delete entries, use setConstantPool.
     * @param enlargedConstantPool the enlarged constant pool
     */
    fun enlargeConstantPool(enlargedConstantPool: Array<Constant>) {
        for (i in constantPool.size..enlargedConstantPool.size - 1) {
            constantPoolEntryToIndex.put(enlargedConstantPool[i], i)
        }
        this.constantPool = enlargedConstantPool
    }

    /**
     * Register the constant pool entry at a given index, so that it can
     * be found through the getConstantPoolIndex method.
     *
     * @param index the index
     */
    fun registerConstantPoolEntry(index: Int) {
        constantPoolEntryToIndex.put(constantPool[index], index)
    }

    /**
     * Unregister the constant pool entry at a given index, so that it can
     * no longer be found through the getConstantPoolIndex method.
     *
     * @param index the index
     */
    fun unregisterConstantPoolEntry(index: Int) {
        constantPoolEntryToIndex.remove(constantPool[index])
    }

    /**
     * The name of this class.
     */
    val thisClassName: String
        @Throws(InvalidByteCodeException::class)
        get() = getConstantPoolEntryName(thisClass)

    /**
     * The simple name of this class without the package.
     */
    val simpleClassName: String
        get() = thisClassName.takeLastWhile { it != '/' }

    /**
     * The name of the super class.
     */
    val superClassName: String
        @Throws(InvalidByteCodeException::class)
        get() = getConstantPoolEntryName(superClass)

    /**
     * The access flags of this class as a hex string.
     */
    val formattedAccessFlags: String
        get() = formatAccessFlags(accessFlags)

    /**
     * The verbose description of the access flags of this class.
     */
    val accessFlagsVerbose: String
        get() = formatAccessFlagsVerbose(AccessFlag.CLASS_ACCESS_FLAGS, accessFlags)

    /**
     * The ConstantUtf8Info constant pool entry at the specified index.
     * @param index the index
     */
    @Throws(InvalidByteCodeException::class)
    fun getConstantPoolUtf8Entry(index: Int): ConstantUtf8Info {
        return getConstantPoolEntry(index, ConstantUtf8Info::class.java)
    }

    /**
     * Get the constant pool entry at the specified index and cast it to a specified class.
     * @param index the index
     * @param entryClass the required subtype of CPInfo
     */
    @Throws(InvalidByteCodeException::class)
    fun <T : Constant> getConstantPoolEntry(index: Int, entryClass: Class<T>): T {
        checkValidConstantPoolIndex(index)

        val cpInfo = constantPool[index]
        if (entryClass.isAssignableFrom(cpInfo::class.java)) {
            return entryClass.cast(cpInfo)
        } else {
            throw InvalidByteCodeException("constant pool entry at $index of class ${cpInfo::class.java.name} is not assignable to ${entryClass.name}")
        }
    }

    /**
     * Get an approximate verbose description of the content of the constant pool entry
     * at the specified index.
     *
     * @param index the index
     */
    @Throws(InvalidByteCodeException::class)
    fun getConstantPoolEntryName(index: Int): String {
        checkValidConstantPoolIndex(index)
        return constantPool[index].verbose
    }

    /**
     * Get the index of a field for given field name and signature or -1 if not found.
     * @param name       the field name.
     * @param descriptor the signature.
     */
    @Throws(InvalidByteCodeException::class)
    fun getFieldIndex(name: String, descriptor: String): Int {

        fields.forEachIndexed { i, fieldInfo ->
            if (fieldInfo.name == name && fieldInfo.descriptor == descriptor) {
                return i
            }
        }
        return -1
    }

    /**
     * Get the FieldInfo for given field name and signature or null if not found.
     * @param name       the field name.
     * @param descriptor the signature.
     */
    @Throws(InvalidByteCodeException::class)
    fun getField(name: String, descriptor: String): FieldInfo? {
        val index = getFieldIndex(name, descriptor)
        return if (index < 0) null else fields[index]
    }

    /**
     * Get the index of a method for given method name and signature or -1 if not found.
     * @param name       the method name.
     * @param descriptor the signature.
     */
    @Throws(InvalidByteCodeException::class)
    fun getMethodIndex(name: String, descriptor: String): Int {
        methods.forEachIndexed { i, methodInfo ->
            if (methodInfo.name == name && methodInfo.descriptor == descriptor) {
                return i
            }
        }
        return -1
    }

    /**
     * Get the MethodInfo for given method name and signature or null if not found.
     * @param name       the method name.
     * @param descriptor the signature.
     */
    @Throws(InvalidByteCodeException::class)
    fun getMethod(name: String, descriptor: String): MethodInfo? {
        val index = getMethodIndex(name, descriptor)
        return if (index < 0) null else methods[index]
    }

    override fun readData(input: DataInput) {
        readMagicNumber(input)
        readVersion(input)
        readConstantPool(input)
        readAccessFlags(input)
        readThisClass(input)
        readSuperClass(input)
        readInterfaces(input)
        readFields(input)
        readMethods(input)
        readAttributes(input, this)
    }

    override fun writeData(output: DataOutput) {
        writeMagicNumber(output)
        writeVersion(output)
        writeConstantPool(output)
        writeAccessFlags(output)
        writeThisClass(output)
        writeSuperClass(output)
        writeInterfaces(output)
        writeFields(output)
        writeMethods(output)
        writeAttributes(output)
    }

    override val debugInfo: String
        get() = ""

    private fun checkValidConstantPoolIndex(index: Int) {
        if (index < 1 || index >= constantPool.size) {
            throw InvalidByteCodeException("Constant pool index $index is out of bounds [0, ${constantPool.size - 1}]")
        }
    }

    private fun readMagicNumber(input: DataInput) {
        val magicNumber = input.readInt()
        if (magicNumber != MAGIC_NUMBER) {
            throw InvalidByteCodeException("Invalid magic number ${magicNumber.hex} instead of ${MAGIC_NUMBER.hex}")
        }

        if (isDebug) debug("read magic number")
    }

    private fun writeMagicNumber(out: DataOutput) {
        out.writeInt(MAGIC_NUMBER)
        if (isDebug) debug("wrote magic number")
    }

    private fun readVersion(input: DataInput) {
        minorVersion = input.readUnsignedShort()
        if (isDebug) debug("read minor version $minorVersion")

        majorVersion = input.readUnsignedShort()
        if (isDebug) debug("read major version $majorVersion")

        checkMajorVersion(majorVersion)
    }

    private fun writeVersion(out: DataOutput) {
        out.writeShort(minorVersion)
        if (isDebug) debug("wrote minor version $minorVersion")

        out.writeShort(majorVersion)
        if (isDebug) debug("wrote major version $majorVersion")

        checkMajorVersion(majorVersion)
    }

    private fun readConstantPool(input: DataInput) {
        constantPoolEntryToIndex.clear()
        val constantPoolCount = input.readUnsignedShort()
        if (isDebug) debug("read constant pool count $constantPoolCount")

        // constantPool[0] is not used
        var placeholderIndex = 0
        constantPool = Array(constantPoolCount) { i ->
            when (i) {
                placeholderIndex -> ConstantPlaceholder
                else -> {
                    if (isDebug) debug("reading constant pool entry $i")
                    val constantType = ConstantType.getFromTag(input.readByte().toInt())
                    constantType.read(this, input).apply {
                        constantPoolEntryToIndex.put(this, i)
                        val extraEntryCount = constantType.extraEntryCount
                        if (extraEntryCount > 0) {
                            // CONSTANT_Double_info and CONSTANT_Long_info take 2 constant
                            // pool entries, the second entry is unusable (design mistake)
                            placeholderIndex = i + extraEntryCount
                        }
                    }
                }
            }
        }
    }

    private fun writeConstantPool(out: DataOutput) {
        out.writeShort(constantPool.size)
        if (isDebug) debug("wrote constant pool count ${constantPool.size}")

        constantPool.forEachIndexed { i, cpInfo ->
            if (cpInfo !is ConstantPlaceholder) {
                if (isDebug) debug("writing constant pool entry $i")
                cpInfo.write(out)
            }
        }
    }

    private fun readAccessFlags(input: DataInput) {
        accessFlags = input.readUnsignedShort()
        if (isDebug) debug("read access flags $accessFlagsVerbose")
    }

    private fun writeAccessFlags(output: DataOutput) {
        output.writeShort(accessFlags)
        if (isDebug) debug("wrote access flags $accessFlagsVerbose")
    }

    private fun readThisClass(input: DataInput) {
        thisClass = input.readUnsignedShort()
        if (isDebug) debug("read this_class index $thisClass")
    }

    private fun writeThisClass(output: DataOutput) {
        output.writeShort(thisClass)
        if (isDebug) debug("wrote this_class index $thisClass")
    }

    private fun readSuperClass(input: DataInput) {
        superClass = input.readUnsignedShort()
        if (isDebug) debug("read super_class index $superClass")
    }

    private fun writeSuperClass(output: DataOutput) {
        output.writeShort(superClass)
        if (isDebug) debug("wrote super_class index $superClass")
    }

    private fun readInterfaces(input: DataInput) {
        val interfacesCount = input.readUnsignedShort()
        if (isDebug) debug("read interfaces count $interfacesCount")

        interfaces = IntArray(interfacesCount) {
            val index = input.readUnsignedShort()
            if (isDebug) debug("read interface index $index")
            index
        }
    }

    private fun writeInterfaces(out: DataOutput) {
        val interfacesCount = interfaces.size
        out.writeShort(interfacesCount)
        if (isDebug) debug("wrote interfaces count $interfacesCount")

        interfaces.forEach {
            out.writeShort(it)
            if (isDebug) debug("wrote interface index $it")
        }
    }

    private fun readFields(input: DataInput) {
        val fieldsCount = input.readUnsignedShort()
        if (isDebug) debug("read fields count $fieldsCount")

        fields = Array(fieldsCount) {
            FieldInfo(this).apply {
                read(input)
            }
        }
    }

    private fun writeFields(out: DataOutput) {
        val fieldsCount = fields.size
        out.writeShort(fieldsCount)
        if (isDebug) debug("wrote fields count $fieldsCount")

        fields.forEach { it.write(out) }
    }

    private fun readMethods(input: DataInput) {

        val methodsCount = input.readUnsignedShort()
        if (isDebug) debug("read methods count $methodsCount")

        methods = Array(methodsCount) {
            MethodInfo(this).apply {
                read(input)
            }
        }
    }

    private fun writeMethods(out: DataOutput) {
        val methodsCount = methods.size
        out.writeShort(methodsCount)
        if (isDebug) debug("wrote methods count $methodsCount")

        methods.forEach { it.write(out) }
    }

    private fun checkMajorVersion(majorVersion: Int) {
        if (majorVersion < 45 || majorVersion > 53) {
            warning("major version should be between 45 and 53 for JDK <= 1.9, was $majorVersion")
        }
    }

    companion object {
        private val MAGIC_NUMBER = 0xCAFEBABE.toInt()
    }
}
