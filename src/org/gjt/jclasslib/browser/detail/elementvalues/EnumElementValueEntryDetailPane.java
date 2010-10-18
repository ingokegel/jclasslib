/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail.elementvalues;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.BrowserTreeNode;
import org.gjt.jclasslib.browser.detail.FixedListDetailPane;
import org.gjt.jclasslib.structures.elementvalues.EnumElementValue;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
 * Class for showing element value entry of type Enum.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 * @version $Revision: 1.1 $ $Date: 2004-12-28 13:04:30 $
 */
public class EnumElementValueEntryDetailPane extends FixedListDetailPane {

    private ExtendedJLabel lblTypeNameIndex;
    private ExtendedJLabel lblTypeNameIndexVerbose;
    private ExtendedJLabel lblConstNameIndex;
    private ExtendedJLabel lblConstNameIndexVerbose;

    public EnumElementValueEntryDetailPane(BrowserServices services) {
        super(services);
    }

    protected void setupLabels() {
        addDetailPaneEntry(normalLabel("Type name:"),
                lblTypeNameIndex = linkLabel(),
                lblTypeNameIndexVerbose = highlightLabel());
        addDetailPaneEntry(normalLabel("Const name:"),
                lblConstNameIndex = linkLabel(),
                lblConstNameIndexVerbose = highlightLabel());
    }

    public void show(TreePath treePath) {
        EnumElementValue eeve = (EnumElementValue)
                ((BrowserTreeNode)treePath.getLastPathComponent()).getElement();

        constantPoolHyperlink(lblTypeNameIndex,
                lblTypeNameIndexVerbose,
                eeve.getTypeNameIndex());

        constantPoolHyperlink(lblConstNameIndex,
                lblConstNameIndexVerbose,
                eeve.getConstNameIndex());

        super.show(treePath);
    }

}
