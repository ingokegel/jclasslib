/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.config.classpath.FindResult
import org.gjt.jclasslib.browser.config.window.BrowserPath
import org.gjt.jclasslib.browser.config.window.WindowState
import org.gjt.jclasslib.io.ClassFileReader
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.util.GUIHelper
import java.awt.BorderLayout
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.util.jar.JarFile
import javax.swing.Action
import javax.swing.JInternalFrame
import javax.swing.JOptionPane

class BrowserInternalFrame(private val desktopManager: BrowserDesktopManager, private val fileName: String, browserPath: BrowserPath? = null) : JInternalFrame(fileName, true, true, true, true), BrowserServices {
    override val classFile: ClassFile = readClassFile()
    override val browserComponent: BrowserComponent = BrowserComponent(this)

    init {
        setFrameIcon(BrowserMDIFrame.ICON_APPLICATION_16)
        readClassFile()
        setupInternalFrame()
        if (browserPath != null) {
            browserComponent.browserPath = browserPath
        }
    }

    // TODO remove this when kotlin does not complain about a missing override anymore
    override fun setLayer(layer: Int?) {
        super.setLayer(layer)
    }

    fun createWindowState(): WindowState = WindowState(fileName, browserComponent.browserPath)


    // Browser services

    override fun activate() {
        // force sync of toolbar state with this frame
        desktopManager.desktopPane.selectedFrame = this
    }

    override val backwardAction: Action
        get() = parentFrame.backwardAction

    override val forwardAction: Action
        get() = parentFrame.forwardAction

    override fun openClassFile(className: String, browserPath: BrowserPath) {
        var findResult: FindResult? = parentFrame.config.findClass(className)
        while (findResult == null) {
            if (GUIHelper.showOptionDialog(parentFrame,
                    "The class $className could not be found.\nYou can check your classpath configuration and try again.",
                    arrayOf("Setup classpath", "Cancel"),
                    JOptionPane.WARNING_MESSAGE) == 0)
            {
                parentFrame.setupClasspathAction()
                findResult = parentFrame.config.findClass(className)
            } else {
                return
            }
        }

        val openFrame = desktopManager.getOpenFrame(WindowState(findResult.fileName))
        if (openFrame != null) {
            openFrame.apply {
                setSelected(true)
                browserComponent.browserPath = browserPath
                desktopManager.scrollToVisible(this)
            }
        } else {
            try {
                BrowserInternalFrame(desktopManager, findResult.fileName, browserPath).apply {
                    if (isMaximum()) {
                        setMaximum(true)
                    } else {
                        desktopManager.scrollToVisible(this)
                    }
                }
            } catch (e: IOException) {
                GUIHelper.showMessage(desktopManager.parentFrame, e.message, JOptionPane.ERROR_MESSAGE)
            }

        }
    }

    override fun canOpenClassFiles(): Boolean = true

    override fun showURL(urlSpec: String) {
        GUIHelper.showURL(urlSpec)
    }

    fun reload() {
        readClassFile()
        browserComponent.rebuild()
    }

    private fun setupInternalFrame() {
        setTitle(fileName)

        val contentPane = contentPane
        contentPane.layout = BorderLayout()

        contentPane.add(browserComponent, BorderLayout.CENTER)

        bounds = desktopManager.nextInternalFrameBounds

        addVetoableChangeListener(desktopManager)
        addInternalFrameListener(desktopManager)
        desktopManager.addInternalFrame(this)

        if (desktopManager.parentFrame.isVisible) {
            isVisible = true
        }
    }

    private val parentFrame: BrowserMDIFrame
        get() = desktopManager.parentFrame

    private fun readClassFile() : ClassFile {
        try {
            val index = fileName.indexOf('!')
            if (index > -1) {
                val jarFileName = fileName.substring(0, index)
                val classFileName = fileName.substring(index + 1)
                val jarFile = JarFile(jarFileName)
                val jarEntry = jarFile.getJarEntry(classFileName)
                if (jarEntry != null) {
                    return ClassFileReader.readFromInputStream(jarFile.getInputStream(jarEntry))
                } else {
                    throw IOException("The jar entry $classFileName was not found")
                }
            } else {
                return ClassFileReader.readFromFile(File(fileName))
            }
        } catch (ex: FileNotFoundException) {
            throw IOException("The file $fileName was not found")
        } catch (ex: IOException) {
            throw IOException("An error occurred while reading " + fileName)
        } catch (ex: Exception) {
            ex.printStackTrace()
            throw IOException("The file $fileName does not seem to contain a class file")
        }
    }
}
