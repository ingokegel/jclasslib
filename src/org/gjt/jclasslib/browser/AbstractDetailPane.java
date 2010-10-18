/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.structures.*;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.HashMap;

/**
    Base class for all detail panes showing specific information for
    a specific tree node selected in <tt>BrowserTreePane</tt>.
    
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.5 $ $Date: 2003-08-18 08:06:31 $
*/
public abstract class AbstractDetailPane extends JPanel {
    
    /** Text prepended to constant pool hyperlinks. */
    public static final String CPINFO_LINK_TEXT = "cp_info #";
    /** Color for hyperlinks. */
    public static final Color COLOR_LINK = new Color(0, 128, 0);

    /** Color for highlighted text (values in key-value pairs). */
    protected static final Color COLOR_HIGHLIGHT = new Color(128, 0, 0);

    /** Services for this detail pane. */
    protected BrowserServices services;

    private HashMap labelToMouseListener = new HashMap();
 
    /**
        Constructs a detail pane with a specified parent frame.
        @param services browser services
     */
    protected AbstractDetailPane(BrowserServices services) {
        this.services = services;
        setupComponent();
    }

    /**
        Get the associated <tt>BrowserServices</tt> object.
        @return the browser services
     */
    public BrowserServices getBrowserServices() {
        return services;
    }

    /**
        Show the detail pane for a specific tree node 
        selected in <tt>BrowserTreePane</tt>.
        @param treePath the <tt>TreePath</tt> for the selection
                        in <tt>BrowserTreePane</tt>
     */
    public abstract void show(TreePath treePath);

    /**
        Setup the detail pane at the beginning of its life cycle.
     */
    protected abstract void setupComponent();

    /**
        Create a normal label (keys in key-value pairs).
        @return the label
     */
    protected ExtendedJLabel normalLabel() {
        return normalLabel("");
    }

    /**
        Create a normal label (keys in key-value pairs).
        @param text the text for the label
        @return the label
     */
    protected ExtendedJLabel normalLabel(String text) {
        ExtendedJLabel label = new ExtendedJLabel(text);
        return label;
    }

    /**
        Create a highlighted label (values in key-value pairs).
        @return the label
     */
    protected ExtendedJLabel highlightLabel() {
        ExtendedJLabel label = normalLabel();
        label.setForeground(COLOR_HIGHLIGHT);
        return label;
    }
    
    /**
        Create a label with the appearance of a hyperlink.
        @return the label
     */
    protected ExtendedJLabel linkLabel() {
        ExtendedJLabel label = normalLabel();
        label.setForeground(COLOR_LINK);
        label.setRequestFocusEnabled(true);
        label.setUnderlined(true);
        return label;
    }
    
    /**
        Determine the index of the tree node selected in <tt>BrowserTreePane</tt>
        among its siblings.
        @param treePath the tree path
        @return the index
     */
    protected int getIndex(TreePath treePath) {
        return ((BrowserTreeNode)treePath.getLastPathComponent()).getIndex();
    }
    
    /**
        Find the attribute pertaining to a specific tree path. 
        @param path the tree path
        @return the attribute
     */
    protected AttributeInfo findAttribute(TreePath path) {
        
        TreePath parentPath = path.getParentPath();
        BrowserTreeNode parentNode = (BrowserTreeNode)parentPath.getLastPathComponent();
        String parentNodeType = parentNode.getType();
        
        ClassFile classFile = services.getClassFile();
        int parentIndex = getIndex(parentPath);
        int index = getIndex(path);
        
        if (parentNodeType.equals(BrowserTreeNode.NODE_NO_CONTENT)) {
            return classFile.getAttributes()[index];

        } else if (parentNodeType.equals(BrowserTreeNode.NODE_FIELD)) {
            return classFile.getFields()[parentIndex].getAttributes()[index];

        } else if (parentNodeType.equals(BrowserTreeNode.NODE_METHOD)) {
            return classFile.getMethods()[parentIndex].getAttributes()[index];

        } else {
            return findAttribute(parentPath).getAttributes()[index];
        }
    }
    
    /**
        Get the name of a constant pool entry.
        @param constantPoolIndex the index of the constant pool entry
        @return the name
     */
    protected String getConstantPoolEntryName(int constantPoolIndex) {

        try {
            return services.getClassFile().getConstantPoolEntryName(constantPoolIndex);
        } catch (InvalidByteCodeException ex) {
            return "invalid constant pool reference";
        }
    }
    
    /**
        Construct a hyperlink into the constant pool.
        @param value the label for the hyperlink source
        @param comment an oprional label whose text is automatically set to
                       the name of the constant pool entry
        @param constantPoolIndex the index of the constant pool entry for the
                                 target of the hyperlink
     */
    protected void constantPoolHyperlink(ExtendedJLabel value,
                                         ExtendedJLabel comment,
                                         int constantPoolIndex) {
                                         
        value.setText(CPINFO_LINK_TEXT + constantPoolIndex);
        setupMouseListener(value, constantPoolIndex);
        value.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        if (comment != null) {
            comment.setToolTipText(comment.getText());
            comment.setText("<" + getConstantPoolEntryName(constantPoolIndex) + ">");
        }

    }
    
    private void setupMouseListener(ExtendedJLabel value, int constantPoolIndex) {

        MouseListener oldListener = (MouseListener)labelToMouseListener.get(value);
        if (oldListener != null) {
            value.removeMouseListener(oldListener);
        }
        MouseListener newListener = new ConstantPoolHyperlinkListener(
                                        services,
                                        constantPoolIndex);

        value.addMouseListener(newListener);
        labelToMouseListener.put(value, newListener);
    }
}

