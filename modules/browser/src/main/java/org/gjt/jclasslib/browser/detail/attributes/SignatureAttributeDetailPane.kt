/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail.attributes;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.detail.FixedListDetailPane;
import org.gjt.jclasslib.structures.attributes.SignatureAttribute;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
 * Detail pane showing a <tt>Signature</tt> attribute.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class SignatureAttributeDetailPane extends FixedListDetailPane {

    // Visual components
    private ExtendedJLabel lblSignature;
    private ExtendedJLabel lblSignatureVerbose;

    /**
     * Constructor.
     *
     * @param services the associated browser services.
     */
    public SignatureAttributeDetailPane(BrowserServices services) {
        super(services);
    }

    protected void addLabels() {
        addDetailPaneEntry(normalLabel("Signature index:"),
                lblSignature = linkLabel(),
                lblSignatureVerbose = highlightLabel());
    }

    public void show(TreePath treePath) {
        SignatureAttribute attribute = (SignatureAttribute)getAttribute(treePath);
        constantPoolHyperlink(lblSignature,
                lblSignatureVerbose,
                attribute.getSignatureIndex());
        super.show(treePath);
    }
}
