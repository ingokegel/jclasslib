/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import javax.swing.tree.*;

/**
    Tree node contained in the tree of the <tt>BrowserTreePane</tt> and 
    representing a structural element of the class file format.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:21 $
*/
public class BrowserMutableTreeNode extends DefaultMutableTreeNode {

    /** Node that does not exhibit detail content */
    public static final String NODE_NO_CONTENT = "noContent";
    /** Node for general information on the class file structure */
    public static final String NODE_GENERAL = "general";
    /** Node for a constant pool entry (<tt>CPInfo</tt>) */
    public static final String NODE_CONSTANT_POOL = "constantPool";
    /** Node for an interface entry  */
    public static final String NODE_INTERFACE = "interface";
    /** Node for a field entry  (<tt>FieldInfo</tt>) */
    public static final String NODE_FIELD = "field";
    /** Node for a method entry  (<tt>MethodInfo</tt>) */
    public static final String NODE_METHOD = "method";
    /** Node for an attribute entry  (<tt>AttributeInfo</tt>) */
    public static final String NODE_ATTRIBUTE = "attribute";
    
    private String type;
    private int index;
    
    public BrowserMutableTreeNode(String text) {
        this(text, NODE_NO_CONTENT);
    }

    public BrowserMutableTreeNode(String text, String type) {
        this(text, type, 0);
    }
    
    public BrowserMutableTreeNode(String text, String type, int index) {
        super(text);
        this.type = type;
        this.index = index;
    }

    /**
        Get the type of the node as defined by the <tt>NODE_</tt> constants.
        @return the type
     */
    public String getType() {
        return type;
    }

    /**
        Get the index of the node among its siblings. This information <i>could</i>
        be retrieved from a tree but is important structural information and
        should not be left to chance.
        @return the index
     */
    public int getIndex() {
        return index;
    }
}
