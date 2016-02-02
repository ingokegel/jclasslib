/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.constants.ConstantIntegerInfo;
import org.gjt.jclasslib.util.ExtendedJLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

/**
    Detail pane showing a <tt>CONSTANT_Integer</tt> constant pool entry.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ConstantIntegerInfoDetailPane extends AbstractConstantInfoDetailPane<ConstantIntegerInfo> {

    // Visual components
    
    private ExtendedJLabel lblBytes;
    private ExtendedJLabel lblInt;

    /**
        Constructor.
        @param services the associated browser services.
     */
    public ConstantIntegerInfoDetailPane(BrowserServices services) {
        super(services);
    }

    @NotNull
    @Override
    public Class<ConstantIntegerInfo> getConstantClass() {
        return ConstantIntegerInfo.class;
    }

    protected void addLabels() {
        
        addDetailPaneEntry(normalLabel("Bytes:"),
                           lblBytes = highlightLabel());

        addDetailPaneEntry(normalLabel("Integer:"),
                           lblInt = highlightLabel());

    }

    public void show(TreePath treePath) {
        
        ConstantIntegerInfo entry = getConstant(treePath);
        lblBytes.setText(entry.getFormattedBytes());
        lblInt.setText(entry.getInt());

        super.show(treePath);
    }
    
}

