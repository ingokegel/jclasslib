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
    Detail pane showing a <tt>CONSTANT_Float</tt> constant pool entry.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2001-05-31 13:15:25 $
*/
public class ConstantFloatInfoDetailPane extends AbstractConstantInfoDetailPane {

    // Visual components
    
    private ExtendedJLabel lblBytes;
    private ExtendedJLabel lblFloat;
    private ExtendedJLabel lblComment;
    
    public ConstantFloatInfoDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("Bytes:"),
                           lblBytes = highlightLabel());

        addDetailPaneEntry(normalLabel("Float:"),
                           lblFloat = highlightLabel(),
                           lblComment = highlightLabel());

    }

    public void show(TreePath treePath) {
        
        int constantPoolIndex = constantPoolIndex(treePath);

        try {
            ConstantFloatInfo entry = (ConstantFloatInfo)services.getClassFile().getConstantPoolEntry(constantPoolIndex, ConstantFloatInfo.class);
            lblBytes.setText(entry.getFormattedBytes());
            lblFloat.setText(entry.getFloat());
        } catch (InvalidByteCodeException ex) {
            lblComment.setText(MESSAGE_INVALID_CONSTANT_POOL_ENTRY);
        }
        
        super.show(treePath);
    }
    
}

