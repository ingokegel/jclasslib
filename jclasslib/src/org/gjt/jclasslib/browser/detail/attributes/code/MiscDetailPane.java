/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes.code;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.detail.FixedListDetailPane;
import org.gjt.jclasslib.structures.attributes.CodeAttribute;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
    Detail pane showing miscellaneous information of a <tt>Code</tt> attribute
    without substructure.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 08:19:37 $
*/
public class MiscDetailPane extends FixedListDetailPane {

    // Visual components
    
    private ExtendedJLabel lblMaxStack;
    private ExtendedJLabel lblMaxLocals;
    private ExtendedJLabel lblCodeLength;
    
    /**
        Constructor.
        @param services the associated browser services.
     */
    public MiscDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("Maximum stack depth:"),
                           lblMaxStack = highlightLabel());

        addDetailPaneEntry(normalLabel("Maximum local variables:"),
                           lblMaxLocals = highlightLabel());

        addDetailPaneEntry(normalLabel("Code length:"),
                           lblCodeLength = highlightLabel());
    }

    public void show(TreePath treePath) {
        
        CodeAttribute attribute = (CodeAttribute)findAttribute(treePath);

        lblMaxStack.setText(attribute.getMaxStack());
        lblMaxLocals.setText(attribute.getMaxLocals());
        lblCodeLength.setText(attribute.getCode().length);
        
        super.show(treePath);
    }
    
}

