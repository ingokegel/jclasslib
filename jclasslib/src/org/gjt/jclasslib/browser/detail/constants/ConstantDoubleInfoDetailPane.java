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
    Detail pane showing a <tt>CONSTANT_Double</tt> constant pool entry.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:23 $
*/
public class ConstantDoubleInfoDetailPane extends AbstractConstantInfoDetailPane {

    // Visual components
    
    private ExtendedJLabel lblHighBytes;
    private ExtendedJLabel lblLowBytes;
    private ExtendedJLabel lblDouble;
    private ExtendedJLabel lblComment;
    
    public ConstantDoubleInfoDetailPane(BrowserInternalFrame parentFrame) {
        super(parentFrame);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("High bytes:"),
                           lblHighBytes = highlightLabel());

        addDetailPaneEntry(normalLabel("Low bytes:"),
                           lblLowBytes = highlightLabel());
        
        addDetailPaneEntry(normalLabel("Double:"),
                           lblDouble = highlightLabel(),
                           lblComment = highlightLabel());

    }

    public void show(TreePath treePath) {
        
        int constantPoolIndex = constantPoolIndex(treePath);

        try {
            ConstantDoubleInfo entry = (ConstantDoubleInfo)parentFrame.getClassFile().getConstantPoolEntry(constantPoolIndex, ConstantDoubleInfo.class);
            lblHighBytes.setText(entry.getFormattedHighBytes());
            lblLowBytes.setText(entry.getFormattedLowBytes());
            lblDouble.setText(entry.getDouble());
        } catch (InvalidByteCodeException ex) {
            lblComment.setText(MESSAGE_INVALID_CONSTANT_POOL_ENTRY);
        }
        
        super.show(treePath);
    }
    
}

