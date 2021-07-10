/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures

/**
 * Implemented by enums that represent a bit field flag.
 */
interface ClassFileFlag {
    /**
     * The flag
     */
    val flag: Int

    /**
     * if the flag is only of historical significance
     */
    val historical: Boolean

    /**
     * The verbose representation of this flag
     */
    val verbose: String
}
