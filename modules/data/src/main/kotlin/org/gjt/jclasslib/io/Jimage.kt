/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.io

import java.io.File
import java.io.InputStream
import java.net.URI
import java.net.URLClassLoader
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

private val modulesRootsCache = mutableMapOf<File, Path>()
private val CLASSFILE_SUFFIX = ".class"

/**
 * Get an input stream to a class file in the JRE.
 * @param fileName the file name to the class file in the JRT, including the module
 * @param jreHome the home directory of the JRE
 */
fun getJrtInputStream(fileName: String, jreHome: File): InputStream {
    return Files.newInputStream(getModulesRoot(jreHome).resolve(fileName))
}

/**
 * Find a module class in the JRT (Java 9+)
 * @param className the module class name
 * @param jreHome the home directory of the JRE
 */
fun findModuleInJrt(className: String, jreHome: File): Path? {
    return getModulesRoot(jreHome).resolve(className + CLASSFILE_SUFFIX)
}
/**
 * Find a class in the JRT (Java 9+)
 * @param className the class name
 * @param jreHome the home directory of the JRE
 */
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

/**
 * Iterate over all classes in the JRT (Java 9+)
 * @param jreHome the home directory of the JRE
 * @param block the code that should be executed for each class
 */
fun forEachClassInJrt(jreHome: File, block : (path : Path) -> Unit) {
    Files.walk(getModulesRoot(jreHome)).forEach { path ->
        if (path.nameCount > 2 && !Files.isDirectory(path) && path.toString().toLowerCase().endsWith(CLASSFILE_SUFFIX)) {
            block(path)
        }
    }
}

/**
 * Iterate over all class names in the JRT (Java 9+)
 * @param jreHome the home directory of the JRE
 * @param block the code that should be executed for each class name
 */
fun forEachClassNameInJrt(jreHome: File, block : (className :String) -> Unit) {
    forEachClassInJrt(jreHome) { path ->
        if (path.endsWith("module-info.class")) {
            block(path.subpath(0, path.nameCount).toString())
        } else {
            block(path.subpath(2, path.nameCount).toString())
        }
    }
}

private fun getModulesRoot(jreHome: File): Path = modulesRootsCache.getOrPut(jreHome) {
    val classLoader = URLClassLoader(arrayOf(File(jreHome, "lib/jrt-fs.jar").toURI().toURL()))
    FileSystems.newFileSystem(URI("jrt:/"), emptyMap<String, String>(), classLoader).getPath("/modules")
}


