/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.mdi.*;
import org.gjt.jclasslib.io.*;
import org.gjt.jclasslib.structures.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.*;

/**
    Visual component displaying a class file.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2001-05-31 13:16:00 $
*/
public class BrowserComponent extends JComponent
                              implements TreeSelectionListener

{

    private BrowserHistory history;
    private BrowserServices services;

    // Visual Components
    
    private JSplitPane splitPane;
    private BrowserTreePane treePane;
    private BrowserDetailPane detailPane;
    
    public BrowserComponent(BrowserServices services) {

        this.services = services;
        setupComponent();
    }

    /**
        Get the pane containing the tree structure for the shown class file.
        @return the pane
     */
    public BrowserTreePane getTreePane() {
        return treePane;
    }
    
    /**
        Get the pane containing the detail area for the specific tree node selected
        in the <tt>BrowserTreePane</tt>.
        @return the pane
     */
    public BrowserDetailPane getDetailPane() {
        return detailPane;
    }
    
    /**
        Get the navigation history of this child window.
        @return the history
     */
    public BrowserHistory getHistory() {
        return history;
    }
    
    /**
        Rebuild tree view and clear history.
     */
    public void rebuild() {
        treePane.rebuild();
        history.clear();
        initialSelection();
    }

    public void valueChanged(TreeSelectionEvent selectionEvent) {
        
        services.activate();
        
        TreePath selectedPath = selectionEvent.getPath();
        
        history.updateHistory(selectedPath);
        showDetailPaneForPath(selectedPath);
        
    }
    
    private void showDetailPaneForPath(TreePath path) {
        
        BrowserMutableTreeNode node = (BrowserMutableTreeNode)path.getLastPathComponent();
        String nodeType = node.getType();
        detailPane.showPane(nodeType, path);
    }
    
    
    private void setupComponent() {

        setLayout(new BorderLayout());

        detailPane = new BrowserDetailPane(services);
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   buildTreePane(),
                                   detailPane);

        add(splitPane, BorderLayout.CENTER);
        
    }
    
    private BrowserTreePane buildTreePane() {
        
        treePane = new BrowserTreePane(services);
        
        JTree treeView = treePane.getTreeView();
        treeView.addTreeSelectionListener(this);
        history = new BrowserHistory(services);
        
        initialSelection();
        
        return treePane;
    }
    
    private void initialSelection() {

        JTree treeView = treePane.getTreeView();

        BrowserMutableTreeNode rootNode = (BrowserMutableTreeNode)treeView.getModel().getRoot();
        treeView.setSelectionPath(new TreePath(new Object[] {rootNode, rootNode.getFirstChild()}));
    }

}
