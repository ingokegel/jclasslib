/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.AbstractDetailPane;
import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.util.ExtendedJLabel;
import org.gjt.jclasslib.util.GUIHelper;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

/**
    Base class for all detail panes with a structure of
    a fixed number of key-value pairs arranged in a list.
    
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.5 $ $Date: 2003-08-18 08:12:02 $
*/
public abstract class FixedListDetailPane extends AbstractDetailPane {
    
    // Visual components

    private java.util.List detailPaneEntries;
    private JScrollPane scrollPane;

    /**
        Constructor.
        @param services the associated browser services.
     */
    protected FixedListDetailPane(BrowserServices services) {
        super(services);
    }

    /**
        Add a detail entry consisting of a key value pair.
        @param key the key label
        @param value the value label
     */
    protected void addDetailPaneEntry(ExtendedJLabel key, ExtendedJLabel value) {
        addDetailPaneEntry(key, value, null);
    }

    /**
        Add a detail entry consisting of a key value pair with a associated comment
        which is made scrollable.
        @param key the key label
        @param value the value label
        @param comment the comment
     */
    protected void addDetailPaneEntry(ExtendedJLabel key,
                                      ExtendedJLabel value,
                                      ExtendedJLabel comment) {
                                          
        if (detailPaneEntries == null) {
            detailPaneEntries = new ArrayList();
        }
        
        detailPaneEntries.add(
                new DetailPaneEntry(key, value, comment)
            );
    }
    
    protected void setupComponent() {
        
        setupLabels();
        
        setLayout(new GridBagLayout());
        
        GridBagConstraints gKey = new GridBagConstraints();
        gKey.anchor = GridBagConstraints.NORTHWEST;
        gKey.insets = new Insets(1,10,0,10);
        
        GridBagConstraints gValue = new GridBagConstraints();
        gValue.gridx = 1;
        gValue.anchor = GridBagConstraints.NORTHEAST;
        gValue.insets = new Insets(1,0,0,5);

        GridBagConstraints gComment = new GridBagConstraints();
        gComment.gridx = 2;
        gComment.anchor = GridBagConstraints.NORTHWEST;
        gComment.insets = new Insets(1,0,0,5);
        gComment.fill = GridBagConstraints.HORIZONTAL;
        
        GridBagConstraints gCommentOnly = (GridBagConstraints)gComment.clone();
        gCommentOnly.gridx = 1;
        gCommentOnly.gridwidth = 2;
        
        GridBagConstraints gRemainder = new GridBagConstraints();
        gRemainder.gridx = 2;
        gRemainder.weightx = gRemainder.weighty = 1;
        gRemainder.fill = GridBagConstraints.BOTH;

        Iterator it = detailPaneEntries.iterator();
        while (it.hasNext()) {
            DetailPaneEntry entry = (DetailPaneEntry)it.next();
            if (entry == null) {
                continue;
            }

            gComment.gridy = gValue.gridy = ++gKey.gridy;
            if (entry.key != null) {
                add(entry.key, gKey);
            }
            if (entry.value != null) {
                add(entry.value, gValue);
            }
            if (entry.comment != null) {
                add(entry.comment, (entry.value == null) ? gCommentOnly : gComment);

                entry.comment.setAutoTooltip(true);
            }
            
        }

        gRemainder.gridy = gKey.gridy + 1;
        gRemainder.gridy += addSpecial(gRemainder.gridy);

        add(new JPanel(), gRemainder);
        scrollPane = new JScrollPane(this);
        GUIHelper.setDefaultScrollbarUnits(scrollPane);
        scrollPane.setBorder(null);

    }

    /**
        Get the scroll pane.
        @return the scroll pane.
     */
    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void show(TreePath treePath) {

        scrollPane.getViewport().setViewPosition(new Point(0, 0));

    }

    /**
        Setup all label and fill the <tt>detailPaneEntries</tt> list so that
        <tt>setupComponent</tt> can layout the pane.
     */
    protected abstract void setupLabels();

    /**
        Hook for derived classes to add additional visual elements.
        @param gridy the current <tt>gridy</tt> of the <tt>GridbagLayout</tt>.
        @return the number of added rows.
     */
    protected int addSpecial(int gridy) {
        return 0;
    }

    private static class DetailPaneEntry {
        public final ExtendedJLabel key;
        public final ExtendedJLabel value;
        public final ExtendedJLabel comment;
        
        private DetailPaneEntry(ExtendedJLabel key,
                                ExtendedJLabel value,
                                ExtendedJLabel comment) {
            this.key = key;
            this.value = value;
            this.comment = comment;
        }
    }

    
}

