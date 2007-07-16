/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.structures.*;
import org.gjt.jclasslib.structures.attributes.*;
import org.gjt.jclasslib.structures.constants.ConstantLargeNumeric;
import org.gjt.jclasslib.structures.elementvalues.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The pane containing the tree structure for the class file shown in the
 * child window.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>, <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 * @version $Revision: 1.12 $ $Date: 2007-07-16 14:15:11 $
 */
public class BrowserTreePane extends JPanel {

    private static final Dimension treeMinimumSize = new Dimension(100, 150);
    private static final Dimension treePreferredSize = new Dimension(250, 150);

    private BrowserServices services;
    private JTree tree;
    private Map categoryToPath = new HashMap();

    /**
     * Constructor.
     *
     * @param services the associated browser services.
     */
    public BrowserTreePane(BrowserServices services) {
        this.services = services;
        setLayout(new BorderLayout());
        setupComponent();
    }

    /**
     * Get the tree view.
     *
     * @return the tree view
     */
    public JTree getTree() {
        return tree;
    }

    /**
     * Get the tree path for a given category.
     *
     * @param category the category. One the <tt>BrowserTree.NODE_</tt> constants.
     * @return the tree path.
     */
    public TreePath getPathForCategory(String category) {
        return (TreePath)categoryToPath.get(category);
    }

    /**
     * Show the specified method if it exists.
     *
     * @param methodName      the name of the method
     * @param methodSignature the signature of the method (in class file format)
     */
    public void showMethod(String methodName, String methodSignature) {

        TreePath methodsPath = (TreePath)categoryToPath.get(BrowserTreeNode.NODE_METHOD);
        if (methodsPath == null) {
            return;
        }
        MethodInfo[] methods = services.getClassFile().getMethods();
        TreeNode methodsNode = (TreeNode)methodsPath.getLastPathComponent();
        for (int i = 0; i < methodsNode.getChildCount(); i++) {
            BrowserTreeNode treeNode = (BrowserTreeNode)methodsNode.getChildAt(i);
            MethodInfo testMethod = methods[treeNode.getIndex()];
            try {
                if (testMethod.getName().equals(methodName) && testMethod.getDescriptor().startsWith(methodSignature)) {
                    TreePath path = methodsPath.pathByAddingChild(treeNode);
                    BrowserTreeNode codeNode = findCodeNode(treeNode, testMethod);
                    if (codeNode != null) {
                        path = path.pathByAddingChild(codeNode);
                    }

                    tree.makeVisible(path);
                    tree.scrollPathToVisible(path);
                    tree.setSelectionPath(path);
                    return;
                }
            } catch (InvalidByteCodeException ex) {
            }
        }
    }

    /**
     * Rebuild the tree from the <tt>ClassFile</tt> object.
     */
    public void rebuild() {
        categoryToPath.clear();
        tree.clearSelection();
        tree.setModel(buildTreeModel());
    }

    private void setupComponent() {

        JScrollPane treeScrollPane = new JScrollPane(buildTree());
        treeScrollPane.setMinimumSize(treeMinimumSize);
        treeScrollPane.setPreferredSize(treePreferredSize);

        add(treeScrollPane, BorderLayout.CENTER);
    }

    private JTree buildTree() {

        tree = new JTree(buildTreeModel());

        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);

