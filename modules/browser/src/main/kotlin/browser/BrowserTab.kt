/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import kotlinx.dom.build.addElement
import org.gjt.jclasslib.browser.config.BrowserPath
import org.gjt.jclasslib.browser.config.classpath.ClasspathEntry
import org.gjt.jclasslib.browser.config.classpath.ClasspathJrtEntry
import org.gjt.jclasslib.browser.config.classpath.FindResult
import org.gjt.jclasslib.io.ClassFileReader
import org.gjt.jclasslib.io.ClassFileWriter
import org.gjt.jclasslib.io.getJrtInputStream
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.util.GUIHelper
import org.w3c.dom.Element
import java.awt.BorderLayout
import java.io.EOFException
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.jar.JarFile
import javax.swing.Action
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.SwingUtilities

class BrowserTab(val fileName: String, val moduleName: String, frame: BrowserFrame) : JPanel(), BrowserServices {

    private val tabbedPane: BrowserTabbedPane
        get() = SwingUtilities.getAncestorOfClass(BrowserTabbedPane::class.java, this) as BrowserTabbedPane

    private val frameContent: FrameContent
        get() = tabbedPane.container

    private val parentFrame: BrowserFrame
        get() = frameContent.frame

    override var classFile: ClassFile = readClassFile(frame)
    override val browserComponent: BrowserComponent = BrowserComponent(this)

    override fun activate() {
        tabbedPane.focus()
        // force sync of toolbar state with this tab
    }

    override val backwardAction: Action
        get() = parentFrame.backwardAction

    override val forwardAction: Action
        get() = parentFrame.forwardAction

    override fun openClassFile(className: String, browserPath: BrowserPath?) {
        val findResult: FindResult? = findClass(className)
        if (findResult != null) {
            val openTab = frameContent.findTab(findResult.fileName)
            if (openTab != null) {
                openTab.apply {
                    select()
                    browserComponent.browserPath = browserPath
                }
            } else {
                try {
                    tabbedPane.addTab(findResult.fileName, findResult.moduleName, browserPath)
                } catch (e: IOException) {
                    GUIHelper.showMessage(parentFrame, e.message, JOptionPane.ERROR_MESSAGE)
                }

            }
        }
    }

    fun saveClassToDirectory(directory: File) : Boolean {
        val simpleClassName = classFile.simpleClassName
        val packageDirectory = File(directory, classFile.thisClassName.removeSuffix(simpleClassName))
        packageDirectory.mkdirs()
        val file = File(packageDirectory, "$simpleClassName.class")
        return try {
            ClassFileWriter.writeToFile(file, classFile)
            true
        } catch (e: Exception) {
            GUIHelper.showMessage(parentFrame, "Could not save file " + file.path + "\n\nError message; " + e.message, JOptionPane.ERROR_MESSAGE)
            false
        }
    }

    private tailrec fun findClass(className: String): FindResult? = parentFrame.config.findClass(className, false) ?:
            if (GUIHelper.showOptionDialog(parentFrame,
                    "The class $className could not be found.\nYou can check your classpath configuration and try again.",
                    arrayOf("Setup classpath", "Cancel"),
                    JOptionPane.WARNING_MESSAGE) != 0) {
                null
            } else {
                parentFrame.setupClasspathAction()
                findClass(className)
            }

    init {
        layout = BorderLayout()
        add(browserComponent, BorderLayout.CENTER)
    }

    fun reload() {
        classFile = readClassFile(parentFrame)
        browserComponent.rebuild()
    }

    private fun select() {
        tabbedPane.selectedComponent = this
        tabbedPane.focus()
        browserComponent.treePane.tree.requestFocus()
    }

    override fun canOpenClassFiles(): Boolean = true

    override fun showURL(urlSpec: String) {
        GUIHelper.showURL(urlSpec)
    }

    fun setBrowserPath(browserPath: BrowserPath?) {
        browserComponent.browserPath = browserPath
    }

    private fun readClassFile(frame: BrowserFrame, suppressEOF: Boolean = false): ClassFile {
        try {
            return when {
                fileName.startsWith(ClasspathJrtEntry.JRT_PREFIX) -> {
                    ClassFileReader.readFromInputStream(getJrtInputStream(fileName.removePrefix(ClasspathJrtEntry.JRT_PREFIX), File(frame.config.jreHome)), suppressEOF)
                }
                fileName.contains('!') -> {
                    val (jarFileName, classFileName) = fileName.split("!", limit = 2)
                    val jarFile = JarFile(jarFileName)
                    val jarEntry = jarFile.getJarEntry(classFileName)
                    if (jarEntry != null) {
                        ClassFileReader.readFromInputStream(jarFile.getInputStream(jarEntry), suppressEOF)
                    } else {
                        throw IOException("The jar entry $classFileName was not found")
                    }
                }
                else -> {
                    ClassFileReader.readFromFile(File(fileName), suppressEOF)
                }
            }
        } catch (ex: FileNotFoundException) {
            throw IOException("The file $fileName was not found")
        } catch (ex: EOFException) {
            if (GUIHelper.showOptionDialog(this,
                "An unexpected end-of-file occurred while reading $fileName. Should the file be read anyway?",
                GUIHelper.YES_NO_OPTIONS,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                return readClassFile(frame, suppressEOF = true)
            } else {
                throw IOException("An (expected) EOF occurred while reading $fileName")
            }
        } catch (ex: IOException) {
            ex.printStackTrace()
            throw IOException("An error occurred while reading $fileName")
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw IOException("The file $fileName does not seem to contain a class file")
        }
    }

    fun saveWorkspace(element: Element) {
        element.addElement(NODE_NAME) {
            setAttribute(ATTRIBUTE_FILE_NAME, fileName)
            setAttribute(ATTRIBUTE_MODULE_NAME, moduleName)
            browserComponent.browserPath?.saveWorkspace(this)
        }
    }

    companion object {
        const val NODE_NAME = "tab"
        private const val ATTRIBUTE_FILE_NAME = "fileName"
        private const val ATTRIBUTE_MODULE_NAME = "moduleName"

        fun create(element: Element, frame: BrowserFrame): BrowserTab =
                BrowserTab(element.getAttribute(ATTRIBUTE_FILE_NAME), element.getAttributeNode(ATTRIBUTE_MODULE_NAME)?.value ?: ClasspathEntry.UNNAMED_MODULE, frame)
    }
}
