/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.constants.ConstantReference;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
    Detail pane showing a <tt>CONSTANT_Fieldref</tt>,  <tt>CONSTANT_Methodref</tt>,
    or a <tt>CONSTANT_InterfaceMethodref</tt> constant pool entry.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.6 $ $Date: 2004-02-10 16:06:56 $
*/
public class ConstantReferenceDetailPane extends AbstractConstantInfoDetailPane {

    // Visual components
    
    private ExtendedJLabel lblClass;
    private ExtendedJLabel lblClassVerbose;
    private ExtendedJLabel lblNameAndType;
    private ExtendedJLabel lblNameAndTypeVerbose;

    private ClassElementOpener classElementOpener;

    /**
        Constructor.
        @param services the associated browser services.
     */
    public ConstantReferenceDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("Class name:"),
                           lblClass = linkLabel(),
                           lblClassVerbose = highlightLabel());

        addDetailPaneEntry(normalLabel("Name and type:"),
                           lblNameAndType = linkLabel(),
                           lblNameAndTypeVerbose = highlightLabel());
    }

    protected int addSpecial(int gridy) {

        classElementOpener = new ClassElementOpener(this);
        if (getBrowserServices().canOpenClassFiles()) {
            return classElementOpener.addSpecial(this, gridy);
        } else {
            return 0;
        }
    }

    public void show(TreePath treePath) {
        
        int constantPoolIndex = constantPoolIndex(treePath);

        try {
            ConstantReference entry = (ConstantReference)services.getClassFile().getConstantPoolEntry(constantPoolIndex, ConstantReference.class);
            classElementOpener.setCPInfo(entry);

            constantPoolHyperlink(lblClass,
                                  lblClassVerbose,
                                  entry.getClassIndex());
        
            constantPoolHyperlink(lblNameAndType,
                                  lblNameAndTypeVerbose,
                                  entry.getNameAndTypeIndex());
        
        } catch (InvalidByteCodeException ex) {
            lblClassVerbose.setText(MESSAGE_INVALID_CONSTANT_POOL_ENTRY);
        }
        
        super.show(treePath);
    }
    
}

