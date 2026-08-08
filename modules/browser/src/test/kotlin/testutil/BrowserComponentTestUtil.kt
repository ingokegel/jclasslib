/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.testutil

import org.gjt.jclasslib.browser.BrowserComponent
import org.gjt.jclasslib.browser.BrowserTreeNode
import javax.swing.tree.TreePath

fun testBrowserComponent(services: TestBrowserServices = TestBrowserServices()): BrowserComponent =
    onEdt { services.browserComponent }

fun BrowserComponent.findNode(predicate: (BrowserTreeNode) -> Boolean): BrowserTreeNode {
    val root = treePane.tree.model.root as BrowserTreeNode
    return root.depthFirstEnumeration().asSequence().filterIsInstance<BrowserTreeNode>().first(predicate)
}

fun BrowserComponent.selectNode(predicate: (BrowserTreeNode) -> Boolean): BrowserTreeNode {
    val node = findNode(predicate)
    treePane.tree.selectionPath = TreePath(node.path)
    return node
}

fun BrowserComponent.selectedNode(): BrowserTreeNode =
    requireNotNull(treePane.tree.selectionPath).lastPathComponent as BrowserTreeNode
