/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.structures.*;
import org.gjt.jclasslib.structures.constants.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.border.*;
import java.awt.*;

/**
    The pane containing the tree structure for the class file shown in the
    child window.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.4 $ $Date: 2002-05-30 17:56:27 $
*/
public class BrowserTreePane extends JPanel {

    private static final Dimension treeMinimumSize = new Dimension(100,150);
    private static final Dimension treePreferredSize = new Dimension(250,150);

    private BrowserServices services;
    private JTree treeView;
    private TreePath constantPoolPath;

    public BrowserTreePane(BrowserServices services) {
        this.services = services;
        setLayout(new BorderLayout());
        setupComponent();
    }

    /**
        Get the tree view.
        @return the tree view
     */
    public JTree getTreeView() {
        return treeView;
    }

    /**
        Get the tree path for the parent node of the constant pool.
        @return the tree path
     */
    public TreePath getConstantPoolPath() {
        return constantPoolPath;
    }

    /**
        Rebuild the tree from the <tt>ClassFile</tt> object.
     */
    public void rebuild() {
        treeView.setModel(buildTreeModel());
    }

    private void setupComponent() {

        JScrollPane treeScrollPane = new JScrollPane(buildTreeView());
        treeScrollPane.setMinimumSize(treeMinimumSize);
        treeScrollPane.setPreferredSize(treePreferredSize);

        add(treeScrollPane, BorderLayout.CENTER);
    }

