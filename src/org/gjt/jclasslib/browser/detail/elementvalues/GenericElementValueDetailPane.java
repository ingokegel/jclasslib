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
import org.gjt.jclasslib.structures.elementvalues.ElementValue;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;


/**
 * Detail pane showing the generic information which applies to all element values.
 * and switches between the contained panes as required.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 * @version $Revision: 1.1 $ $Date: 2004-12-28 13:04:30 $
 */
public class GenericElementValueDetailPane extends FixedListDetailPane {

    private ExtendedJLabel lblTag;
    private ExtendedJLabel lblTagVerbose;

    public GenericElementValueDetailPane(BrowserServices services) {
        super(services);
    }

    protected void setupLabels() {
        addDetailPaneEntry(normalLabel("Tag:"),
                lblTag = highlightLabel(),
                lblTagVerbose = highlightLabel());
    }

    public void show(TreePath treePath) {
        ElementValue ceve = (ElementValue)
                ((BrowserTreeNode)treePath.getLastPathComponent()).getElement();

        lblTag.setText(String.valueOf((char)ceve.getTag()));
        lblTagVerbose.setText("<" + ElementValue.getTagDescription(ceve.getTag()) + ">");

        super.show(treePath);
    }
}
