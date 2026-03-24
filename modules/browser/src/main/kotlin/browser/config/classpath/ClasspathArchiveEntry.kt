/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import kotlinx.dom.build.addElement
import org.gjt.jclasslib.browser.BrowserFrame
import org.gjt.jclasslib.browser.ClassFileCallback
import org.gjt.jclasslib.browser.handleClassFile
import org.gjt.jclasslib.io.ClassFileReadMode
import org.w3c.dom.Element
import java.io.IOException
import java.util.jar.JarEntry
import java.util.jar.JarFile
import javax.swing.tree.DefaultTreeModel

// Some obfuscators (Caesium, Paramorphism, etc.) append "/" to class entry names,
// making them appear as directories. The JVM still loads these as class files.
fun JarFile.getJarEntryAllowingObfuscatedDirectory(name: String): JarEntry? =
    getJarEntry(name) ?: getJarEntry("$name/")

fun JarEntry.nameWithoutObfuscatedDirectorySuffix(): String = name.removeSuffix("/")

class ClasspathArchiveEntry(fileName : String) : ClasspathFileEntry(fileName) {

    private val moduleName by lazy {
        JarFile(file).use { jarFile ->
            val moduleInfoEntry = jarFile.getJarEntry(MODULE_INFO_CLASS_FILE_NAME)
            if (moduleInfoEntry != null) {
                getModuleName(jarFile.getInputStream(moduleInfoEntry))
            } else {
                null
            }
        } ?: UNNAMED_MODULE
    }

    override fun saveWorkspace(element: Element) {
        element.addElement(NODE_NAME) {
            setAttribute("path", file.path)
        }
    }

    override fun findClass(className: String, modulePathSelection: Boolean): FindResult? {
        if (!modulePathSelection || getModuleName(className) == moduleName) {
            val fileName = getClassPathClassName(className, modulePathSelection).replace('.', '/') + ".class"
            try {
                val jarFile = JarFile(file)
                val entry = jarFile.getJarEntryAllowingObfuscatedDirectory(fileName)
                if (entry != null) {
                    return createFindResult(entry)
                }
            } catch (_: IOException) {
            }
        }
        return null
    }

    private fun createFindResult(entry: JarEntry): FindResult = FindResult(file.path + "!" + entry.nameWithoutObfuscatedDirectorySuffix(), moduleName)

    override fun mergeClassesIntoTree(classPathModel: DefaultTreeModel, modulePathModel: DefaultTreeModel, reset: Boolean) {
        forEachEntry {
            addEntry(it.nameWithoutObfuscatedDirectorySuffix(), moduleName, classPathModel, modulePathModel, reset)
        }
    }

    override fun scanClassFiles(classFileCallback: ClassFileCallback, includeJdk: Boolean, frame: BrowserFrame, readMode: ClassFileReadMode) {
        forEachEntry {
            classFileCallback.handleClassFile(createFindResult(it), frame, readMode)
        }
    }

    private fun forEachEntry(handler: (jarEntry: JarEntry) -> Unit) {
        try {
            JarFile(file).use { jarFile ->
                jarFile.entries().iterator().forEach {
                    if (it.nameWithoutObfuscatedDirectorySuffix().lowercase().endsWith(CLASSFILE_SUFFIX)) {
                        handler(it)
                    }
                }
            }
        } catch (_: IOException) {
        }
    }

    companion object {
        const val NODE_NAME = "archive"
        private const val ATTRIBUTE_PATH = "path"

        fun create(element: Element) = ClasspathArchiveEntry(element.getAttribute(ATTRIBUTE_PATH))
    }
}
