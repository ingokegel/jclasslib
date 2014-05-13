/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath;

import javax.swing.tree.DefaultMutableTreeNode;

/**
    Tree node for the tree in the <tt>ClasspathBrowser</tt> dialog.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 08:10:15 $
*/
public class ClassTreeNode extends DefaultMutableTreeNode {

    private boolean packageNode;

    /**
     * Constructor for the root node.
     */
    public ClassTreeNode() {
    }

    /**
     * Constructor for class and package nodes.
     * @param name the name of the entry.
     * @param packageNode whether the node is a package node or not.
     */
    public ClassTreeNode(String name, boolean packageNode) {
        super(name);
        this.packageNode = packageNode;
    }

    /**
     * Return whether the node is a package node or not.
     * @return the value.
     */
    public boolean isPackageNode() {
        return packageNode;
    }
}
