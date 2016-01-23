/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.util.ExtendedJLabel;
import org.gjt.jclasslib.util.HtmlDisplayTextArea;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseListener;
import java.util.HashMap;

/**
    Base class for all detail panes showing specific information for
    a specific tree node selected in <tt>BrowserTreePane</tt>.
    
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public abstract class AbstractDetailPane extends JPanel {
    
    /** Text prepended to constant pool hyperlinks. */
    public static final String CPINFO_LINK_TEXT = "cp_info #";

    /** Color for highlighted text (values in key-value pairs). */
    protected static final Color COLOR_HIGHLIGHT = new Color(128, 0, 0);

    /** Services for this detail pane. */
    protected BrowserServices services;

    private HashMap<ExtendedJLabel, MouseListener> labelToMouseListener = new HashMap<ExtendedJLabel, MouseListener>();
 
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

    public String getClipboardText() {
        return null;
    }

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
        return new ExtendedJLabel(text);
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
        Create a highlighted text area for HTML display (values in key-value pairs).
        @return the text area
     */
    protected HtmlDisplayTextArea highlightTextArea() {
        HtmlDisplayTextArea textArea = new HtmlDisplayTextArea();
        textArea.setForeground(COLOR_HIGHLIGHT);
        return textArea;
    }

    /**
        Create a label with the appearance of a hyperlink.
        @return the label
     */
    protected ExtendedJLabel linkLabel() {
        ExtendedJLabel label = normalLabel();
        label.setForeground(HtmlDisplayTextArea.COLOR_LINK);
        label.setRequestFocusEnabled(true);
        label.setUnderlined(true);
        return label;
    }
    
    protected Object getElement(TreePath treePath) {
        return ((BrowserTreeNode)treePath.getLastPathComponent()).getElement();
    }

    /**
        Find the attribute pertaining to a specific tree path. 
        @param path the tree path
        @return the attribute
     */
    protected AttributeInfo getAttribute(TreePath path) {
        return (AttributeInfo)getElement(path);
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
        @param comment an optional label whose text is automatically set to
                       the name of the constant pool entry
        @param constantPoolIndex the index of the constant pool entry for the
                                 target of the hyperlink
     */
    protected void constantPoolHyperlink(ExtendedJLabel value,
                                         ExtendedJLabel comment,
                                         int constantPoolIndex) {
                                         
        value.setText(CPINFO_LINK_TEXT + constantPoolIndex);
        setupMouseListener(value, new ConstantPoolHyperlinkListener(services, constantPoolIndex));
        value.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        applyComment(comment, constantPoolIndex);

    }

    protected void classAttributeIndexHyperlink(ExtendedJLabel value,
                                                ExtendedJLabel comment,
                                                int index,
                                                Class<? extends AttributeInfo> attributeInfoClass,
                                                String text) {

        value.setText(text + index);
        setupMouseListener(value, new ClassAttributeHyperlinkListener(services, index, attributeInfoClass));
        value.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        applyComment(comment, index);
    }

    private void applyComment(ExtendedJLabel comment, int constantPoolIndex) {
        if (comment != null) {
            comment.setToolTipText(comment.getText());
            comment.setText("<" + getConstantPoolEntryName(constantPoolIndex) + ">");
        }
    }

    private void setupMouseListener(ExtendedJLabel value, MouseListener mouseListener) {

        MouseListener oldListener = labelToMouseListener.get(value);
        if (oldListener != null) {
            value.removeMouseListener(oldListener);
        }
        value.addMouseListener(mouseListener);
        labelToMouseListener.put(value, mouseListener);
    }
}

