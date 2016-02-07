/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.BrowserTreeNode;
import org.gjt.jclasslib.structures.AnnotationData;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
 * Class for showing an annotation node.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class AnnotationDetailPane extends FixedListDetailPane {

    private ExtendedJLabel lblType;
    private ExtendedJLabel lblTypeVerbose;

    private ExtendedJLabel lblValuePairEntries;

    public AnnotationDetailPane(BrowserServices services) {
        super(services);
    }

    protected void addLabels() {

        addDetailPaneEntry(normalLabel("Type:"),
                lblType = linkLabel(),
                lblTypeVerbose = highlightLabel());

        addDetailPaneEntry(normalLabel("Number of entries:"),
                lblValuePairEntries = highlightLabel());
    }

    public void show(TreePath treePath) {
        AnnotationData annotation = (AnnotationData)((BrowserTreeNode)treePath.getLastPathComponent()).getElement();

        constantPoolHyperlink(lblType,
                lblTypeVerbose,
                annotation.getTypeIndex());

        lblValuePairEntries.setText(String.valueOf(annotation.getElementValuePairEntries().length));

        super.show(treePath);
    }
}
