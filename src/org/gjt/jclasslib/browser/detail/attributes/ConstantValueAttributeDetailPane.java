/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.detail.FixedListDetailPane;
import org.gjt.jclasslib.structures.attributes.ConstantValueAttribute;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
    Detail pane showing a <tt>ConstantValue</tt> attribute.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.5 $ $Date: 2003-08-18 08:18:35 $
*/
public class ConstantValueAttributeDetailPane extends FixedListDetailPane {

    // Visual components
    
    private ExtendedJLabel lblValue;
    private ExtendedJLabel lblVerbose;
    
    /**
        Constructor.
        @param services the associated browser services.
     */
    public ConstantValueAttributeDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("Constant value index:"),
                           lblValue = linkLabel(),
                           lblVerbose = highlightLabel());
    }

    public void show(TreePath treePath) {
        
        ConstantValueAttribute attribute = (ConstantValueAttribute)findAttribute(treePath);

        constantPoolHyperlink(lblValue,
                              lblVerbose,
                              attribute.getConstantvalueIndex());
        
        super.show(treePath);
    }
    
}

