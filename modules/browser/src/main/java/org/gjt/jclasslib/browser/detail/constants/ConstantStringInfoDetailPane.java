/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.constants.ConstantStringInfo;
import org.gjt.jclasslib.util.ExtendedJLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

/**
    Detail pane showing a <tt>CONSTANT_String</tt> constant pool entry.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ConstantStringInfoDetailPane extends AbstractConstantInfoDetailPane<ConstantStringInfo> {

    // Visual components
    
    private ExtendedJLabel lblString;
    private ExtendedJLabel lblStringVerbose;
    
    /**
        Constructor.
        @param services the associated browser services.
     */
    public ConstantStringInfoDetailPane(BrowserServices services) {
        super(services);
    }

    @NotNull
    @Override
    public Class<ConstantStringInfo> getConstantClass() {
        return ConstantStringInfo.class;
    }

    protected void addLabels() {
        
        addDetailPaneEntry(normalLabel("String:"),
                           lblString = linkLabel(),
                           lblStringVerbose = highlightLabel());
    }

    public void show(TreePath treePath) {
        
        ConstantStringInfo entry = getConstant(treePath);

        constantPoolHyperlink(lblString,
                              lblStringVerbose,
                              entry.getStringIndex());

        super.show(treePath);
    }
    
}

