/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.util.AlertType
import org.gjt.jclasslib.util.GUIHelper

fun classAttributeLink(services: BrowserServices, index: Int, attributeInfoClass: Class<out AttributeInfo>) {
    val attributesPath = services.browserComponent.treePane.getPathForCategory(NodeType.ATTRIBUTE)
    val attributesNode = attributesPath.lastPathComponent as BrowserTreeNode
    val targetNode = findChildNode(attributesNode, attributeInfoClass)
    if (targetNode == null) {
        GUIHelper.showMessage(services.browserComponent, getString("message.attribute.of.class.not.found", attributeInfoClass.name), AlertType.ERROR)
        return
    }
    val targetPath = attributesPath.pathByAddingChild(targetNode)
    services.browserComponent.treePane.tree.apply {
        selectionPath = targetPath
        scrollPathToVisible(targetPath)
    }

    val detailPane = services.browserComponent.detailPane.attributeDetailPane
    (detailPane.getDetailPane(attributeInfoClass) as TableDetailPane).selectIndex(index)
}

private fun findChildNode(attributesNode: BrowserTreeNode, attributeInfoClass: Class<out AttributeInfo>): BrowserTreeNode? =
        attributesNode.find { node -> node.element?.let { it::class.java } == attributeInfoClass }

