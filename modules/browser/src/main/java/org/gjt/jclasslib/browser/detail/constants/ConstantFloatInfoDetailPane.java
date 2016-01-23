/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.constants.ConstantFloatInfo;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
    Detail pane showing a <tt>CONSTANT_Float</tt> constant pool entry.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ConstantFloatInfoDetailPane extends AbstractConstantInfoDetailPane {

    // Visual components
    
    private ExtendedJLabel lblBytes;
    private ExtendedJLabel lblFloat;

    /**
        Constructor.
        @param services the associated browser services.
     */
    public ConstantFloatInfoDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("Bytes:"),
                           lblBytes = highlightLabel());

        addDetailPaneEntry(normalLabel("Float:"),
                           lblFloat = highlightLabel());

    }

    public void show(TreePath treePath) {
        ConstantFloatInfo entry = getConstant(treePath, ConstantFloatInfo.class);
        lblBytes.setText(entry.getFormattedBytes());
        lblFloat.setText(entry.getFloat());

        super.show(treePath);
    }
    
}

