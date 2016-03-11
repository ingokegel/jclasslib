/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.io

import java.io.File
import java.net.URI
import java.net.URLClassLoader
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

private val modulesRootsCache = mutableMapOf<File, Path>()
private val CLASSFILE_SUFFIX = ".class"

fun getJrtInputStream(fileName: String, jreHome: File) = Files.newInputStream(getModulesRoot(jreHome).resolve(fileName))

fun findClassInJrt(className: String, jreHome: File): Path? {
    val fileName = className.replace('.', '/') + CLASSFILE_SUFFIX
    Files.newDirectoryStream(getModulesRoot(jreHome)).forEach { module ->
        val path = module.resolve(fileName)
        if (Files.exists(path)) {
            return path
        }
    }
    return null
}

fun forEachClassInJrt(jreHome: File, block : (path : Path) -> Unit) {
    Files.walk(getModulesRoot(jreHome)).forEach { path ->
        if (path.nameCount > 2 && !Files.isDirectory(path) && path.toString().toLowerCase().endsWith(CLASSFILE_SUFFIX)) {
            block(path)
        }
    }
}

fun forEachClassNameInJrt(jreHome: File, block : (className :String) -> Unit) {
    forEachClassInJrt(jreHome) { path ->
        block(path.subpath(2, path.nameCount).toString())
    }
}

private fun getModulesRoot(jreHome: File) = modulesRootsCache.getOrPut(jreHome) {
    val classLoader = URLClassLoader(arrayOf(File(jreHome, "jrt-fs.jar").toURI().toURL()))
    FileSystems.newFileSystem(URI("jrt:/"), null, classLoader).getPath("/modules")
}


