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
    Detail pane showing a <tt>SourceFile</tt> attribute.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.3 $ $Date: 2002-02-27 16:47:42 $
*/
public class SourceFileAttributeDetailPane extends FixedListDetailPane {

    // Visual components
    
    private ExtendedJLabel lblSourceFile;
    private ExtendedJLabel lblSourceFileVerbose;
    
    public SourceFileAttributeDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("Source file name index:"),
                           lblSourceFile = linkLabel(),
                           lblSourceFileVerbose = highlightLabel());
    }

    public void show(TreePath treePath) {
        
        SourceFileAttribute attribute = (SourceFileAttribute)findAttribute(treePath);

        constantPoolHyperlink(lblSourceFile,
                              lblSourceFileVerbose,
                              attribute.getSourcefileIndex());
        
        super.show(treePath);
    }
    
}