        return tree;
    }

    private TreeModel buildTreeModel() {
        BrowserTreeNode rootNode = buildRootNode();
        DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);

        return treeModel;
    }

    private BrowserTreeNode buildRootNode() {

        BrowserTreeNode rootNode = new BrowserTreeNode("Class file");
        ClassFile classFile = services.getClassFile();
        if (classFile != null) {

            BrowserTreeNode generalNode = new BrowserTreeNode("General Information", BrowserTreeNode.NODE_GENERAL);
            BrowserTreeNode constantPoolNode = buildConstantPoolNode();
            BrowserTreeNode interfacesNode = buildInterfacesNode();
            BrowserTreeNode fieldsNode = buildFieldsNode();
            BrowserTreeNode methodsNode = buildMethodsNode();
            BrowserTreeNode attributesNode = buildAttributesNode();

            rootNode.add(generalNode);
            rootNode.add(constantPoolNode);
            rootNode.add(interfacesNode);
            rootNode.add(fieldsNode);
            rootNode.add(methodsNode);
            rootNode.add(attributesNode);

            categoryToPath.put(BrowserTreeNode.NODE_GENERAL, new TreePath(new Object[]{rootNode, generalNode}));
            categoryToPath.put(BrowserTreeNode.NODE_CONSTANT_POOL, new TreePath(new Object[]{rootNode, constantPoolNode}));
            categoryToPath.put(BrowserTreeNode.NODE_INTERFACE, new TreePath(new Object[]{rootNode, interfacesNode}));
            categoryToPath.put(BrowserTreeNode.NODE_FIELD, new TreePath(new Object[]{rootNode, fieldsNode}));
            categoryToPath.put(BrowserTreeNode.NODE_METHOD, new TreePath(new Object[]{rootNode, methodsNode}));
            categoryToPath.put(BrowserTreeNode.NODE_ATTRIBUTE, new TreePath(new Object[]{rootNode, attributesNode}));
        }

        return rootNode;
    }

    private BrowserTreeNode buildConstantPoolNode() {

        BrowserTreeNode constantPoolNode = new BrowserTreeNode("Constant Pool");

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
                                     BrowserTreeNode constantPoolNode) {


        if (constantPoolEntry == null) {
            constantPoolNode.add(buildNullNode());
        } else {
            BrowserTreeNode entryNode =
                    new BrowserTreeNode(getFormattedIndex(index, constantPoolCount) +
                    constantPoolEntry.getTagVerbose(),
                            BrowserTreeNode.NODE_CONSTANT_POOL,
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
                                               BrowserTreeNode constantPoolNode) {

        BrowserTreeNode entryNode =
                new BrowserTreeNode(getFormattedIndex(index, constantPoolCount) +
                "(large numeric continued)",
                        BrowserTreeNode.NODE_NO_CONTENT);
        constantPoolNode.add(entryNode);
    }

    private BrowserTreeNode buildInterfacesNode() {

        BrowserTreeNode interfacesNode = new BrowserTreeNode("Interfaces");
        int[] interfaces = services.getClassFile().getInterfaces();
        int interfacesCount = interfaces.length;
        BrowserTreeNode entryNode;
        for (int i = 0; i < interfacesCount; i++) {
            entryNode = new BrowserTreeNode("Interface " + i,
                    BrowserTreeNode.NODE_INTERFACE,
                    i);
            interfacesNode.add(entryNode);
        }

        return interfacesNode;
    }

    private BrowserTreeNode buildFieldsNode() {

        return buildClassMembersNode("Fields",
                BrowserTreeNode.NODE_FIELDS,
                BrowserTreeNode.NODE_FIELD,
                services.getClassFile().getFields());
    }

    private BrowserTreeNode buildMethodsNode() {

        return buildClassMembersNode("Methods",
                BrowserTreeNode.NODE_METHODS,
                BrowserTreeNode.NODE_METHOD,
                services.getClassFile().getMethods());
    }

    private BrowserTreeNode buildClassMembersNode(String text,
                                                  String containerType,
                                                  String childType,
                                                  ClassMember[] classMembers) {

        BrowserTreeNode classMemberNode = new BrowserTreeNode(text, containerType);
        int classMembersCount = classMembers.length;

        for (int i = 0; i < classMembersCount; i++) {
            addClassMembersNode(classMembers[i],
                    i,
                    classMembersCount,
                    childType,
                    classMemberNode);
        }

        return classMemberNode;
    }

    private void addClassMembersNode(ClassMember classMember,
                                     int index,
                                     int classMembersCount,
                                     String type,
                                     BrowserTreeNode classMemberNode) {

        if (classMember == null) {
            classMemberNode.add(buildNullNode());
        } else {
            try {
                BrowserTreeNode entryNode =
                        new BrowserTreeNode(getFormattedIndex(index, classMembersCount) +
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

    private BrowserTreeNode buildAttributesNode() {
        BrowserTreeNode attributesNode = new BrowserTreeNode("Attributes");

        addAttributeNodes(attributesNode, services.getClassFile());

        return attributesNode;
    }

    private BrowserTreeNode buildNullNode() {

        return new BrowserTreeNode("[error] null");
    }

    private void addAttributeNodes(BrowserTreeNode parentNode,
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
                                        BrowserTreeNode parentNode) {


        if (attribute == null) {
            parentNode.add(buildNullNode());
        } else {
            try {
                BrowserTreeNode entryNode =
                        new BrowserTreeNode(getFormattedIndex(index, attributesCount) +
                        attribute.getName(),
                                BrowserTreeNode.NODE_ATTRIBUTE,
                                index);

                parentNode.add(entryNode);
                if (attribute instanceof RuntimeAnnotationsAttribute) {
                    addRuntimeAnnotation(entryNode, (RuntimeAnnotationsAttribute)attribute);
                } else if (attribute instanceof AnnotationDefaultAttribute) {
                    addSingleElementValueEntryNode(((AnnotationDefaultAttribute)attribute).getDefaultValue(), 0, 1, entryNode);
                } else {
                    addAttributeNodes(entryNode, attribute);
                }

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

    private BrowserTreeNode findCodeNode(BrowserTreeNode treeNode, MethodInfo methodInfo) {
        AttributeInfo[] attributes = methodInfo.getAttributes();
        for (int i = 0; i < attributes.length; i++) {
            if (attributes[i] instanceof CodeAttribute) {
                return (BrowserTreeNode)treeNode.getChildAt(i);
            }
        }
        return null;
    }


    private void addRuntimeAnnotation(BrowserTreeNode parentNode,
                                      RuntimeAnnotationsAttribute structure) {

        AnnotationElementValue[] annotations = structure.getRuntimeAnnotations();
        if (annotations == null) {
            return;
        }
        int annotationsCount = annotations.length;
        for (int i = 0; i < annotationsCount; i++) {
            addSingleAnnotationNode(annotations[i],
                    i,
                    annotationsCount,
                    parentNode);
        }
    }

    private void addSingleAnnotationNode(AnnotationElementValue annotation,
                                         int index,
                                         int attributesCount,
                                         BrowserTreeNode parentNode) {


        if (annotation == null) {
            parentNode.add(buildNullNode());
        } else {
            BrowserTreeNode entryNode =
                    new BrowserTreeNode(getFormattedIndex(index, attributesCount) +
                    annotation.getEntryName(),
                            BrowserTreeNode.NODE_ANNOTATION,
                            index,
                            annotation);
            parentNode.add(entryNode);
            addElementValuePairEntry(entryNode, annotation);
        }
    }

    private void addElementValuePairEntry(BrowserTreeNode parentNode,
                                          AnnotationElementValue annotation) {

        ElementValuePair[] entries = annotation.getElementValuePairEntries();
        if (entries == null) {
            return;
        }
        int entriesCount = entries.length;
        for (int i = 0; i < entriesCount; i++) {
            addSingleElementValuePairEntryNode(entries[i],
                    i,
                    entriesCount,
                    parentNode);
        }
    }

    private void addArrayElementValueEntry(BrowserTreeNode parentNode,
                                           ArrayElementValue aeve) {

        ElementValue[] entries = aeve.getElementValueEntries();
        if (entries == null) {
            return;
        }
        int entriesCount = entries.length;
        for (int i = 0; i < entriesCount; i++) {
            addSingleElementValueEntryNode(entries[i],
                    i,
                    entriesCount,
                    parentNode);
        }
    }


    private void addSingleElementValuePairEntryNode(ElementValuePair evep,
                                                    int index,
                                                    int attributesCount,
                                                    BrowserTreeNode parentNode) {


        if (evep == null) {
            parentNode.add(buildNullNode());
        } else {
            BrowserTreeNode entryNode =
                    new BrowserTreeNode(getFormattedIndex(index, attributesCount) +
                    evep.getEntryName(),
                            BrowserTreeNode.NODE_ELEMENTVALUEPAIR,
                            index,
                            evep);
            parentNode.add(entryNode);
            addSingleElementValueEntryNode(evep.getElementValue(), 0, 1, entryNode);
        }
    }

    private void addSingleElementValueEntryNode(ElementValue eve,
                                                int index,
                                                int attributesCount,
                                                BrowserTreeNode parentNode) {

        if (eve == null) {
            parentNode.add(buildNullNode());
        } else {
            String prefix = attributesCount > 1 ?
                    getFormattedIndex(index, attributesCount) : "";
            String nodeType = BrowserTreeNode.NODE_ELEMENTVALUE;
            if (eve instanceof AnnotationElementValue) {
                nodeType = BrowserTreeNode.NODE_ANNOTATION;
            } else if (eve instanceof ArrayElementValue) {
                nodeType = BrowserTreeNode.NODE_ARRAYELEMENTVALUE;
            }

            BrowserTreeNode entryNode =
                    new BrowserTreeNode(prefix + eve.getEntryName(),
                            nodeType, index, eve);

            parentNode.add(entryNode);
            if (nodeType == BrowserTreeNode.NODE_ANNOTATION) {
                addElementValuePairEntry(entryNode, (AnnotationElementValue)eve);
            } else if (nodeType == BrowserTreeNode.NODE_ARRAYELEMENTVALUE) {
                addArrayElementValueEntry(entryNode, (ArrayElementValue)eve);
            }
        }
    }
}
