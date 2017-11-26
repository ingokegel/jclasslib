/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import java.io.File
import javax.swing.filechooser.FileFilter

class MultiFileFilter(private val extensions: List<String>, description: String) : FileFilter() {
    private val description: String = StringBuilder(description).apply {
        append(" (")
        for (i in extensions.indices) {
            if (i > 0) {
                append(", ")
            }
            append("*.")
            append(extensions[i])
        }
        append(")")
    }.toString()

    constructor(extension: String, description: String) : this(listOf(extension), description)

    override fun accept(file: File): Boolean = file.isDirectory || extensions.any { extension ->
        file.name.endsWith(extension)
    }

    override fun getDescription(): String = description
}

