/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures

import org.gjt.jclasslib.structures.constants.*

enum class ConstantType(val tag: Int, val verbose: String, val size : Int) {

    CLASS(7, "CONSTANT_Class_info", 2) {
        override fun create(): CPInfo = ConstantClassInfo()
    },
    FIELDREF(9, "CONSTANT_Fieldref_info", 4) {
        override fun create(): CPInfo = ConstantFieldrefInfo()
    },
    METHODREF(10, "CONSTANT_Methodref_info", 4) {
        override fun create(): CPInfo = ConstantMethodrefInfo()
    },
    INTERFACE_METHODREF(11, "CONSTANT_InterfaceMethodref_info", 4) {
        override fun create(): CPInfo = ConstantInterfaceMethodrefInfo()
    },
    STRING(8, "CONSTANT_String_info", 2) {
        override fun create(): CPInfo = ConstantStringInfo()
    },
    INTEGER(3, "CONSTANT_Integer_info", 4) {
        override fun create(): CPInfo = ConstantIntegerInfo()
    },
    FLOAT(4, "CONSTANT_Float_info", 4) {
        override fun create(): CPInfo = ConstantFloatInfo()
    },
    LONG(5, "CONSTANT_Long_info", 8) {
        override fun create(): CPInfo = ConstantLongInfo()
    },
    DOUBLE(6, "CONSTANT_Double_info", 8) {
        override fun create(): CPInfo = ConstantDoubleInfo()
    },
    NAME_AND_TYPE(12, "CONSTANT_NameAndType_info", 4) {
        override fun create(): CPInfo = ConstantNameAndTypeInfo()
    },
    METHOD_TYPE(16, "CONSTANT_MethodType_info", 2) {
        override fun create(): CPInfo = ConstantMethodTypeInfo()
    },
    METHOD_HANDLE(15, "CONSTANT_MethodHandle_info", 3) {
        override fun create(): CPInfo = ConstantMethodHandleInfo()
    },
    INVOKE_DYNAMIC(18, "CONSTANT_InvokeDynamic_info", 4) {
        override fun create(): CPInfo = ConstantInvokeDynamicInfo()
    },
    UTF8(1, "CONSTANT_Utf8_info", 0) {
        override fun create(): CPInfo = ConstantUtf8Info()
    };

    abstract fun create() : CPInfo

    val extraEntryCount: Int
        get() = when(this) {
            LONG, DOUBLE -> 1
            else -> 0
        }

    override fun toString(): String = verbose

    companion object {
        private val LOOKUP = arrayOfNulls<ConstantType>(values().maxBy { it.tag }!!.tag + 1)

        init {
            for (constantType in values()) {
                LOOKUP[constantType.tag] = constantType
            }
        }

        @JvmStatic
        @Throws(InvalidByteCodeException::class)
        fun getFromTag(tag: Byte): ConstantType {
            if (tag < LOOKUP.size && tag >= 0) {
                val constantType = LOOKUP[tag.toInt()]
                if (constantType != null) {
                    return constantType
                }
            }
            throw InvalidByteCodeException("invalid constant pool entry with unknown tag " + tag)
        }
    }

}
