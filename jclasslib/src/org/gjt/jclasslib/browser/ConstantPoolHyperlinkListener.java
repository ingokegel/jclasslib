/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.event.*;

/**
    Listens for mouse clicks and manages linking into the constat pool.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2001-05-31 13:15:25 $
*/
public class ConstantPoolHyperlinkListener extends MouseAdapter {

    private BrowserServices services;
    private int constantPoolIndex; 
    
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
        
        JTree treeView = services.getBrowserComponent().getTreePane().getTreeView();
        TreePath newPath = linkPath(services, constantPoolIndex);
        treeView.setSelectionPath(newPath);
        treeView.scrollPathToVisible(newPath);
    }
    
    private static TreePath linkPath(BrowserServices services, int constantPoolIndex) {
        
        TreePath constantPoolPath = services.getBrowserComponent().getTreePane().getConstantPoolPath();
        
        BrowserMutableTreeNode constantPoolNode = (BrowserMutableTreeNode)constantPoolPath.getLastPathComponent();
        TreeNode targetNode = constantPoolNode.getChildAt(constantPoolIndex - 1);
        TreePath linkPath = constantPoolPath.pathByAddingChild(targetNode);
        
        return linkPath;
    }
    
}

