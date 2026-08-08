/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.testutil

import org.gjt.jclasslib.browser.BrowserFrame
import org.gjt.jclasslib.browser.BrowserTab
import org.gjt.jclasslib.browser.BrowserTreeNode
import org.gjt.jclasslib.browser.DefaultSavingConfirmationPolicy
import org.gjt.jclasslib.browser.savingConfirmationPolicy
import org.gjt.jclasslib.io.writeToByteArray
import org.gjt.jclasslib.structures.ClassFile
import java.io.File
import javax.swing.tree.TreePath

class BrowserAppFixture {

    val frame: BrowserFrame = onEdt {
        BrowserFrame().apply {
            setSize(1000, 800)
            isVisible = true
        }
    }

    fun openClass(classFile: ClassFile, file: File): BrowserTab {
        file.writeBytes(classFile.writeToByteArray())
        return onEdt { frame.openClassFromFile(file) }
    }

    fun selectNode(tab: BrowserTab, predicate: (BrowserTreeNode) -> Boolean): BrowserTreeNode = onEdt {
        val tree = tab.browserComponent.treePane.tree
        val root = tree.model.root as BrowserTreeNode
        val node = root.depthFirstEnumeration().asSequence().filterIsInstance<BrowserTreeNode>().first(predicate)
        tree.selectionPath = TreePath(node.path)
        node
    }

    fun dispose() {
        onEdt { frame.dispose() }
    }
}

fun resetSavingConfirmationPolicy() {
    savingConfirmationPolicy = DefaultSavingConfirmationPolicy()
}
