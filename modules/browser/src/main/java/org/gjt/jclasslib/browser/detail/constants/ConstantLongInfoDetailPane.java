/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.constants.ConstantLongInfo;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
    Detail pane showing a <tt>CONSTANT_Long</tt> constant pool entry.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ConstantLongInfoDetailPane extends AbstractConstantInfoDetailPane {

    // Visual components
    
    private ExtendedJLabel lblHighBytes;
    private ExtendedJLabel lblLowBytes;
    private ExtendedJLabel lblLong;

    /**
        Constructor.
        @param services the associated browser services.
     */
    public ConstantLongInfoDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void addLabels() {
        
        addDetailPaneEntry(normalLabel("High bytes:"),
                           lblHighBytes = highlightLabel());

        addDetailPaneEntry(normalLabel("Low bytes:"),
                           lblLowBytes = highlightLabel());
        
        addDetailPaneEntry(normalLabel("Long:"),
                           lblLong = highlightLabel());

    }

    public void show(TreePath treePath) {
        
        ConstantLongInfo entry = getConstant(treePath, ConstantLongInfo.class);
        lblHighBytes.setText(entry.getFormattedHighBytes());
        lblLowBytes.setText(entry.getFormattedLowBytes());
        lblLong.setText(entry.getLong());

        super.show(treePath);
    }
    
}

