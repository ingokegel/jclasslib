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
    Detail pane showing a <tt>CONSTANT_Long</tt> constant pool entry.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.3 $ $Date: 2002-02-27 16:47:43 $
*/
public class ConstantLongInfoDetailPane extends AbstractConstantInfoDetailPane {

    // Visual components
    
    private ExtendedJLabel lblHighBytes;
    private ExtendedJLabel lblLowBytes;
    private ExtendedJLabel lblLong;
    private ExtendedJLabel lblComment;
    
    public ConstantLongInfoDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("High bytes:"),
                           lblHighBytes = highlightLabel());

        addDetailPaneEntry(normalLabel("Low bytes:"),
                           lblLowBytes = highlightLabel());
        
        addDetailPaneEntry(normalLabel("Long:"),
                           lblLong = highlightLabel(),
                           lblComment = highlightLabel());

    }

    public void show(TreePath treePath) {
        
        int constantPoolIndex = constantPoolIndex(treePath);

        try {
            ConstantLongInfo entry = (ConstantLongInfo)services.getClassFile().getConstantPoolEntry(constantPoolIndex, ConstantLongInfo.class);
            lblHighBytes.setText(entry.getFormattedHighBytes());
            lblLowBytes.setText(entry.getFormattedLowBytes());
            lblLong.setText(entry.getLong());
        } catch (InvalidByteCodeException ex) {
            lblComment.setText(MESSAGE_INVALID_CONSTANT_POOL_ENTRY);
        }
        
        super.show(treePath);
    }
    
}