    private JTree buildTreeView() {

        treeView = new JTree(buildTreeModel());

        treeView.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        treeView.setRootVisible(false);
        treeView.setShowsRootHandles(true);
        treeView.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));

        return treeView;
    }

    private TreeModel buildTreeModel() {
        BrowserMutableTreeNode rootNode = buildRootNode();
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

        return treeModel;
    }

    private BrowserMutableTreeNode buildRootNode() {

        BrowserMutableTreeNode rootNode = new BrowserMutableTreeNode("Class file");
        ClassFile classFile = services.getClassFile();
        if (classFile != null) {
			BrowserMutableTreeNode generalNode = new BrowserMutableTreeNode("General Information", BrowserMutableTreeNode.NODE_GENERAL);
			BrowserMutableTreeNode constantPoolNode = buildConstantPoolNode();
			rootNode.add(generalNode);
			rootNode.add(constantPoolNode);
			rootNode.add(buildInterfacesNode());
			rootNode.add(buildFieldsNode());
			rootNode.add(buildMethodsNode());
			rootNode.add(buildAttributesNode());

			constantPoolPath = new TreePath(new Object[] {rootNode, constantPoolNode});
		}

        return rootNode;
    }

    private BrowserMutableTreeNode buildConstantPoolNode() {

        BrowserMutableTreeNode constantPoolNode = new BrowserMutableTreeNode("Constant Pool");

		CPInfo[] constantPool = services.getClassFile().getConstantPool();
		int constantPoolCount = constantPool.length;

		for (int i = 1; i < constantPoolCount; i++) {
			i += addConstantPoolEntry(constantPool[i], i, constantPoolCount, constantPoolNode);
		}

        return constantPoolNode;
    }

    private int addConstantPoolEntry(CPInfo constantPoolEntry,
                                     int index,
                                     int constantPoolCount,
                                     BrowserMutableTreeNode constantPoolNode) {


        if (constantPoolEntry == null) {
            constantPoolNode.add(buildNullNode());
        } else {
            BrowserMutableTreeNode entryNode =
                new BrowserMutableTreeNode(getFormattedIndex(index, constantPoolCount) +
                                           constantPoolEntry.getTagVerbose(),
                                           BrowserMutableTreeNode.NODE_CONSTANT_POOL,
                                           index);

            constantPoolNode.add(entryNode);
            if (constantPoolEntry instanceof ConstantLargeNumeric) {
                addConstantPoolContinuedEntry(index + 1,
                                              constantPoolCount,
                                              constantPoolNode);
                return 1;
            }
        }
        return 0;
    }

    private void addConstantPoolContinuedEntry(int index,
                                               int constantPoolCount,
                                               BrowserMutableTreeNode constantPoolNode) {

        BrowserMutableTreeNode entryNode =
            new BrowserMutableTreeNode(getFormattedIndex(index, constantPoolCount) +
                                       "(large numeric continued)",
                                       BrowserMutableTreeNode.NODE_NO_CONTENT);
        constantPoolNode.add(entryNode);
    }

    private BrowserMutableTreeNode buildInterfacesNode() {

        BrowserMutableTreeNode interfacesNode = new BrowserMutableTreeNode("Interfaces");
        int[] interfaces = services.getClassFile().getInterfaces();
        int interfacesCount = interfaces.length;
        BrowserMutableTreeNode entryNode;
        for (int i = 0; i < interfacesCount; i++) {
            entryNode = new BrowserMutableTreeNode("Interface " + i,
                                                   BrowserMutableTreeNode.NODE_INTERFACE,
                                                   i);
            interfacesNode.add(entryNode);
        }

        return interfacesNode;
    }

    private BrowserMutableTreeNode buildFieldsNode() {

        return buildClassMembersNode("Fields",
                                     BrowserMutableTreeNode.NODE_FIELD,
                                     services.getClassFile().getFields());
    }

    private BrowserMutableTreeNode buildMethodsNode() {

        return buildClassMembersNode("Methods",
                                     BrowserMutableTreeNode.NODE_METHOD,
                                     services.getClassFile().getMethods());
    }

    private BrowserMutableTreeNode buildClassMembersNode(String text,
                                                         String type,
                                                         ClassMember[] classMembers) {

        BrowserMutableTreeNode classMemberNode = new BrowserMutableTreeNode(text);
        int classMembersCount = classMembers.length;

        for (int i = 0; i < classMembersCount; i++) {
            addClassMembersNode(classMembers[i],
                                i,
                                classMembersCount,
                                type,
                                classMemberNode);
        }

        return classMemberNode;
    }

    private void addClassMembersNode(ClassMember classMember,
                                     int index,
                                     int classMembersCount,
                                     String type,
                                     BrowserMutableTreeNode classMemberNode) {

        if (classMember == null) {
            classMemberNode.add(buildNullNode());
        } else {
            try {
                BrowserMutableTreeNode entryNode =
                    new BrowserMutableTreeNode(getFormattedIndex(index, classMembersCount) +
                                               classMember.getName(),
                                               type,
                                               index);

                classMemberNode.add(entryNode);
                addAttributeNodes(entryNode, classMember);

            } catch (InvalidByteCodeException ex) {
                classMemberNode.add(buildNullNode());
            }
        }
    }

    private BrowserMutableTreeNode buildAttributesNode() {
        BrowserMutableTreeNode attributesNode = new BrowserMutableTreeNode("Attributes");

        addAttributeNodes(attributesNode, services.getClassFile());

        return attributesNode;
    }

    private BrowserMutableTreeNode buildNullNode() {

        return new BrowserMutableTreeNode("[error] null");
    }

    private void addAttributeNodes(BrowserMutableTreeNode parentNode,
                                   AbstractStructureWithAttributes structure) {

        AttributeInfo[] attributes = structure.getAttributes();
        if (attributes == null) {
            return;
        }
        int attributesCount = attributes.length;
        for (int i = 0; i < attributesCount; i++) {
            addSingleAttributeNode(attributes[i],
                                   i,
                                   attributesCount,
                                   parentNode);
        }
    }

    private void addSingleAttributeNode(AttributeInfo attribute,
                                        int index,
                                        int attributesCount,
                                        BrowserMutableTreeNode parentNode) {


        if (attribute == null) {
            parentNode.add(buildNullNode());
        } else {
            try {
                BrowserMutableTreeNode entryNode =
                    new BrowserMutableTreeNode(getFormattedIndex(index, attributesCount) +
                                               attribute.getName(),
                                               BrowserMutableTreeNode.NODE_ATTRIBUTE,
                                               index);

                parentNode.add(entryNode);
                addAttributeNodes(entryNode, attribute);

            } catch (InvalidByteCodeException ex) {
                parentNode.add(buildNullNode());
            }
        }
    }

    private String getFormattedIndex(int index, int maxIndex) {

        StringBuffer buffer = new StringBuffer("[");
        String indexString = String.valueOf(index);
        String maxIndexString = String.valueOf(maxIndex - 1);
        for (int i = 0; i < maxIndexString.length() - indexString.length(); i++) {
            buffer.append("0");
        }
        buffer.append(indexString);
        buffer.append("]");
        buffer.append(" ");

        return buffer.toString();
    }

}
