/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath;

import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.io.IOException;

/**
    Base class for classpath entries.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 08:10:15 $
*/
public abstract class ClasspathEntry implements ClasspathComponent {

    /** Suffix for class files. */
    protected static final String CLASSFILE_SUFFIX = ".class";

    private String fileName;
    private File file;

    /**
     * Get the name of the classpath entry.
     * @return the name
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Set the name of the classpath entry.
     * @param fileName the name.
     */
    public void setFileName(String fileName) {

        this.fileName = fileName;
        file = new File(fileName);
        try {
            file = file.getCanonicalFile();
        } catch (IOException e) {
            file = null;
        }
    }

    public boolean equals(Object other) {

        if (this == other) {
            return true;
        }
        if (other.getClass() != getClass()) {
            return false;
        }

        return fileName.equals(((ClasspathEntry)other).fileName);
    }

    public int hashCode() {
        return fileName.hashCode();
    }

    // classpath entries are immutable
    public void addClasspathChangeListener(ClasspathChangeListener listener) {
    }

    public void removeClasspathChangeListener(ClasspathChangeListener listener) {
    }

    /**
     * Get the file for the classpath entry. May be <tt>null</tt> if the entry is invalid.
     * @return the file.
     */
    protected File getFile() {
        return file;
    }

    /**
     * Convenience method to get a node or add a new class of package node to
     * a parent node. New nodes will be added in correct sort order, packages first.
     * @param newNodeName the name of the new node.
     * @param parentNode the parent node.
     * @param packageNode whether the new node is a package node or not.
     * @param model the tree model.
     * @param reset whether a reset operation is in progress.
     * @return the fould or created node.
     */
    protected ClassTreeNode addOrFindNode(String newNodeName,
                                          ClassTreeNode parentNode,
                                          boolean packageNode,
                                          DefaultTreeModel model,
                                          boolean reset)
    {
        int childCount = parentNode.getChildCount();

        ClassTreeNode newNode = new ClassTreeNode(newNodeName, packageNode);
        for (int i = 0; i < childCount; i++) {
            ClassTreeNode childNode = (ClassTreeNode)parentNode.getChildAt(i);
            String childNodeName = childNode.toString();
            if (childNode.getChildCount() > 0 && !packageNode) {
                continue;
            } else if (childNode.getChildCount() == 0 && packageNode) {
                insertNode(newNode, parentNode, i, model, reset);
                return newNode;
            } else if (newNodeName.equals(childNodeName)) {
                return childNode;
            } else if (newNodeName.compareTo(childNodeName) < 0) {
                insertNode(newNode, parentNode, i, model, reset);
                return newNode;
            }
        }
        insertNode(newNode, parentNode, childCount, model, reset);

        return newNode;
    }

    /**
     * Strip the class suffix from the supplied file name.
     * @param name the file name.
     * @return the stripped name.
     */
    protected String stripClassSuffix(String name) {
        return name.substring(0, name.length() - CLASSFILE_SUFFIX.length());
    }

    private void insertNode(ClassTreeNode newNode,
                              ClassTreeNode parentNode,
                              int insertionIndex,
                              DefaultTreeModel model,
                              boolean reset)
    {
        parentNode.insert(newNode, insertionIndex);
        if (!reset) {
            model.nodesWereInserted(parentNode, new int[] {insertionIndex});
        }
    }

}
