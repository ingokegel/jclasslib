/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.constants.ConstantMethodTypeInfo;
import org.gjt.jclasslib.util.ExtendedJLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

/**
    Detail pane showing a <tt>CONSTANT_MethodType</tt> constant pool entry.
 
*/
public class ConstantMethodTypeDetailPane extends AbstractConstantInfoDetailPane<ConstantMethodTypeInfo> {

    // Visual components
    
    private ExtendedJLabel lblType;
    private ExtendedJLabel lblTypeVerbose;

    /**
        Constructor.
        @param services the associated browser services.
     */
    public ConstantMethodTypeDetailPane(BrowserServices services) {
        super(services);
    }

    @NotNull
    @Override
    public Class<ConstantMethodTypeInfo> getConstantClass() {
        return ConstantMethodTypeInfo.class;
    }

    protected void addLabels() {
        
        addDetailPaneEntry(normalLabel("Type:"),
                           lblType = linkLabel(),
                           lblTypeVerbose = highlightLabel());
    }


    public void show(TreePath treePath) {
        
        ConstantMethodTypeInfo entry = getConstant(treePath);

        constantPoolHyperlink(lblType,
                              lblTypeVerbose,
                              entry.getDescriptorIndex());

        super.show(treePath);
    }
    
}

