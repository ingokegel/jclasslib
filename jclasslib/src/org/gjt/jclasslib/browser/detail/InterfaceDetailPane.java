/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.*;
import org.gjt.jclasslib.util.*;

import javax.swing.tree.*;

/**
    Detail pane showing interface entries.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:23 $
*/
public class InterfaceDetailPane extends FixedListDetailPane {

    // Visual components
    
    private ExtendedJLabel lblInterface;
    private ExtendedJLabel lblInterfaceVerbose;
    
    public InterfaceDetailPane(BrowserInternalFrame parentFrame) {
        super(parentFrame);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("Interface:"),
                           lblInterface = linkLabel(),
                           lblInterfaceVerbose = highlightLabel());

    }

    public void show(TreePath treePath) {
        
        int constantPoolIndex = parentFrame.getClassFile().getInterfaces()[getIndex(treePath)];
        
        constantPoolHyperlink(lblInterface,
                              lblInterfaceVerbose,
                              constantPoolIndex);
        
        super.show(treePath);
        
    }
    
}

