/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.constants.ConstantMethodTypeInfo;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
 * Detail pane showing a <tt>CONSTANT_MethodType</tt> constant pool entry.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
 * @version $Revision: 1.5 $ $Date: 2003-08-18 08:16:34 $
 */
public class ConstantMethodTypeDetailPane extends AbstractConstantInfoDetailPane {

    // Visual components

    private ExtendedJLabel lblDescriptor;
    private ExtendedJLabel lblDescriptorVerbose;

    /**
     * Constructor.
     *
     * @param services the associated browser services.
     */
    public ConstantMethodTypeDetailPane(BrowserServices services) {
        super(services);
    }

    protected void setupLabels() {

        addDetailPaneEntry(normalLabel("Descriptor:"),
            lblDescriptor = linkLabel(),
            lblDescriptorVerbose = highlightLabel());
    }

    public void show(TreePath treePath) {

        int constantPoolIndex = constantPoolIndex(treePath);

        try {
            ConstantMethodTypeInfo entry = (ConstantMethodTypeInfo)services.getClassFile().getConstantPoolEntry(constantPoolIndex, ConstantMethodTypeInfo.class);

            constantPoolHyperlink(lblDescriptor,
                lblDescriptorVerbose,
                entry.getDescriptorIndex());

        } catch (InvalidByteCodeException ex) {
            lblDescriptorVerbose.setText(MESSAGE_INVALID_CONSTANT_POOL_ENTRY);
        }

        super.show(treePath);
    }

}

