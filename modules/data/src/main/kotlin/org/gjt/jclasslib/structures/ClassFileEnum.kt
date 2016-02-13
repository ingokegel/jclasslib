/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures

/**
 * Implemented by enums that have an identifying bytecode tag.
 */
interface ClassFileEnum {
    /**
     * The bytecode tag representing the enum value.
     */
    val tag: Int
}