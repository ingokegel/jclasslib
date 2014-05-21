/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.browser.detail.AttributeDetailPane;
import org.gjt.jclasslib.browser.detail.ListDetailPane;
import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.util.GUIHelper;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ClassAttributeHyperlinkListener extends MouseAdapter {

    private BrowserServices services;
    private int index;
    private Class<? extends AttributeInfo> attributeInfoClass;

    public ClassAttributeHyperlinkListener(BrowserServices services, int index, Class<? extends AttributeInfo> attributeInfoClass) {
        this.services = services;
        this.index = index;
        this.attributeInfoClass = attributeInfoClass;
    }

    public void mouseClicked(MouseEvent event) {
        link(services, index, attributeInfoClass);
    }

    public static void link(BrowserServices services, int index, Class<? extends AttributeInfo> attributeInfoClass) {
        BrowserTreePane treePane = services.getBrowserComponent().getTreePane();
        TreePath attributesPath = treePane.getPathForCategory(BrowserTreeNode.NODE_ATTRIBUTE);
        link(services, attributesPath, index, attributeInfoClass);
    }

    public static void link(BrowserServices services, TreePath parentPath, int index, Class<? extends AttributeInfo> attributeInfoClass) {

        BrowserTreeNode attributesNode = (BrowserTreeNode)parentPath.getLastPathComponent();
        BrowserTreeNode targetNode = findChildNode(attributesNode, attributeInfoClass);
        if (targetNode == null) {
            GUIHelper.showMessage(services.getBrowserComponent(), "No attribute of class " + attributeInfoClass.getName() + " found", JOptionPane.ERROR_MESSAGE);
            return;
        }

        TreePath targetPath = parentPath.pathByAddingChild(targetNode);
        JTree tree = services.getBrowserComponent().getTreePane().getTree();
        tree.setSelectionPath(targetPath);
        tree.scrollPathToVisible(targetPath);

        AttributeDetailPane detailPane = (AttributeDetailPane)services.getBrowserComponent().getDetailPane().getCurrentDetailPane();
        ((ListDetailPane)detailPane.getAttributeDetailPane(attributeInfoClass)).selectIndex(index);
    }

    private static BrowserTreeNode findChildNode(BrowserTreeNode attributesNode, Class<? extends AttributeInfo> attributeInfoClass) {
        int childCount = attributesNode.getChildCount();
        for (int i = 0; i < childCount; i++) {
            BrowserTreeNode childNode = (BrowserTreeNode)attributesNode.getChildAt(i);
            AttributeInfo attributeInfo = (AttributeInfo)childNode.getElement();
            if (attributeInfo.getClass() == attributeInfoClass) {
                return childNode;
            }
        }
        return null;
    }

}

