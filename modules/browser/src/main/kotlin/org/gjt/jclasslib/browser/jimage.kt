/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.config.classpath.FindResult
import sun.misc.Unsafe
import java.io.File
import java.net.URI
import java.nio.file.FileSystems
import java.nio.file.Files
import java.nio.file.Path

val JRT_PREFIX = "jrt:"
private val IMAGES = mapOf("bootImagePath" to "bootmodules", "extImagePath" to "extmodules", "appImagePath" to "appmodules")

private val rootCache = mutableMapOf<File, Path>()

fun getJrtInputStream(fileName: String, jreHome: File) = Files.newInputStream(getJrtRoot(jreHome).resolve(fileName.removePrefix(JRT_PREFIX)))

fun findClassInJrt(className: String, jreHome: File): FindResult? {
    val fileName = className.replace('.', '/') + ".class"
    Files.newDirectoryStream(getJrtRoot(jreHome)).forEach { module ->
        val path = module.resolve(fileName)
        if (Files.exists(path)) {
            return FindResult("$JRT_PREFIX${path.toString()}")
        }
    }
    return null
}

fun enumerateJrtClasses(jreHome: File, block : (path : Path) -> Unit) {
    Files.walk(getJrtRoot(jreHome)).forEach { path ->
        if (path.nameCount > 1) {
            block(path.subpath(1, path.nameCount))
        }
    }
}

private fun getJrtRoot(jreHome: File) = rootCache.getOrPut(jreHome) {
    initSystemImage(jreHome)
    FileSystems.getFileSystem(URI("jrt:/")).getPath("/")
}

private fun initSystemImage(jreHome: File) {
    val systemImagesClass = Class.forName("jdk.internal.jrtfs.SystemImages")
    val modulesPath = jreHome.toPath().resolve("lib/modules")
    val unsafe = Unsafe::class.java.getDeclaredField("theUnsafe").run {
        isAccessible = true
        get(null) as Unsafe
    }
    for ((variableName, imageName) in IMAGES) {
        systemImagesClass.getDeclaredField(variableName).let {field ->
            unsafe.putObjectVolatile(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), modulesPath.resolve("$imageName.jimage"))
        }
    }
}

