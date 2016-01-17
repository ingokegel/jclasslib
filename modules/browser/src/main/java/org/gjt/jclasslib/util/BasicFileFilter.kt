/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.util

import java.io.File
import javax.swing.filechooser.FileFilter

class BasicFileFilter(private val extensions: List<String>, description: String) : FileFilter() {
    private val description: String

    init {

        val buffer = StringBuilder(description)
        buffer.append(" (")
        for (i in extensions.indices) {
            if (i > 0) {
                buffer.append(", ")
            }
            buffer.append("*.")
            buffer.append(extensions[i])
        }
        buffer.append(")")

        this.description = buffer.toString()
    }

    constructor(extension: String, description: String) : this(listOf(extension), description)

    override fun accept(file: File): Boolean = extensions.any { extension ->
        file.isDirectory || file.name.endsWith(extension)
    }

    override fun getDescription(): String {
        return description
    }
}

