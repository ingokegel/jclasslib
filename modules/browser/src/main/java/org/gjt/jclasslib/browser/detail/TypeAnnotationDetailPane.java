/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.BrowserTreeNode;
import org.gjt.jclasslib.browser.NodeType;
import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.MethodInfo;
import org.gjt.jclasslib.structures.attributes.*;
import org.gjt.jclasslib.util.ExtendedJLabel;
import org.gjt.jclasslib.util.HtmlDisplayTextArea;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.tree.TreePath;

/**
 * Class for showing a type annotation node.
 */
public class TypeAnnotationDetailPane extends FixedListDetailPane {

    private ExtendedJLabel lblType;
    private HtmlDisplayTextArea txtInfo;
    private HtmlDisplayTextArea txtType;
    private ExtendedJLabel lblTypePath;

    public TypeAnnotationDetailPane(BrowserServices services) {
        super(services);
    }

    protected void addLabels() {
        addDetailPaneEntry(normalLabel("Target Type:"),
            lblType = highlightLabel());

        addDetailPaneEntry(normalLabel("Target Info:"),
            txtInfo = highlightTextArea());

        addDetailPaneEntry(lblTypePath = normalLabel("Type path:"),
            txtType = highlightTextArea());

        txtInfo.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    String description = e.getDescription();
                    handleLink(description);
                }
            }
        });

    }

    public void show(TreePath treePath) {
        TypeAnnotation typeAnnotation = (TypeAnnotation)((BrowserTreeNode)treePath.getLastPathComponent()).getElement();

        lblType.setText(typeAnnotation.getTargetType().toString());
        txtInfo.setText(typeAnnotation.getTargetInfo().getVerbose().replace("\n", "<br>"));
        String typePathVerbose = getTypePathVerbose(typeAnnotation.getTypePathEntries());
        boolean pathAvailable = typePathVerbose != null;
        if (pathAvailable) {
            txtType.setText(typePathVerbose);
        }
        lblTypePath.setVisible(pathAvailable);
        txtType.setVisible(pathAvailable);

        super.show(treePath);
    }

    private String getTypePathVerbose(TypePathEntry[] typePathEntries) {
        if (typePathEntries == null || typePathEntries.length == 0) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        for (TypePathEntry typePathEntry : typePathEntries) {
            buffer.append(typePathEntry.getTypePathKind());
            buffer.append(", argument index ").append(typePathEntry.getTypeArgumentIndex());
        }
        return buffer.toString();
    }

    private void handleLink(String description) {
        char type = description.charAt(0);
        int index = Integer.parseInt(description.substring(1));
        switch (type) {
            case 'E':
                handleExceptionsLink(index);
                break;
            case 'L':
                handleLocalVarLink(index);
                break;
            case 'I':
                handleInterfaceLink(index);
                break;
            default:
                throw new IllegalArgumentException("Invalid link type " + type);
        }
    }

    private void handleExceptionsLink(int index) {
        JTree tree = getServices().getBrowserComponent().getTreePane().getTree();
        TreePath parentPath = findParentNode(tree, MethodInfo.class);
        TreePath path = findAttributeChildNode(parentPath, ExceptionsAttribute.class);

        handleListLink(index, path, ExceptionsAttribute.class);
    }

    private void handleLocalVarLink(int index) {
        JTree tree = getServices().getBrowserComponent().getTreePane().getTree();
        TreePath parentPath = findParentNode(tree, CodeAttribute.class);
        TreePath path = findAttributeChildNode(parentPath, LocalVariableTableAttribute.class);
        LocalVariableTableAttribute attribute = (LocalVariableTableAttribute)((BrowserTreeNode)path.getLastPathComponent()).getElement();

        handleListLink(getLinkIndex(index, attribute), path, LocalVariableTableAttribute.class);
    }

    private int getLinkIndex(int index, LocalVariableTableAttribute attribute) {
        LocalVariableEntry[] localVariableTable = attribute.getLocalVariableEntries();
        for (int i = 0; i < localVariableTable.length; i++) {
            LocalVariableEntry entry = localVariableTable[i];
            if (entry.getIndex() == index) {
                return i;
            }
        }
        throw new IllegalArgumentException("index " + index + " not found in local variable table");
    }

    private void handleInterfaceLink(int index) {
        TreePath interfacesPath = getServices().getBrowserComponent().getTreePane().getPathForCategory(NodeType.INTERFACE);
        BrowserTreeNode interfacesNode = (BrowserTreeNode)interfacesPath.getLastPathComponent();
        if (index >= interfacesNode.getChildCount()) {
            throw new IllegalArgumentException("Invalid interface index " + index);
        }

        TreePath path = interfacesPath.pathByAddingChild(interfacesNode.getChildAt(index));
        selectPath(path);
    }

    private void handleListLink(int index, TreePath path, Class<? extends AttributeInfo> attributeClass) {
        selectPath(path);
        AttributeDetailPane detailPane = (AttributeDetailPane)getServices().getBrowserComponent().getDetailPane().getCurrentDetailPane();
        ((TableDetailPane)detailPane.getDetailPane(attributeClass)).selectIndex(index);
    }

    private void selectPath(TreePath path) {
        JTree tree = getServices().getBrowserComponent().getTreePane().getTree();
        tree.setSelectionPath(path);
        tree.scrollPathToVisible(path);
    }

    private TreePath findAttributeChildNode(TreePath path, Class<? extends AttributeInfo> attributeClass) {
        BrowserTreeNode methodNode = (BrowserTreeNode)path.getLastPathComponent();
        for (int i = 0; i < methodNode.getChildCount(); i++) {
            BrowserTreeNode attributeNode = (BrowserTreeNode)methodNode.getChildAt(i);
            if (attributeNode.getElement().getClass() == attributeClass) {
                return path.pathByAddingChild(attributeNode);
            }
        }
        throw new RuntimeException("No attribute node for class " + attributeClass + " found");
    }

    private TreePath findParentNode(JTree tree, Class elementClass) {
        TreePath path = tree.getSelectionPath();
        while (path != null) {
            BrowserTreeNode node = (BrowserTreeNode)path.getLastPathComponent();
            if (node.getElement().getClass() == elementClass) {
                break;
            } else {
                path = path.getParentPath();
            }
        }
        if (path == null) {
            throw new RuntimeException("No parent node with element class " + elementClass + " found");
        }
        return path;
    }

}
