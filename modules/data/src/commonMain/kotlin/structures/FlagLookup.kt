/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.structures

abstract class FlagLookup<T> where T : Enum<T>, T: ClassFileFlag {
    fun decompose(flags: Int, validFlags: Set<T>): List<T> = validFlags.filter { flags and it.flag == it.flag }
    fun composeFrom(flags: Iterable<T>): Int = flags.fold(0) { acc, flag -> acc or flag.flag }
}