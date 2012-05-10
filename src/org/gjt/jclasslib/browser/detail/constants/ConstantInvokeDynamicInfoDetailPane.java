/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.constants.ConstantInvokeDynamicInfo;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
    Detail pane showing a <tt>CONSTANT_Invoke_Dynamic</tt> constant pool entry.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ConstantInvokeDynamicInfoDetailPane extends AbstractConstantInfoDetailPane {

    // Visual components

    private ExtendedJLabel lblNameAndType;
    private ExtendedJLabel lblNameAndTypeVerbose;
    private ExtendedJLabel lblBootstrap;
    private ExtendedJLabel lblBootstrapVerbose;

    /**
        Constructor.
        @param services the associated browser services.
     */
    public ConstantInvokeDynamicInfoDetailPane(BrowserServices services) {
        super(services);
    }

    protected void setupLabels() {

        addDetailPaneEntry(normalLabel("Name and type:"),
                           lblNameAndType = linkLabel(),
                           lblNameAndTypeVerbose = highlightLabel());

        addDetailPaneEntry(normalLabel("Bootstrap method:"),
                           lblBootstrap = linkLabel(),
                           lblBootstrapVerbose = highlightLabel());
    }

    public void show(TreePath treePath) {

        int constantPoolIndex = constantPoolIndex(treePath);

        try {
            ConstantInvokeDynamicInfo entry = (ConstantInvokeDynamicInfo)services.getClassFile().getConstantPoolEntry(constantPoolIndex, ConstantInvokeDynamicInfo.class);

            constantPoolHyperlink(lblNameAndType,
                lblNameAndTypeVerbose,
                entry.getNameAndTypeIndex());

            constantPoolHyperlink(lblBootstrap,
                lblBootstrapVerbose,
                entry.getBootstrapMethodAttributeIndex());

        } catch (InvalidByteCodeException ex) {
            lblNameAndTypeVerbose.setText(MESSAGE_INVALID_CONSTANT_POOL_ENTRY);
        }

        super.show(treePath);

    }

}

