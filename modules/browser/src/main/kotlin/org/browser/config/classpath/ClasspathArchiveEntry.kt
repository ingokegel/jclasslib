/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import kotlinx.dom.build.addElement
import org.w3c.dom.Element
import java.io.IOException
import java.util.jar.JarFile
import javax.swing.tree.DefaultTreeModel

class ClasspathArchiveEntry(fileName : String) : ClasspathEntry(fileName) {

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
                val entry = jarFile.getJarEntry(fileName)
                if (entry != null) {
                    return FindResult(file.path + "!" + fileName, moduleName)
                }
            } catch (e: IOException) {
            }
        }
        return null
    }

    override fun mergeClassesIntoTree(classPathModel: DefaultTreeModel, modulePathModel: DefaultTreeModel, reset: Boolean) {
        try {
            JarFile(file).use { jarFile ->
                jarFile.entries().iterator().forEach {
                    if (!it.isDirectory && it.name.toLowerCase().endsWith(CLASSFILE_SUFFIX)) {
                        addEntry(it.name, moduleName, classPathModel, modulePathModel, reset)
                    }
                }
            }
        } catch (ex: IOException) {
        }
    }

    companion object {
        const val NODE_NAME = "archive"
        private const val ATTRIBUTE_PATH = "path"

        fun create(element: Element) = ClasspathArchiveEntry(element.getAttribute(ATTRIBUTE_PATH))
    }
}
