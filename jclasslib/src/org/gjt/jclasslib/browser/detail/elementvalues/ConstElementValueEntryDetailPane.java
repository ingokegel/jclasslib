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
import org.gjt.jclasslib.structures.elementvalues.ConstElementValue;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
 * Class for showing element value entry of type Constant.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 * @version $Revision: 1.1 $ $Date: 2004-12-28 13:04:30 $
 */
public class ConstElementValueEntryDetailPane extends FixedListDetailPane {

    private ExtendedJLabel lblIndex;
    private ExtendedJLabel lblIndexVerbose;

    public ConstElementValueEntryDetailPane(BrowserServices services) {
        super(services);
    }

    protected void setupLabels() {

        addDetailPaneEntry(normalLabel("Constant value:"),
                lblIndex = linkLabel(),
                lblIndexVerbose = highlightLabel());
    }

    public void show(TreePath treePath) {
        ConstElementValue ceve = (ConstElementValue)
                ((BrowserTreeNode)treePath.getLastPathComponent()).getElement();

        constantPoolHyperlink(lblIndex,
                lblIndexVerbose,
                ceve.getConstValueIndex());

        super.show(treePath);
    }
}
