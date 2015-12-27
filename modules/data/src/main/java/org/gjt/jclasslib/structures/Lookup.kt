/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures


abstract class Lookup<T>(
    val enumClass : Class<T>,
    val name : String

) where T : Enum<T>, T : ClassFileEnum {

    private val LOOKUP = arrayOfNulls<Any?>(enumClass.enumConstants.maxBy { it.tag }!!.tag + 1)

    init {
        for (constant in enumClass.enumConstants) {
            LOOKUP[constant.tag] = constant
        }
    }

    @Throws(InvalidByteCodeException::class)
    fun getFromTag(tag: Int): T {
        if (tag < LOOKUP.size && tag >= 0) {
            val constant = LOOKUP[tag.toInt()]
            if (constant != null) {
                @Suppress("UNCHECKED_CAST")
                return constant as T
            }
        }
        throw InvalidByteCodeException("Invalid $name: $tag")
    }
}