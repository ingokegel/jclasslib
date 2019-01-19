/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.constants

import org.gjt.jclasslib.structures.ClassFileEnum
import org.gjt.jclasslib.structures.Lookup

/**
 * Lists all types of [ConstantMethodHandleInfo] constant pool entries.
 */
@Suppress("NOT_DOCUMENTED")
enum class MethodHandleType(override val tag: Int, val verbose: String) : ClassFileEnum {

    GET_FIELD(1, "REF_getField"),
    GET_STATIC(2, "REF_getStatic"),
    PUT_FIELD(3, "REF_putField"),
    PUT_STATIC(4, "REF_putStatic"),
    INVOKE_VIRTUAL(5, "REF_invokeVirtual"),
    INVOKE_STATIC(6, "REF_invokeStatic"),
    INVOKE_SPECIAL(7, "REF_invokeSpecial"),
    NEW_INVOKE_SPECIAL(8, "REF_newInvokeSpecial"),
    INVOKE_INTERFACE(9, "REF_invokeInterface");

    companion object : Lookup<MethodHandleType>(MethodHandleType::class.java, "method handle entry")

}