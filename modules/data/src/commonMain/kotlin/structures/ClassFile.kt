/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.structures

import org.gjt.jclasslib.io.DataInput
import org.gjt.jclasslib.io.DataOutput
import org.gjt.jclasslib.structures.constants.ConstantClassInfo
import org.gjt.jclasslib.structures.constants.ConstantPlaceholder
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info
import kotlin.reflect.KClass

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
                constantPoolEntryToIndex[cpInfo] = i
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

    /***
     * Returns the constant that holds the name of this class.
      */
    val thisClassConstant: ConstantClassInfo get() = getConstantPoolEntry(thisClass, ConstantClassInfo::class)

    /**
     * Constant pool index of the super class of this class.
     */
    var superClass: Int = 0

    /***
     * Returns the constant that holds the name of the super class.
     */
    val superClassConstant: ConstantClassInfo get() = getConstantPoolEntry(superClass, ConstantClassInfo::class)

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
        get() =
            KnownMajorJavaVersions.entries.firstOrNull { it.majorVersion == majorVersion }?.verbose ?: "unknown value $majorVersion"

    /**
     * Index of an equivalent constant pool entry, or -1 if no equivalent constant pool entry can be found.
     *
     * @param constant the constant pool entry
     */
    fun getConstantPoolIndex(constant: Constant): Int = constantPoolEntryToIndex[constant] ?: -1

    /**
     * Set all constant pool entries where the new array
     * of constant pool entries must start with the old constant pool. If
     * you delete entries, use setConstantPool.
     * @param enlargedConstantPool the enlarged constant pool
     */
    fun enlargeConstantPool(enlargedConstantPool: Array<Constant>) {
        for (i in constantPool.size until enlargedConstantPool.size) {
            constantPoolEntryToIndex[enlargedConstantPool[i]] = i
        }
        this.constantPool = enlargedConstantPool
    }

    /**
     * Register the constant pool entry at a given index so that it can
     * be found through the getConstantPoolIndex method.
     *
     * @param index the index
     */
    fun registerConstantPoolEntry(index: Int) {
        constantPoolEntryToIndex[constantPool[index]] = index
    }

    /**
     * Unregister the constant pool entry at a given index so that it can
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
        get() = getConstantPoolEntryName(superClass)

    /**
     * The access flags of this class as a hex string.
     */
    val formattedAccessFlags: String
        get() = formatFlags(accessFlags)

    /**
     * The verbose description for the access flags of this class.
     */
    val accessFlagsVerbose: String
        get() = formatFlagsVerbose(AccessFlag.CLASS_ACCESS_FLAGS, accessFlags)

    /**
     * The ConstantUtf8Info constant pool entry at the specified index.
     * @param index the index
     */
    fun getConstantPoolUtf8Entry(index: Int): ConstantUtf8Info =
            getConstantPoolEntry(index, ConstantUtf8Info::class)

    /**
     * Get the constant pool entry at the specified index and cast it to a specified class.
     * @param index the index
     * @param entryClass the required subtype of CPInfo
     */
    @Suppress("UNCHECKED_CAST")
    fun <T : Constant> getConstantPoolEntry(index: Int, entryClass: KClass<T>): T {
        checkValidConstantPoolIndex(index)

        val cpInfo = constantPool[index]
        return if (entryClass.isInstance(cpInfo)) {
            cpInfo as T
        } else {
            throw InvalidByteCodeException("constant pool entry at $index of class ${cpInfo::class} is not assignable to $entryClass")
        }
    }

    /**
     * Get an approximate verbose description for the content of the constant pool entry
     * at the specified index.
     *
     * @param index the index
     */
    fun getConstantPoolEntryName(index: Int): String {
        checkValidConstantPoolIndex(index)
        return constantPool[index].verbose
    }

    /**
     * Get the index of a field for the given field name and signature or -1 if not found.
     * @param name       the field name.
     * @param descriptor the signature.
     */
    fun getFieldIndex(name: String, descriptor: String): Int {

        fields.forEachIndexed { i, fieldInfo ->
            if (fieldInfo.name == name && fieldInfo.descriptor == descriptor) {
                return i
            }
        }
        return -1
    }

    /**
     * Get the FieldInfo for the given field name and signature or null if not found.
     * @param name       the field name.
     * @param descriptor the signature.
     */
    fun getField(name: String, descriptor: String): FieldInfo? {
        val index = getFieldIndex(name, descriptor)
        return if (index < 0) null else fields[index]
    }

    /**
     * Get the index of a method for the given method name and signature or -1 if not found.
     * @param name       the method name.
     * @param descriptor the signature.
     */
    fun getMethodIndex(name: String, descriptor: String): Int {
        methods.forEachIndexed { i, methodInfo ->
            if (methodInfo.name == name && methodInfo.descriptor == descriptor) {
                return i
            }
        }
        return -1
    }

    /**
     * Get the MethodInfo for the given method name and signature or null if not found.
     * @param name       the method name.
     * @param descriptor the signature.
     */
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
            throw InvalidByteCodeException("Constant pool index $index is out of bounds [1, ${constantPool.size - 1}]")
        }
    }

    private fun readMagicNumber(input: DataInput) {
        val magicNumber = input.readInt()
        if (magicNumber != MAGIC_NUMBER) {
            throw InvalidByteCodeException("Invalid magic number ${magicNumber.hex} instead of ${MAGIC_NUMBER.hex}")
        }

        if (isDebug) debug("read magic number", input)
    }

    private fun writeMagicNumber(output: DataOutput) {
        output.writeInt(MAGIC_NUMBER)
        if (isDebug) debug("wrote magic number", output)
    }

    private fun readVersion(input: DataInput) {
        minorVersion = input.readUnsignedShort()
        if (isDebug) debug("read minor version $minorVersion", input)

        majorVersion = input.readUnsignedShort()
        if (isDebug) debug("read major version $majorVersion", input)

        checkMajorVersion(majorVersion)
    }

    private fun writeVersion(output: DataOutput) {
        output.writeShort(minorVersion)
        if (isDebug) debug("wrote minor version $minorVersion", output)

        output.writeShort(majorVersion)
        if (isDebug) debug("wrote major version $majorVersion", output)

        checkMajorVersion(majorVersion)
    }

    private fun readConstantPool(input: DataInput) {
        constantPoolEntryToIndex.clear()
        val constantPoolCount = input.readUnsignedShort()
        if (isDebug) debug("read constant pool count $constantPoolCount", input)

        // constantPool[0] is not used
        var placeholderIndex = 0
        constantPool = Array(constantPoolCount) { i ->
            when (i) {
                placeholderIndex -> ConstantPlaceholder
                else -> {
                    if (isDebug) debug("reading constant pool entry $i", input)
                    val constantType = ConstantType.getFromTag(input.readByte().toInt())
                    constantType.read(this, input).apply {
                        constantPoolEntryToIndex[this] = i
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

    private fun writeConstantPool(output: DataOutput) {
        output.writeShort(constantPool.size)
        if (isDebug) debug("wrote constant pool count ${constantPool.size}", output)

        constantPool.forEachIndexed { i, cpInfo ->
            if (cpInfo != ConstantPlaceholder) {
                if (isDebug) debug("writing constant pool entry $i", output)
                cpInfo.write(output)
            }
        }
    }

    private fun readAccessFlags(input: DataInput) {
        accessFlags = input.readUnsignedShort()
        if (isDebug) debug("read access flags $accessFlagsVerbose", input)
    }

    private fun writeAccessFlags(output: DataOutput) {
        output.writeShort(accessFlags)
        if (isDebug) debug("wrote access flags $accessFlagsVerbose", output)
    }

    private fun readThisClass(input: DataInput) {
        thisClass = input.readUnsignedShort()
        if (isDebug) debug("read this_class index $thisClass", input)
    }

    private fun writeThisClass(output: DataOutput) {
        output.writeShort(thisClass)
        if (isDebug) debug("wrote this_class index $thisClass", output)
    }

    private fun readSuperClass(input: DataInput) {
        superClass = input.readUnsignedShort()
        if (isDebug) debug("read super_class index $superClass", input)
    }

    private fun writeSuperClass(output: DataOutput) {
        output.writeShort(superClass)
        if (isDebug) debug("wrote super_class index $superClass", output)
    }

    private fun readInterfaces(input: DataInput) {
        val interfacesCount = input.readUnsignedShort()
        if (isDebug) debug("read interfaces count $interfacesCount", input)

        interfaces = IntArray(interfacesCount) {
            val index = input.readUnsignedShort()
            if (isDebug) debug("read interface index $index", input)
            index
        }
    }

    private fun writeInterfaces(output: DataOutput) {
        val interfacesCount = interfaces.size
        output.writeShort(interfacesCount)
        if (isDebug) debug("wrote interfaces count $interfacesCount", output)

        interfaces.forEach {
            output.writeShort(it)
            if (isDebug) debug("wrote interface index $it", output)
        }
    }

    private fun readFields(input: DataInput) {
        val fieldsCount = input.readUnsignedShort()
        if (isDebug) debug("read fields count $fieldsCount", input)

        fields = Array(fieldsCount) {
            FieldInfo(this).apply {
                read(input)
            }
        }
    }

    private fun writeFields(output: DataOutput) {
        val fieldsCount = fields.size
        output.writeShort(fieldsCount)
        if (isDebug) debug("wrote fields count $fieldsCount", output)

        fields.forEach { it.write(output) }
    }

    private fun readMethods(input: DataInput) {

        val methodsCount = input.readUnsignedShort()
        if (isDebug) debug("read methods count $methodsCount", input)

        methods = Array(methodsCount) {
            MethodInfo(this).apply {
                read(input)
            }
        }
    }

    private fun writeMethods(output: DataOutput) {
        val methodsCount = methods.size
        output.writeShort(methodsCount)
        if (isDebug) debug("wrote methods count $methodsCount", output)

        methods.forEach { it.write(output) }
    }

    private fun checkMajorVersion(majorVersion: Int) {
        if (isMajorVersionWarnings && majorVersion !in MAJOR_VERSION_RANGE) {
            warning("major version should be between ${MAJOR_VERSION_RANGE.first} and ${MAJOR_VERSION_RANGE.last} for JDK <= ${KnownMajorJavaVersions.entries.last().verbose}, was $majorVersion")
        }
    }

    private enum class KnownMajorJavaVersions(val majorVersion: Int, val verbose: String) {
        V45(45, "1.1"),
        V46(46, "1.2"),
        V47(47, "1.3"),
        V48(48, "1.4"),
        V49(49, "1.5"),
        V50(50, "1.6"),
        V51(51, "1.7"),
        V52(52, "1.8"),
        V53(53, "9"),
        V54(54, "10"),
        V55(55, "11"),
        V56(56, "12"),
        V57(57, "13"),
        V58(58, "14"),
        V59(59, "15"),
        V60(60, "16"),
        V61(61, "17"),
        V62(62, "18"),
        V63(63, "19"),
        V64(64, "20"),
        V65(65, "21"),
        V66(66, "22"),
        V67(67, "23"),
        V68(68, "24"),
        V69(69, "25"),
        V70(69, "26")
    }

    companion object {
        private const val MAGIC_NUMBER = 0xCAFEBABE.toInt()
        private val MAJOR_VERSION_RANGE = KnownMajorJavaVersions.entries.first().majorVersion.rangeTo(KnownMajorJavaVersions.entries.last().majorVersion)
    }
}
