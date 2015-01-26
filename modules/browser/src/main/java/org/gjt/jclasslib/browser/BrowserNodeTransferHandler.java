/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class BrowserNodeTransferHandler extends TransferHandler {

    private BrowserServices services;

    public BrowserNodeTransferHandler(BrowserServices services) {
        this.services = services;
    }

    @Override
    public int getSourceActions(JComponent c) {
        return COPY;
    }

    @Override
    protected Transferable createTransferable(JComponent c) {

        if (!(c instanceof JTree)) {
            return null;
        }

        TreePath selectionPath = ((JTree)c).getSelectionPath();
        if (selectionPath == null) {
            return null;
        }

        AbstractDetailPane detailPane = services.getBrowserComponent().getDetailPane().getCurrentDetailPane();
        if (detailPane == null) {
            return null;
        }

        String text = detailPane.getClipboardText();
        if (text != null) {
            return new StringSelection(text);
        } else {
            return null;
        }

    }
}
