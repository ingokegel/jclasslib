/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.*;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
    Detail pane showing class members (methods or fields).
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.6 $ $Date: 2003-08-18 08:13:21 $
*/
public class ClassMemberDetailPane extends FixedListDetailPane {

    /** Constant which indicates that a <tt>ClassMemberDetailPane</tt> shows fields. */
    public static final int FIELDS = 1;
    /** Constant which indicates that a <tt>ClassMemberDetailPane</tt> shows methods. */
    public static final int METHODS = 2;
    
    private int mode;
    
    // Visual components
    
    private ExtendedJLabel lblName;
    private ExtendedJLabel lblNameVerbose;

    private ExtendedJLabel lblDescriptor;
    private ExtendedJLabel lblDescriptorVerbose;

    private ExtendedJLabel lblAccessFlags;
    private ExtendedJLabel lblAccessFlagsVerbose;

    /**
        Construct a <tt>ClassMemberDetailPane</tt> with a specified mode which is
        either <tt>FIELDS</tt> or <tt>METHODS</tt>.
        @param services browser services
        @param mode the mode
     */
    public ClassMemberDetailPane(BrowserServices services, int mode) {
        super(services);
        this.mode = mode;
    }
    
    protected void setupLabels() {
        
        addDetailPaneEntry(normalLabel("Name:"),
                           lblName = linkLabel(),
                           lblNameVerbose = highlightLabel());

        addDetailPaneEntry(normalLabel("Descriptor:"),
                           lblDescriptor = linkLabel(),
                           lblDescriptorVerbose = highlightLabel());

        addDetailPaneEntry(normalLabel("Access flags:"),
                           lblAccessFlags = highlightLabel(), 
                           lblAccessFlagsVerbose = highlightLabel());
    }

    public void show(TreePath treePath) {
        
        int index = getIndex(treePath);
        ClassMember classMember;
        if (mode == FIELDS) {
            FieldInfo[] fields = services.getClassFile().getFields();
            if (index >= fields.length) {
                return;
            }
            classMember = fields[index];
        } else {
            MethodInfo[] methods = services.getClassFile().getMethods();
            if (index >= methods.length) {
                return;
            }
            classMember = methods[index];
        }
        
        constantPoolHyperlink(lblName,
                              lblNameVerbose,
                              classMember.getNameIndex());
        
        constantPoolHyperlink(lblDescriptor,
                              lblDescriptorVerbose,
                              classMember.getDescriptorIndex());
        
        lblAccessFlags.setText(classMember.getFormattedAccessFlags());
        lblAccessFlagsVerbose.setText("[" + classMember.getAccessFlagsVerbose() + "]");

        super.show(treePath);
        
    }
    
}

