/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.constants.ConstantMethodHandleInfo;
import org.gjt.jclasslib.util.ExtendedJLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

public class ConstantMethodHandleInfoDetailPane extends AbstractConstantInfoDetailPane<ConstantMethodHandleInfo> {

    private ExtendedJLabel lblKind;
    private ExtendedJLabel lblReference;
    private ExtendedJLabel lblReferenceVerbose;

    public ConstantMethodHandleInfoDetailPane(BrowserServices services) {
        super(services);
    }

    @Override
    protected void addLabels() {
        addDetailPaneEntry(normalLabel("Reference kind:"),
            lblKind = normalLabel());

        addDetailPaneEntry(normalLabel("Reference index:"),
            lblReference = linkLabel(),
            lblReferenceVerbose = highlightLabel());
    }

    @NotNull
    @Override
    public Class<ConstantMethodHandleInfo> getConstantClass() {
        return ConstantMethodHandleInfo.class;
    }

    @Override
    public void show(TreePath treePath) {

        ClassFile classFile = getServices().getClassFile();
        ConstantMethodHandleInfo entry = getConstant(treePath);

        lblKind.setText(entry.getType().getVerbose());
        constantPoolHyperlink(lblReference,
            lblReferenceVerbose,
            entry.getReferenceIndex());


        super.show(treePath);

    }
}
