/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes;

import org.gjt.jclasslib.browser.*;
import org.gjt.jclasslib.browser.detail.*;
import org.gjt.jclasslib.structures.*;
import org.gjt.jclasslib.util.*;

import javax.swing.tree.*;

/**
    Detail pane showing the generic information which applies to all attribute.

    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2001-05-31 13:15:25 $
*/
public class GenericAttributeDetailPane extends FixedListDetailPane {

    // Visual components
    
    private ExtendedJLabel lblNameIndex;
    private ExtendedJLabel lblNameIndexVerbose;
    private ExtendedJLabel lblLength;
    
    public GenericAttributeDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("Attribute name index:"),
                           lblNameIndex = linkLabel(),
                           lblNameIndexVerbose);

        addDetailPaneEntry(normalLabel("Attribute length:"),
                           lblLength = highlightLabel());
    
    }

    public void show(TreePath treePath) {
        
        AttributeInfo attribute = findAttribute(treePath);

        constantPoolHyperlink(lblNameIndex,
                              lblNameIndexVerbose,
                              attribute.getAttributeNameIndex());
        
        lblLength.setText(attribute.getAttributeLength());
        
        super.show(treePath);
    }
    
}

