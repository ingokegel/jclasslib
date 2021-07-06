/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.config.classpath

import java.io.File

abstract class ClasspathFileEntry(fileName : String) : ClasspathEntry() {

    val file: File = File(fileName).canonicalFile

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other == null || other::class.java != this::class.java) {
            return false
        }
        other as ClasspathFileEntry

        return file == other.file
    }

    override fun hashCode(): Int = file.hashCode()
}