/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.util

import com.formdev.flatlaf.util.UIScale
import com.install4j.api.Util
import com.install4j.runtime.filechooser.AbstractFileSystemChooser
import java.awt.Window
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JTree
import javax.swing.SwingUtilities
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.TreePath
import kotlin.math.roundToInt

object GUIHelper {

    const val MESSAGE_TITLE = "jclasslib"
    val ICON_EMPTY: Icon = EmptyIcon(16, 16)

    fun isMacOs() = System.getProperty("os.name").lowercase().startsWith("mac")

    fun centerOnParentWindow(window: Window, parentWindow: Window) {
        val x = parentWindow.x + (parentWindow.width - window.width) / 2
        val y = parentWindow.y + (parentWindow.height - window.height) / 2
        window.setLocation(x, y)
    }

    fun showURL(urlSpec: String) {
        try {
            Util.showUrl(URL(urlSpec))
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
    }

    fun <T : AbstractFileSystemChooser<*>> T.applyPath(currentDirectory: String): T {
        currentDirectory(currentDirectory.let { if (it.isNotEmpty()) File(it) else null })
        return this
    }

    fun JComponent.getParentWindow(): Window? = SwingUtilities.getWindowAncestor(this)

    fun scale(value: Int): Int {
        return (value * UIScale.getUserScaleFactor()).roundToInt()
    }
}


fun JTree.expandAll() {
    val rootNode = model.root as DefaultMutableTreeNode
    rootNode.depthFirstEnumeration().asSequence()
        .forEach { node -> expandPath(TreePath((node as DefaultMutableTreeNode).path)) }
}
