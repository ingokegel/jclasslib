/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.*;
import org.gjt.jclasslib.structures.*;
import org.gjt.jclasslib.structures.constants.*;
import org.gjt.jclasslib.util.*;

import javax.swing.tree.*;

/**
    Detail pane showing a <tt>CONSTANT_String</tt> constant pool entry.

    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2001-05-31 13:15:25 $
*/
public class ConstantStringInfoDetailPane extends AbstractConstantInfoDetailPane {

    // Visual components
    
    private ExtendedJLabel lblString;
    private ExtendedJLabel lblStringVerbose;
    
    public ConstantStringInfoDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("String:"),
                           lblString = linkLabel(),
                           lblStringVerbose = highlightLabel());
    }

    public void show(TreePath treePath) {
        
        int constantPoolIndex = constantPoolIndex(treePath);

        try {
            ConstantStringInfo entry = (ConstantStringInfo)services.getClassFile().getConstantPoolEntry(constantPoolIndex, ConstantStringInfo.class);

            constantPoolHyperlink(lblString,
                                  lblStringVerbose,
                                  entry.getStringIndex());
        
        } catch (InvalidByteCodeException ex) {
            lblStringVerbose.setText(MESSAGE_INVALID_CONSTANT_POOL_ENTRY);
        }
        
        super.show(treePath);
    }
    
}

