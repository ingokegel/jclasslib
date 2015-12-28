/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures

import org.gjt.jclasslib.structures.constants.*

/**
 * Utility methods for working on the constant pool of a ClassFile
 * object.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com)
 */

object ConstantPoolUtil {

    /**
     * Add a ConstantMethodRef constant pool entry to the constant pool
     * of a ClassFile.
     * @param classFile the class file whose constant pool is to be edited
     * @param className the name of the referenced class
     * @param methodName the name of the referenced method
     * @param methodSignature the signature of the referenced method
     * @param sizeDelta the minimum increment by which the array holding the
     * constant pool is to be enlarged. Set to 0 if unsure.
     * @return the constant pool index of the added ConstantMethodRef
     */
    fun addConstantMethodrefInfo(classFile: ClassFile, className: String, methodName: String, methodSignature: String): Int {
        val classIndex = addConstantClassInfo(classFile, className)
        val nameAndTypeIndex = addConstantNameAndTypeInfo(classFile, methodName, methodSignature)

        val methodrefInfo = ConstantMethodrefInfo().apply {
            this.classFile = classFile
            this.classIndex = classIndex
            this.nameAndTypeIndex = nameAndTypeIndex

        }
        return addConstantPoolEntry(classFile, methodrefInfo)
    }

    /**
     * Add a ConstantFieldRef constant pool entry to the constant pool
     * of a ClassFile.
     * @param classFile the class file whose constant pool is to be edited
     * @param className the name of the referenced class
     * @param fieldName the name of the referenced field
     * @param fieldType the type of the referenced field
     * @param sizeDelta the minimum increment by which the array holding the
     * constant pool is to be enlarged. Set to 0 if unsure.
     * @return the constant pool index of the added ConstantMethodRef
     */
    fun addConstantFieldrefInfo(classFile: ClassFile, className: String, fieldName: String, fieldType: String): Int {
        val classIndex = addConstantClassInfo(classFile, className)
        val nameAndTypeIndex = addConstantNameAndTypeInfo(classFile, fieldName, fieldType)

        val fieldrefInfo = ConstantFieldrefInfo().apply {
            this.classFile = classFile
            this.classIndex = classIndex
            this.nameAndTypeIndex = nameAndTypeIndex
        }
        return addConstantPoolEntry(classFile, fieldrefInfo)
    }

    /**
     * Add a ConstantNameAndTypeInfo constant pool entry to the
     * constant pool of a ClassFile.
     * @param classFile the class file whose constant pool is to be edited
     * @param name the name
     * @param descriptor the descriptor
     * @param sizeDelta the minimum increment by which the array holding the
     * constant pool is to be enlarged. Set to 0 if unsure.
     * @return the constant pool index of the added ConstantNameAndTypeInfo
     */
    fun addConstantNameAndTypeInfo(classFile: ClassFile, name: String, descriptor: String): Int {
        val nameIndex = addConstantUTF8Info(classFile, name)
        val descriptorIndex = addConstantUTF8Info(classFile, descriptor)

        val nameAndTypeInfo = ConstantNameAndTypeInfo().apply {
            this.classFile = classFile
            this.nameIndex = nameIndex
            this.descriptorIndex = descriptorIndex

        }
        return addConstantPoolEntry(classFile, nameAndTypeInfo)
    }

    /**
     * Add a ConstantClassInfo constant pool entry to the
     * constant pool of a ClassFile.
     * @param classFile the class file whose constant pool is to be edited
     * @param className the name of the referenced class
     * @param sizeDelta the minimum increment by which the array holding the
     * constant pool is to be enlarged. Set to 0 if unsure.
     * @return the constant pool index of the added ConstantClassInfo
     */
    fun addConstantClassInfo(classFile: ClassFile, className: String): Int {
        val nameIndex = addConstantUTF8Info(classFile, className)

        val classInfo = ConstantClassInfo().apply {
            this.classFile = classFile
            this.nameIndex = nameIndex
        }
        return addConstantPoolEntry(classFile, classInfo)
    }

    /**
     * Add a ConstantUTF8Info constant pool entry to the
     * constant pool of a ClassFile.
     * @param classFile the class file whose constant pool is to be edited
     * @param string the string
     * @param sizeDelta the minimum increment by which the array holding the
     * constant pool is to be enlarged. Set to 0 if unsure.
     * @return the constant pool index of the added ConstantUTF8Info
     */
    fun addConstantUTF8Info(classFile: ClassFile, string: String): Int {
        val utf8Info = ConstantUtf8Info().apply {
            this.classFile = classFile
            this.string = string
        }
        return addConstantPoolEntry(classFile, utf8Info)
    }

    /**
     * Add a constant pool entry to the
     * constant pool of a ClassFile.
     * @param classFile the class file whose constant pool is to be edited
     * @param newEntry the new constant pool entry
     * @param sizeDelta the minimum increment by which the array holding the
     * constant pool is to be enlarged. Set to 0 if unsure.
     * @return the constant pool index of the added constant pool entry
     */
    fun addConstantPoolEntry(classFile: ClassFile, newEntry: CPInfo): Int {
        val constantPool = classFile.constantPool
        val index = classFile.getConstantPoolIndex(newEntry)
        if (index > -1) {
            return index
        }

        val newConstantPool = Array(constantPool.size + 1) {i ->
            when (i) {
                constantPool.size -> newEntry
                else -> constantPool[i]
            }
        }
        classFile.enlargeConstantPool(newConstantPool)
        return constantPool.size
    }

}
