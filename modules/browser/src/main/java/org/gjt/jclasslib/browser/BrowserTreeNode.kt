/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Tree node contained in the tree of the <tt>BrowserTreePane</tt> and
 * representing a structural element of the class file format.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>, <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class BrowserTreeNode extends DefaultMutableTreeNode {


    private NodeType type;
    private Object element;

    public BrowserTreeNode(String text) {
        this(text, NodeType.NO_CONTENT);
    }

    public BrowserTreeNode(String text, NodeType type) {
        this(text, type, null);
    }

    public BrowserTreeNode(String text, NodeType type, Object element) {
        super(text);
        this.type = type;
        this.element = element;
    }

    /**
     * Get the type of the node as defined by the <tt>NODE_</tt> constants.
     *
     * @return the type
     */
    public NodeType getType() {
        return type;
    }

    public int getIndex() {
        return getParent().getIndex(this);
    }

    /**
     * Get the element associated with this node
     *
     * @return the element
     */
    public Object getElement() {
        return element;
    }
}
