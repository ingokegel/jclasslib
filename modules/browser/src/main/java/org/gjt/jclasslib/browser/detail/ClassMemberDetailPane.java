/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.ClassMember;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
    Detail pane showing class members (methods or fields).
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ClassMemberDetailPane extends FixedListWithSignatureDetailPane {

    // Visual components
    
    private ExtendedJLabel lblName;
    private ExtendedJLabel lblNameVerbose;

    private ExtendedJLabel lblDescriptor;
    private ExtendedJLabel lblDescriptorVerbose;

    private ExtendedJLabel lblAccessFlags;
    private ExtendedJLabel lblAccessFlagsVerbose;

    private ClassMember lastClassMember;

    /**
        Construct a <tt>ClassMemberDetailPane</tt> with a specified mode which is
        either <tt>FIELDS</tt> or <tt>METHODS</tt>.
        @param services browser services
        @param signatureMode the mode
     */
    public ClassMemberDetailPane(BrowserServices services, SignatureMode signatureMode) {
        super(services, signatureMode);
    }

    @Override
    protected String getSignatureVerbose() {
        if (lastClassMember == null) {
            return null;
        }
        StringBuilder buffer = new StringBuilder();
        getSignatureMode().appendSignature(lastClassMember, buffer);

        return buffer.toString();
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

        lastClassMember = getClassMember(treePath);
        if (lastClassMember == null) {
            return;
        }

        constantPoolHyperlink(lblName,
                              lblNameVerbose,
                              lastClassMember.getNameIndex());
        
        constantPoolHyperlink(lblDescriptor,
                              lblDescriptorVerbose,
                              lastClassMember.getDescriptorIndex());
        
        lblAccessFlags.setText(lastClassMember.getFormattedAccessFlags());
        lblAccessFlagsVerbose.setText("[" + lastClassMember.getAccessFlagsVerbose() + "]");

        super.show(treePath);
        
    }

    private ClassMember getClassMember(TreePath treePath) {
        int index = getIndex(treePath);
        ClassMember[] classMembers = getSignatureMode().getClassMembers(services.getClassFile());
        if (index < classMembers.length) {
            return classMembers[index];
        } else {
            return null;
        }
    }

    protected String getSignatureButtonText() {
        return "Copy signature to clipboard";
    }
}

