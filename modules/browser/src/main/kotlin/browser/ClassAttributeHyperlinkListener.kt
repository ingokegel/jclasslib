/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import browser.BrowserBundle.getString
import com.install4j.runtime.alert.AlertType
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.util.GUIHelper
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent

class ClassAttributeHyperlinkListener(private val services: BrowserServices, private val index: Int, private val attributeInfoClass: Class<out AttributeInfo>) : MouseAdapter() {

    override fun mouseClicked(event: MouseEvent) {
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
            attributesNode.find { it.element?.let { it::class.java } == attributeInfoClass }
}

