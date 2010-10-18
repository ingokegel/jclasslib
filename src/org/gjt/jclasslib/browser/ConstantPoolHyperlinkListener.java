/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import javax.swing.*;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
    Listens for mouse clicks and manages linking into the constat pool.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.6 $ $Date: 2003-08-18 08:02:07 $
*/
public class ConstantPoolHyperlinkListener extends MouseAdapter {

    private BrowserServices services;
    private int constantPoolIndex;

    /**
        Constructor.
        @param services the browser services
        @param constantPoolIndex the index of the constant pool to lonk to.
     */
    public ConstantPoolHyperlinkListener(BrowserServices services, int constantPoolIndex) {
        
        this.services = services;
        this.constantPoolIndex = constantPoolIndex;
    }
    
    public void mouseClicked(MouseEvent event) {
        link(services, constantPoolIndex);
    }

    /**
        Link to a specific constant pool entry.
        @param services browser services
        @param constantPoolIndex the index of the constant pool entry
     */
    public static void link(BrowserServices services, int constantPoolIndex) {
        
        if (constantPoolIndex <= 0) {
            return;
        }
        
        JTree tree = services.getBrowserComponent().getTreePane().getTree();
        TreePath newPath = linkPath(services, constantPoolIndex);
        tree.setSelectionPath(newPath);
        tree.scrollPathToVisible(newPath);
    }
    
    private static TreePath linkPath(BrowserServices services, int constantPoolIndex) {
        
        TreePath constantPoolPath = services.getBrowserComponent().getTreePane().getPathForCategory(BrowserTreeNode.NODE_CONSTANT_POOL);
        
        BrowserTreeNode constantPoolNode = (BrowserTreeNode)constantPoolPath.getLastPathComponent();
        TreeNode targetNode = constantPoolNode.getChildAt(constantPoolIndex - 1);
        TreePath linkPath = constantPoolPath.pathByAddingChild(targetNode);
        
        return linkPath;
    }
    
}

