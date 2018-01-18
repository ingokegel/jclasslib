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
private const val classFileSuffix = ".class"

/**
 * Get an input stream to a class file in the JRE.
 * @param fileName the file name to the class file in the JRT, including the module
 * @param jreHome the home directory of the JRE
 */
fun getJrtInputStream(fileName: String, jreHome: File): InputStream =
        Files.newInputStream(getModulesRoot(jreHome).resolve(fileName))

/**
 * Find a class with a prepended module name in the JRT (Java 9+)
 * @param moduleNameAndClassName the module name and the class name, separated by a slash
 * @param jreHome the home directory of the JRE
 */
fun findClassWithModuleNameInJrt(moduleNameAndClassName: String, jreHome: File): Path? =
        getModulesRoot(jreHome).resolve(moduleNameAndClassName + classFileSuffix)
/**
 * Find a class in the JRT (Java 9+)
 * @param className the class name
 * @param jreHome the home directory of the JRE
 */
fun findClassInJrt(className: String, jreHome: File): Path? {
    val fileName = className.replace('.', '/') + classFileSuffix
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
        if (path.nameCount > 2 && !Files.isDirectory(path) && path.toString().toLowerCase().endsWith(classFileSuffix)) {
            block(path)
        }
    }
}

/**
 * Iterate over all class names in the JRT (Java 9+)
 * @param jreHome the home directory of the JRE
 * @param block the code that should be executed for each class name
 */
fun forEachClassNameInJrt(jreHome: File, block : (moduleName: String, className :String) -> Unit) {
    forEachClassInJrt(jreHome) { path ->
        block(path.getName(1).toString(), path.subpath(2, path.nameCount).toString())
    }
}

private fun getModulesRoot(jreHome: File): Path = modulesRootsCache.getOrPut(jreHome) {
    val classLoader = URLClassLoader(arrayOf(File(jreHome, "lib/jrt-fs.jar").toURI().toURL()))
    FileSystems.newFileSystem(URI("jrt:/"), emptyMap<String, String>(), classLoader).getPath("/modules")
}


