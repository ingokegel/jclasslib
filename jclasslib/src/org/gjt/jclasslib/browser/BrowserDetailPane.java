/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.browser.detail.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;

/**
    The right half of a child window of the bytecode browser application 
    showing detailed information for the specific tree node selected in
    <tt>BrowserTreePane</tt>.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:22 $
*/
public class BrowserDetailPane extends JPanel {

    private static final Dimension detailMinimumSize = new Dimension(150,150);
    private static final Dimension detailPreferredSize = new Dimension(150,150);

    private BrowserInternalFrame parentFrame;
    private HashMap nodeTypeToDetailPane = new HashMap();
    
    public BrowserDetailPane(BrowserInternalFrame parentFrame) {
        this.parentFrame = parentFrame;
        setupComponent();
    }
    
    /**
        Show details for the specific tree node selected in
        <tt>BrowserTreePane</tt>.
        @param nodeType the type of the node as defined in the <tt>NODE_</tt>
                        constants in <tt>BrowserMutableTreeNode</tt>
        @param treePath the tree path of the selected node
     */
    public void showPane(String nodeType, TreePath treePath) {
        CardLayout layout = (CardLayout)getLayout();
        AbstractDetailPane detailPane = (AbstractDetailPane)nodeTypeToDetailPane.get(nodeType);
        if (detailPane != null) {
            detailPane.show(treePath);
        }
        
        layout.show(this, nodeType);
    }
    
    /**
        Setup the internal state of the component at the beginning of its life cycle.
     */
    public void setupComponent() {

        setLayout(new CardLayout());
        
        add(new JPanel(), BrowserMutableTreeNode.NODE_NO_CONTENT);
        
        addScreen(new GeneralDetailPane(parentFrame),
                  BrowserMutableTreeNode.NODE_GENERAL);
        addScreen(new ConstantPoolDetailPane(parentFrame),
                  BrowserMutableTreeNode.NODE_CONSTANT_POOL);
        addScreen(new InterfaceDetailPane(parentFrame),
                  BrowserMutableTreeNode.NODE_INTERFACE);
        addScreen(new ClassMemberDetailPane(parentFrame, ClassMemberDetailPane.FIELDS),
                  BrowserMutableTreeNode.NODE_FIELD);
        addScreen(new ClassMemberDetailPane(parentFrame, ClassMemberDetailPane.METHODS),
                  BrowserMutableTreeNode.NODE_METHOD);
        addScreen(new AttributeDetailPane(parentFrame),
                  BrowserMutableTreeNode.NODE_ATTRIBUTE);
        
        setMinimumSize(detailMinimumSize);
        setPreferredSize(detailPreferredSize);
        
    }

    /**
        Get the <tt>AttributeDetailPane</tt> detail pane associated with the
        node type <tt>BrowserMutableTreeNode.NODE_ATTRIBUTE</tt>. This is necessary for 
        hyperlinks within <tt>Code</tt> attributes.
        @return the <tt>AttributeDetailPane</tt>
     */
    public AttributeDetailPane getAttributeDetailPane() {
        return (AttributeDetailPane)nodeTypeToDetailPane.get(BrowserMutableTreeNode.NODE_ATTRIBUTE);
    }

    private void addScreen(JPanel panel, String nodeType) {
        add(panel, nodeType);
        nodeTypeToDetailPane.put(nodeType, panel);
    }
    
}
