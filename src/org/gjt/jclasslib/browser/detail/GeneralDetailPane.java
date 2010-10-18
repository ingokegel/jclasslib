/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
    Detail pane showing general information on the class file structure.
    All fields in the <tt>ClassFile</tt> structure without substructure 
    are incorporated in theis pane.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.5 $ $Date: 2003-08-18 08:14:22 $
*/
public class GeneralDetailPane extends FixedListDetailPane {
    
    // Visual components
    
    private ExtendedJLabel lblMinorVersion;
    private ExtendedJLabel lblMajorVersion;
    private ExtendedJLabel lblConstantPoolCount;
    private ExtendedJLabel lblAccessFlags;
    private ExtendedJLabel lblAccessFlagsVerbose;
    private ExtendedJLabel lblThisClass;
    private ExtendedJLabel lblThisClassVerbose;
    private ExtendedJLabel lblSuperClass;
    private ExtendedJLabel lblSuperClassVerbose;
    private ExtendedJLabel lblInterfacesCount;
    private ExtendedJLabel lblFieldsCount;
    private ExtendedJLabel lblMethodsCount;
    private ExtendedJLabel lblAttributesCount;

    /**
        Constructor.
        @param services the associated browser services.
     */
    public GeneralDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("Minor version:"),
                           lblMinorVersion = highlightLabel());

        addDetailPaneEntry(normalLabel("Major version:"),
                           lblMajorVersion = highlightLabel());
        
        addDetailPaneEntry(normalLabel("Constant pool count:"),
                           lblConstantPoolCount = highlightLabel());
        
        addDetailPaneEntry(normalLabel("Access flags:"),
                           lblAccessFlags = highlightLabel(), 
                           lblAccessFlagsVerbose = highlightLabel());
        
        addDetailPaneEntry(normalLabel("This class:"),
                           lblThisClass = linkLabel(),
                           lblThisClassVerbose = highlightLabel());
        
        addDetailPaneEntry(normalLabel("Super class:"),
                           lblSuperClass = linkLabel(),
                           lblSuperClassVerbose = highlightLabel());
        
        addDetailPaneEntry(normalLabel("Interfaces count:"),
                           lblInterfacesCount = highlightLabel());
        
        addDetailPaneEntry(normalLabel("Fields count:"),
                           lblFieldsCount = highlightLabel());
        
        addDetailPaneEntry(normalLabel("Methods count:"),
                           lblMethodsCount = highlightLabel());
        
        addDetailPaneEntry(normalLabel("Attributes count:"),
                           lblAttributesCount = highlightLabel());

    }

    public void show(TreePath treePath) {
        
        ClassFile classFile = services.getClassFile();
        
        lblMinorVersion.setText(classFile.getMinorVersion());
        lblMajorVersion.setText(classFile.getMajorVersion());
        lblConstantPoolCount.setText(classFile.getConstantPool().length);

        lblAccessFlags.setText(classFile.getFormattedAccessFlags());
        lblAccessFlagsVerbose.setText("[" + classFile.getAccessFlagsVerbose() + "]");

        constantPoolHyperlink(lblThisClass,
                              lblThisClassVerbose,
                              classFile.getThisClass());

        constantPoolHyperlink(lblSuperClass,
                              lblSuperClassVerbose,
                              classFile.getSuperClass());
        
        lblInterfacesCount.setText(classFile.getInterfaces().length);
        lblFieldsCount.setText(classFile.getFields().length);
        lblMethodsCount.setText(classFile.getMethods().length);
        lblAttributesCount.setText(classFile.getAttributes().length);

        super.show(treePath);
    }
    
}

