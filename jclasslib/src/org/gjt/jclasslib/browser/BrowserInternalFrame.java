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
    A child window of the bytecode browser application.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:22 $
*/
public class BrowserInternalFrame extends BasicInternalFrame
                                  implements TreeSelectionListener {


    private File file;
    private ClassFile classFile;
    private BrowserHistory history;

    private boolean valid;
    private Exception exception;

    // Visual Components
    
    private JSplitPane splitPane;
    private BrowserTreePane treePane;
    private BrowserDetailPane detailPane;
    
    public BrowserInternalFrame(BasicDesktopManager desktopManager, String fileName) {
        this(desktopManager, new File(fileName));
    }

    public BrowserInternalFrame(BasicDesktopManager desktopManager, File file) {
        super(desktopManager, file.getAbsolutePath());
        this.file = file;
        
        readClassFile();
        setupInternalFrame();
    }

    public String getInitParam() {
        return file.getAbsolutePath();
    }
    
    /**
        Get the <tt>File</tt> object for the show class file.
        @return the <tt>File</tt> object
     */
    public File getFile() {
        return file;
    }
    
    /**
        Get the <tt>ClassFile</tt> object for the show class file.
        @return the <tt>ClassFile</tt> object
     */
    public ClassFile getClassFile() {
        return classFile;
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
        Reload the class file and update the display.
     */
    public void reload() {
        readClassFile();
        treePane.rebuild();
        history.clear();
        initialSelection();
    }

    public void valueChanged(TreeSelectionEvent selectionEvent) {
        
        // force sync of toolbar state with this frame
        desktopManager.getDesktopPane().setSelectedFrame(this);
        
        TreePath selectedPath = selectionEvent.getPath();
        
        history.updateHistory(selectedPath);
        showDetailPaneForPath(selectedPath);
        
    }
    
    private void showDetailPaneForPath(TreePath path) {
        
        BrowserMutableTreeNode node = (BrowserMutableTreeNode)path.getLastPathComponent();
        String nodeType = node.getType();
        detailPane.showPane(nodeType, path);
    }
    
    private BrowserMDIFrame getParentFrame() {
        return (BrowserMDIFrame)desktopManager.getParentFrame();
    }
    
    private void readClassFile() {
        valid = false;
        try {
            classFile = ClassFileReader.readFromFile(file);
            valid = true;
        } catch (InvalidByteCodeException ex) {
            exception = ex;
        } catch (IOException ex) {
            exception = ex;
        }
    }
    
    private void setupInternalFrame() {

        setTitle(file.getAbsolutePath());

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        detailPane = new BrowserDetailPane(this);
        
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                   buildTreePane(),
                                   detailPane);

        contentPane.add(splitPane, BorderLayout.CENTER);
        
    }
    
    private BrowserTreePane buildTreePane() {
        
        treePane = new BrowserTreePane(this);
        
        JTree treeView = treePane.getTreeView();
        treeView.addTreeSelectionListener(this);
        history = new BrowserHistory(getParentFrame(), treeView, detailPane);
        
        initialSelection();
        
        return treePane;
    }
    
    private void initialSelection() {

        JTree treeView = treePane.getTreeView();

        BrowserMutableTreeNode rootNode = (BrowserMutableTreeNode)treeView.getModel().getRoot();
        treeView.setSelectionPath(new TreePath(new Object[] {rootNode, rootNode.getFirstChild()}));
    }

}
