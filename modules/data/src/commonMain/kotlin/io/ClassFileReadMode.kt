/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.io

/**
 * The mode for reading class files.
 */
enum class ClassFileReadMode {
    /** Read the full class file with all attributes. */
    FULL,
    /** Skip reading all attributes for improved performance. */
    SKIP_ATTRIBUTES
}