/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.constants.ConstantNameAndTypeInfo;
import org.gjt.jclasslib.util.ExtendedJLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

public class ConstantNameAndTypeInfoDetailPane extends AbstractConstantInfoDetailPane<ConstantNameAndTypeInfo> {

    // Visual components
    
    private ExtendedJLabel lblName;
    private ExtendedJLabel lblNameVerbose;
    private ExtendedJLabel lblDescriptor;
    private ExtendedJLabel lblDescriptorVerbose;
    
    /**
        Constructor.
        @param services the associated browser services.
     */
    public ConstantNameAndTypeInfoDetailPane(BrowserServices services) {
        super(services);
    }

    @NotNull
    @Override
    public Class<ConstantNameAndTypeInfo> getConstantClass() {
        return ConstantNameAndTypeInfo.class;
    }

    protected void addLabels() {
        
        addDetailPaneEntry(normalLabel("Name:"),
                           lblName = linkLabel(),
                           lblNameVerbose = highlightLabel());

        addDetailPaneEntry(normalLabel("Descriptor:"),
                           lblDescriptor = linkLabel(),
                           lblDescriptorVerbose = highlightLabel());
    }

    public void show(TreePath treePath) {
        
        ConstantNameAndTypeInfo entry = getConstant(treePath);

        constantPoolHyperlink(lblName,
                              lblNameVerbose,
                              entry.getNameIndex());

        constantPoolHyperlink(lblDescriptor,
                              lblDescriptorVerbose,
                              entry.getDescriptorIndex());

        super.show(treePath);
        
    }
    
}

