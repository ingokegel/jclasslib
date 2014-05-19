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
package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.BrowserTreeNode;
import org.gjt.jclasslib.structures.attributes.TypeAnnotation;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
 * Class for showing a type annotation node.
 */
public class TypeAnnotationDetailPane extends FixedListDetailPane {

    private ExtendedJLabel lblType;
    private ExtendedJLabel lblTypeVerbose;

    private ExtendedJLabel lblInfo;

    public TypeAnnotationDetailPane(BrowserServices services) {
        super(services);
    }

    protected void setupLabels() {
        addDetailPaneEntry(normalLabel("Target Type:"),
            lblType = highlightLabel(),
            lblTypeVerbose = highlightLabel());

        addDetailPaneEntry(normalLabel("Target Info:"),
            lblInfo = highlightLabel());

    }

    public void show(TreePath treePath) {
        TypeAnnotation annotation = (TypeAnnotation)
            ((BrowserTreeNode)treePath.getLastPathComponent()).getElement();

        lblType.setText(annotation.getTargetType().toString());
        lblTypeVerbose.setText("<" + annotation.getTargetType() + ">");

        lblInfo.setText(annotation.getTargetInfo().getVerbose());


        super.show(treePath);
    }
}
