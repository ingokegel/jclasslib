/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.BrowserTreeNode;
import org.gjt.jclasslib.structures.elementvalues.ArrayElementValue;
import org.gjt.jclasslib.structures.elementvalues.ElementValue;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
 * Class for showing an array element value entry.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 * @version $Revision: 1.1 $ $Date: 2004-12-28 13:04:31 $
 */
public class ArrayElementValueDetailPane extends FixedListDetailPane {

    private ExtendedJLabel lblTag;
    private ExtendedJLabel lblTagVerbose;

    private ExtendedJLabel lblValueCount;

    public ArrayElementValueDetailPane(BrowserServices services) {
        super(services);
    }

    protected void setupLabels() {
        addDetailPaneEntry(normalLabel("Tag:"),
                lblTag = highlightLabel(),
                lblTagVerbose = highlightLabel());

        addDetailPaneEntry(normalLabel("Number of values:"),
                lblValueCount = highlightLabel());
    }

    public void show(TreePath treePath) {
        ArrayElementValue aev = (ArrayElementValue)
                ((BrowserTreeNode)treePath.getLastPathComponent()).getElement();

        lblTag.setText(String.valueOf((char)aev.getTag()));
        lblTagVerbose.setText("<" + ElementValue.getTagDescription(aev.getTag()) + ">");

        lblValueCount.setText(String.valueOf(aev.getElementValueEntries().length));

        super.show(treePath);
    }


}
