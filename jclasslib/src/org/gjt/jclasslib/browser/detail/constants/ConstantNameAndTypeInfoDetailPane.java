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
    Detail pane showing a <tt>CONSTANT_NameAndType</tt> constant pool entry.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.3 $ $Date: 2002-02-27 16:47:43 $
*/
public class ConstantNameAndTypeInfoDetailPane extends AbstractConstantInfoDetailPane {

    // Visual components
    
    private ExtendedJLabel lblName;
    private ExtendedJLabel lblNameVerbose;
    private ExtendedJLabel lblDescriptor;
    private ExtendedJLabel lblDescriptorVerbose;
    
    public ConstantNameAndTypeInfoDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("Name:"),
                           lblName = linkLabel(),
                           lblNameVerbose = highlightLabel());

        addDetailPaneEntry(normalLabel("Descriptor:"),
                           lblDescriptor = linkLabel(),
                           lblDescriptorVerbose = highlightLabel());
    }

    public void show(TreePath treePath) {
        
        int constantPoolIndex = constantPoolIndex(treePath);

        try {
            ConstantNameAndTypeInfo entry = (ConstantNameAndTypeInfo)services.getClassFile().getConstantPoolEntry(constantPoolIndex, ConstantNameAndTypeInfo.class);

            constantPoolHyperlink(lblName,
                                  lblNameVerbose,
                                  entry.getNameIndex());
        
            constantPoolHyperlink(lblDescriptor,
                                  lblDescriptorVerbose,
                                  entry.getDescriptorIndex());
        
        } catch (InvalidByteCodeException ex) {
            lblNameVerbose.setText(MESSAGE_INVALID_CONSTANT_POOL_ENTRY);
        }

        super.show(treePath);
        
    }
    
}

