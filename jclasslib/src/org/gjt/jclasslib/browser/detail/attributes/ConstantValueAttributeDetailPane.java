/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes;

import org.gjt.jclasslib.browser.*;
import org.gjt.jclasslib.browser.detail.*;
import org.gjt.jclasslib.structures.attributes.*;
import org.gjt.jclasslib.util.*;

import javax.swing.tree.*;

/**
    Detail pane showing a <tt>ConstantValue</tt> attribute.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:25 $
*/
public class ConstantValueAttributeDetailPane extends FixedListDetailPane {

    // Visual components
    
    private ExtendedJLabel lblValue;
    private ExtendedJLabel lblVerbose;
    
    public ConstantValueAttributeDetailPane(BrowserInternalFrame parentFrame) {
        super(parentFrame);
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

