/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
    Detail pane showing a <tt>CONSTANT_Utf8</tt> constant pool entry.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ConstantUtf8InfoDetailPane extends AbstractConstantInfoDetailPane {

    // Visual components
    
    private ExtendedJLabel lblByteLength;
    private ExtendedJLabel lblStringLength;
    private ExtendedJLabel lblString;
    
    /**
        Constructor.
        @param services the associated browser services.
     */
    public ConstantUtf8InfoDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("Length of byte array:"),
                           lblByteLength = highlightLabel());

        addDetailPaneEntry(normalLabel("Length of string:"),
                           lblStringLength = highlightLabel());
        
        addDetailPaneEntry(normalLabel("String:"),
                           null,
                           lblString = highlightLabel());

    }

    public void show(TreePath treePath) {
        
        ConstantUtf8Info entry = getConstant(treePath, ConstantUtf8Info.class);
        lblByteLength.setText(entry.getBytes().length);
        lblStringLength.setText(entry.getString().length());
        try {
            lblString.setText(entry.getVerbose());
        } catch (InvalidByteCodeException e) {
            lblString.setText(MESSAGE_INVALID_CONSTANT_POOL_ENTRY);
        }

        super.show(treePath);
    }
    
}

