/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.constants.ConstantMethodHandleInfo;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
 * Detail pane showing a <tt>CONSTANT_MethodHandle</tt> constant pool entry.
 */
public class ConstantMethodHandleDetailPane extends AbstractConstantInfoDetailPane {

    // Visual components

    private ExtendedJLabel lblKind;
    private ExtendedJLabel lblKindVerbose;
    private ExtendedJLabel lblReference;
    private ExtendedJLabel lblReferenceVerbose;

    private ClassElementOpener classElementOpener;

    /**
     * Constructor.
     *
     * @param services the associated browser services.
     */
    public ConstantMethodHandleDetailPane(BrowserServices services) {
        super(services);
    }

    protected void setupLabels() {

        addDetailPaneEntry(normalLabel("Kind:"),
            lblKind = normalLabel(),
            lblKindVerbose = highlightLabel());

        addDetailPaneEntry(normalLabel("Reference:"),
            lblReference = linkLabel(),
            lblReferenceVerbose = highlightLabel());
    }

    protected int addSpecial(int gridy) {

        classElementOpener = new ClassElementOpener(this);
        if (getBrowserServices().canOpenClassFiles()) {
            return classElementOpener.addSpecial(this, gridy);
        } else {
            return 0;
        }
    }

    public void show(TreePath treePath) {

        int constantPoolIndex = constantPoolIndex(treePath);

        try {
            ConstantMethodHandleInfo entry = (ConstantMethodHandleInfo)services.getClassFile().getConstantPoolEntry(constantPoolIndex, ConstantMethodHandleInfo.class);
            classElementOpener.setCPInfo(entry);

            lblKind.setText(entry.getType().getTag());
            switch (entry.getType()) {
                case GET_FIELD:
                    lblKindVerbose.setText("REF_getField");
                    break;
                case GET_STATIC:
                    lblKindVerbose.setText("REF_getStatic");
                    break;
                case PUT_FIELD:
                    lblKindVerbose.setText("REF_putField");
                    break;
                case PUT_STATIC:
                    lblKindVerbose.setText("REF_putStatic");
                    break;
                case INVOKE_VIRTUAL:
                    lblKindVerbose.setText("REF_invokeVirtual");
                    break;
                case INVOKE_STATIC:
                    lblKindVerbose.setText("REF_invokeStatic");
                    break;
                case INVOKE_SPECIAL:
                    lblKindVerbose.setText("REF_invokeSpecial");
                    break;
                case NEW_INVOKE_SPECIAL:
                    lblKindVerbose.setText("REF_newInvokeSpecial");
                    break;
                case INVOKE_INTERFACE:
                    lblKindVerbose.setText("REF_invokeInterface");
                    break;
            }

            constantPoolHyperlink(lblReference,
                lblReferenceVerbose,
                entry.getReferenceIndex());

        } catch (InvalidByteCodeException ex) {
            lblKindVerbose.setText(MESSAGE_INVALID_CONSTANT_POOL_ENTRY);
        }

        super.show(treePath);
    }

}

