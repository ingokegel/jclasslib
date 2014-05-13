/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
    Detail pane showing interface entries.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.5 $ $Date: 2003-08-18 08:14:22 $
*/
public class InterfaceDetailPane extends FixedListDetailPane {

    // Visual components
    
    private ExtendedJLabel lblInterface;
    private ExtendedJLabel lblInterfaceVerbose;
    
    /**
        Constructor.
        @param services the associated browser services.
     */
    public InterfaceDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("Interface:"),
                           lblInterface = linkLabel(),
                           lblInterfaceVerbose = highlightLabel());

    }

    public void show(TreePath treePath) {
        
        int constantPoolIndex = services.getClassFile().getInterfaces()[getIndex(treePath)];
        
        constantPoolHyperlink(lblInterface,
                              lblInterfaceVerbose,
                              constantPoolIndex);
        
        super.show(treePath);
        
    }
    
}

