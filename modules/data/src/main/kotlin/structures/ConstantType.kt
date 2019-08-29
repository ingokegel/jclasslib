/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures

import org.gjt.jclasslib.structures.constants.*
import java.io.DataInput

/**
 * Describes all different constant types in the constant pool of a class file.
 * @property verbose the name of the constant type as used in the class file format spec
 * @property size the fixed extra size of the constant pool entry
 */
enum class ConstantType(override val tag: Int, val verbose: String, val size: Int) : ClassFileEnum {
    /**
     * See [ConstantClassInfo]
     */
    CLASS(7, "CONSTANT_Class_info", 2) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantClassInfo(classFile)
    },
    /**
     * See [ConstantFieldrefInfo]
     */
    FIELDREF(9, "CONSTANT_Fieldref_info", 4) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantFieldrefInfo(classFile)
    },
    /**
     * See [ConstantMethodrefInfo]
     */
    METHODREF(10, "CONSTANT_Methodref_info", 4) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantMethodrefInfo(classFile)
    },
    /**
     * See [ConstantInterfaceMethodrefInfo]
     */
    INTERFACE_METHODREF(11, "CONSTANT_InterfaceMethodref_info", 4) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantInterfaceMethodrefInfo(classFile)
    },
    /**
     * See [ConstantStringInfo]
     */
    STRING(8, "CONSTANT_String_info", 2) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantStringInfo(classFile)
    },
    /**
     * See [ConstantIntegerInfo]
     */
    INTEGER(3, "CONSTANT_Integer_info", 4) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantIntegerInfo(classFile)
    },
    /**
     * See [ConstantFloatInfo]
     */
    FLOAT(4, "CONSTANT_Float_info", 4) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantFloatInfo(classFile)
    },
    /**
     * See [ConstantLongInfo]
     */
    LONG(5, "CONSTANT_Long_info", 8) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantLongInfo(classFile)
    },
    /**
     * See [ConstantDoubleInfo]
     */
    DOUBLE(6, "CONSTANT_Double_info", 8) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantDoubleInfo(classFile)
    },
    /**
     * See [ConstantNameAndTypeInfo]
     */
    NAME_AND_TYPE(12, "CONSTANT_NameAndType_info", 4) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantNameAndTypeInfo(classFile)
    },
    /**
     * See [ConstantMethodTypeInfo]
     */
    METHOD_TYPE(16, "CONSTANT_MethodType_info", 2) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantMethodTypeInfo(classFile)
    },
    /**
     * See [ConstantMethodHandleInfo]
     */
    METHOD_HANDLE(15, "CONSTANT_MethodHandle_info", 3) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantMethodHandleInfo(classFile, input)
    },
    /**
     * See [ConstantInvokeDynamicInfo]
     */
    INVOKE_DYNAMIC(18, "CONSTANT_InvokeDynamic_info", 4) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantInvokeDynamicInfo(classFile)
    },
    /**
     * See [ConstantUtf8Info]
     */
    UTF8(1, "CONSTANT_Utf8_info", 0) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantUtf8Info(classFile)
    },
    /**
     * See [ConstantModuleInfo]
     */
    MODULE(19, "CONSTANT_Module_info", 2) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantModuleInfo(classFile)
    },
    /**
     * See [ConstantPackageInfo]
     */
    PACKAGE(20, "CONSTANT_Package_info", 2) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantPackageInfo(classFile)
    },
    /**
     * See [ConstantDynamicInfo]
     */
    DYNAMIC(17, "CONSTANT_Dynamic_info", 4) {
        override fun create(classFile: ClassFile, input: DataInput): Constant = ConstantDynamicInfo(classFile)
    };


    /**
     * Read the corresponding constant pool structure from the input stream.
     * @param classFile the class file of which this structure is part of
     * @param input the input stream from which to read the structure.
     */
    fun read(classFile: ClassFile, input: DataInput): Constant {
        return create(classFile, input).apply {
            if (this !is ConstantMethodHandleInfo) {
                read(input)
            }
        }
    }

    /**
     * @suppress
     */
    protected abstract fun create(classFile: ClassFile, input: DataInput): Constant

    /**
     * Return the extra number of unused constant pool entries following this type of constant pool entry.
     */
    val extraEntryCount: Int
        get() = when (this) {
            LONG, DOUBLE -> 1
            else -> 0
        }

    /**
     * Verbose description of the constant type.
     */
    override fun toString(): String = verbose

    @Suppress("NOT_DOCUMENTED")
    companion object : Lookup<ConstantType>(ConstantType::class.java, "constant pool type")

}
