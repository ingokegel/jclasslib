/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.BrowserTreeNode;
import org.gjt.jclasslib.structures.elementvalues.AnnotationElementValue;
import org.gjt.jclasslib.structures.elementvalues.ElementValue;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
 * Class for showing an annotation node.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 * @version $Revision: 1.1 $ $Date: 2004-12-28 13:04:31 $
 */
public class AnnotationDetailPane extends FixedListDetailPane {

    private ExtendedJLabel lblTag;
    private ExtendedJLabel lblTagVerbose;

    private ExtendedJLabel lblType;
    private ExtendedJLabel lblTypeVerbose;

    private ExtendedJLabel lblValuePairEntries;

    public AnnotationDetailPane(BrowserServices services) {
        super(services);
    }

    protected void setupLabels() {
        addDetailPaneEntry(normalLabel("Tag:"),
                lblTag = highlightLabel(),
                lblTagVerbose = highlightLabel());

        addDetailPaneEntry(normalLabel("Type:"),
                lblType = linkLabel(),
                lblTypeVerbose = highlightLabel());

        addDetailPaneEntry(normalLabel("Number of entries:"),
                lblValuePairEntries = highlightLabel());
    }

    public void show(TreePath treePath) {
        AnnotationElementValue annotation = (AnnotationElementValue)
                ((BrowserTreeNode)treePath.getLastPathComponent()).getElement();

        lblTag.setText(String.valueOf((char)annotation.getTag()));
        lblTagVerbose.setText("<" + ElementValue.getTagDescription(annotation.getTag()) + ">");

        constantPoolHyperlink(lblType,
                lblTypeVerbose,
                annotation.getTypeIndex());

        lblValuePairEntries.setText(String.valueOf(annotation.getElementValuePairEntries().length));

        super.show(treePath);
    }
}
