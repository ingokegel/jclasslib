/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail.attributes;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.detail.FixedListDetailPane;
import org.gjt.jclasslib.structures.attributes.AnnotationHolder;
import org.gjt.jclasslib.structures.attributes.RuntimeAnnotationsAttribute;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
 * Detail pane showing <tt>VisibleRuntimeAnnotations</tt>  and
 * <tt>InvisibleRuntimeAnnotations</tt> attribute.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class RuntimeAnnotationsAttributeDetailPane extends FixedListDetailPane {

    private ExtendedJLabel lblAnnotationEntries;

    /**
     * Constructor.
     *
     * @param services the associated browser services.
     */
    public RuntimeAnnotationsAttributeDetailPane(BrowserServices services) {
        super(services);
    }


    protected void setupLabels() {
        addDetailPaneEntry(normalLabel("Number of annotations:"),
                lblAnnotationEntries = highlightLabel());
    }

    public void show(TreePath treePath) {
        AnnotationHolder ah =
                (AnnotationHolder)findAttribute(treePath);

        lblAnnotationEntries.setText(String.valueOf(ah.getNumberOfAnnotations()));

        super.show(treePath);

    }
}
