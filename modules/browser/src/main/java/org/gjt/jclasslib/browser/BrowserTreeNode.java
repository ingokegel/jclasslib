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
    private int index;
    private Object element;

    /**
     * Constructor.
     *
     * @param text the display text.
     */
    public BrowserTreeNode(String text) {
        this(text, NodeType.NO_CONTENT);
    }

    /**
     * Constructor.
     *
     * @param text the display text.
     * @param type the node type. One of the <tt>NODE_</tt> constants.
     */
    public BrowserTreeNode(String text, NodeType type) {
        this(text, type, 0);
    }

    /**
     * Constructor.
     *
     * @param text  the display text.
     * @param type  the node type. One of the <tt>NODE_</tt> constants.
     * @param index the logical index of this node.
     */
    public BrowserTreeNode(String text, NodeType type, int index) {
        this(text, type, index, null);
    }

    public BrowserTreeNode(String text, NodeType type, int index, Object element) {
        super(text);
        this.type = type;
        this.index = index;
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

    /**
     * Get the index of the node among its siblings. This information <i>could</i>
     * be retrieved from a tree but is important structural information and
     * should not be left to chance.
     *
     * @return the index
     */
    public int getIndex() {
        return index;
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
