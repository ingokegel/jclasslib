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
    Detail pane showing a <tt>CONSTANT_Integer</tt> constant pool entry.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2001-05-31 13:15:25 $
*/
public class ConstantIntegerInfoDetailPane extends AbstractConstantInfoDetailPane {

    // Visual components
    
    private ExtendedJLabel lblBytes;
    private ExtendedJLabel lblInt;
    private ExtendedJLabel lblComment;
    
    public ConstantIntegerInfoDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("Bytes:"),
                           lblBytes = highlightLabel());

        addDetailPaneEntry(normalLabel("Integer:"),
                           lblInt = highlightLabel(),
                           lblComment = highlightLabel());

    }

    public void show(TreePath treePath) {
        
        int constantPoolIndex = constantPoolIndex(treePath);

        try {
            ConstantIntegerInfo entry = (ConstantIntegerInfo)services.getClassFile().getConstantPoolEntry(constantPoolIndex, ConstantIntegerInfo.class);
            lblBytes.setText(entry.getFormattedBytes());
            lblInt.setText(entry.getInt());
        } catch (InvalidByteCodeException ex) {
            lblComment.setText(MESSAGE_INVALID_CONSTANT_POOL_ENTRY);
        }
        
        super.show(treePath);
    }
    
}

