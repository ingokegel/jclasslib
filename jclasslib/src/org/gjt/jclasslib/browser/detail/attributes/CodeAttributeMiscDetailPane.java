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
    Detail pane showing miscellaneous information of a <tt>Code</tt> attribute
    without substructure.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2001-05-31 13:15:25 $
*/
public class CodeAttributeMiscDetailPane extends FixedListDetailPane {

    // Visual components
    
    private ExtendedJLabel lblMaxStack;
    private ExtendedJLabel lblMaxLocals;
    private ExtendedJLabel lblCodeLength;
    
    public CodeAttributeMiscDetailPane(BrowserServices services) {
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

