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
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:21 $
*/
public class ConstantPoolHyperlinkListener extends MouseAdapter {

    private BrowserInternalFrame parentFrame;
    private int constantPoolIndex; 
    
    public ConstantPoolHyperlinkListener(BrowserInternalFrame parentFrame, int constantPoolIndex) {
        
        this.parentFrame = parentFrame;
        this.constantPoolIndex = constantPoolIndex;
    }
    
    public void mouseClicked(MouseEvent event) {
        link(parentFrame, constantPoolIndex);
    }

    /**
        Link to a specific constant pool entry.
        @param parentFrame the frame in which the link is to be performed
        @param constantPoolIndex the index of the constant pool entry
     */
    public static void link(BrowserInternalFrame parentFrame, int constantPoolIndex) {
        
        JTree treeView = parentFrame.getTreePane().getTreeView();
        TreePath newPath = linkPath(parentFrame, constantPoolIndex);
        treeView.setSelectionPath(newPath);
        treeView.scrollPathToVisible(newPath);
    }
    
    private static TreePath linkPath(BrowserInternalFrame parentFrame, int constantPoolIndex) {
        
        TreePath constantPoolPath = parentFrame.getTreePane().getConstantPoolPath();
        
        BrowserMutableTreeNode constantPoolNode = (BrowserMutableTreeNode)constantPoolPath.getLastPathComponent();
        TreeNode targetNode = constantPoolNode.getChildAt(constantPoolIndex - 1);
        TreePath linkPath = constantPoolPath.pathByAddingChild(targetNode);
        
        return linkPath;
    }
    
}

