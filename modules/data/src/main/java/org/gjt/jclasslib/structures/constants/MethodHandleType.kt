/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.constants

import org.gjt.jclasslib.structures.InvalidByteCodeException

enum class MethodHandleType(val tag: Int, val verbose: String) {

    GET_FIELD(1, "REF_getField"),
    GET_STATIC(2, "REF_getStatic"),
    PUT_FIELD(3, "REF_putField"),
    PUT_STATIC(4, "REF_putStatic"),
    INVOKE_VIRTUAL(5, "REF_invokeVirtual"),
    INVOKE_STATIC(6, "REF_invokeStatic"),
    INVOKE_SPECIAL(7, "REF_invokeSpecial"),
    NEW_INVOKE_SPECIAL(8, "REF_newInvokeSpecial"),
    INVOKE_INTERFACE(9, "REF_invokeInterface");

    companion object {
        private val LOOKUP = arrayOfNulls<MethodHandleType>(MethodHandleType.values().maxBy { it.tag }!!.tag + 1)

        init {
            for (methodHandleType in MethodHandleType.values()) {
                LOOKUP[methodHandleType.tag] = methodHandleType
            }
        }

        @JvmStatic
        @Throws(InvalidByteCodeException::class)
        fun getFromTag(tag: Byte): MethodHandleType {
            if (tag < LOOKUP.size && tag >= 0) {
                val methodHandleType = LOOKUP[tag.toInt()]
                if (methodHandleType != null) {
                    return methodHandleType
                }
            }
            throw InvalidByteCodeException("invalid method handle entry with unknown tag " + tag)
        }

    }

}