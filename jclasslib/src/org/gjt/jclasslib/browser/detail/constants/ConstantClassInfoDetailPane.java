/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.constants.ConstantClassInfo;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
    Detail pane showing a <tt>CONSTANT_Class</tt> constant pool entry.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.5 $ $Date: 2003-08-18 08:15:30 $
*/
public class ConstantClassInfoDetailPane extends AbstractConstantInfoDetailPane {

    // Visual components
    
    private ExtendedJLabel lblClass;
    private ExtendedJLabel lblClassVerbose;

    private ClassElementOpener classElementOpener;

    /**
        Constructor.
        @param services the associated browser services.
     */
    public ConstantClassInfoDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("Class name:"),
                           lblClass = linkLabel(),
                           lblClassVerbose = highlightLabel());
    }

    protected int addSpecial(int gridy) {
        classElementOpener = new ClassElementOpener(this);
        return classElementOpener.addSpecial(this, gridy);
    }

    public void show(TreePath treePath) {
        
        int constantPoolIndex = constantPoolIndex(treePath);

        try {
            ConstantClassInfo entry = (ConstantClassInfo)services.getClassFile().getConstantPoolEntry(constantPoolIndex, ConstantClassInfo.class);
            classElementOpener.setCPInfo(entry);

            constantPoolHyperlink(lblClass,
                                  lblClassVerbose,
                                  entry.getNameIndex());
        
        } catch (InvalidByteCodeException ex) {
            lblClassVerbose.setText(MESSAGE_INVALID_CONSTANT_POOL_ENTRY);
        }
        
        super.show(treePath);
    }
    
}

