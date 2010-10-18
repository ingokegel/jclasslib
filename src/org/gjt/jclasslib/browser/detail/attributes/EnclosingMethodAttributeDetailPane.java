/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail.attributes;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.detail.FixedListDetailPane;
import org.gjt.jclasslib.structures.attributes.EnclosingMethodAttribute;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
 * Detail pane showing a <tt>Enclosing Method</tt> attribute.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 * @version $Revision: 1.1 $ $Date: 2004-12-28 13:04:30 $
 */
public class EnclosingMethodAttributeDetailPane extends FixedListDetailPane {

    // Visual components
    private ExtendedJLabel lblClass;
    private ExtendedJLabel lblClassVerbose;
    private ExtendedJLabel lblMethod;
    private ExtendedJLabel lblMethodVerbose;

    /**
     * Constructor.
     *
     * @param services the associated browser services.
     */
    public EnclosingMethodAttributeDetailPane(BrowserServices services) {
        super(services);
    }

    protected void setupLabels() {
        addDetailPaneEntry(normalLabel("Class index:"),
                lblClass = linkLabel(),
                lblClassVerbose = highlightLabel());
        addDetailPaneEntry(normalLabel("Method index:"),
                lblMethod = linkLabel(),
                lblMethodVerbose = highlightLabel());
    }

    public void show(TreePath treePath) {
        EnclosingMethodAttribute attribute = (EnclosingMethodAttribute)findAttribute(treePath);
        constantPoolHyperlink(lblClass,
                lblClassVerbose,
                attribute.getClassInfoIndex());

        constantPoolHyperlink(lblMethod,
                lblMethodVerbose,
                attribute.getMethodInfoIndex());

        super.show(treePath);
    }
}
