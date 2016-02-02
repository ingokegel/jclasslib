/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.ClassMember;
import org.gjt.jclasslib.util.ExtendedJLabel;

import javax.swing.tree.TreePath;

/**
 * Detail pane showing aggregated information about class members.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
 *
 */
public class ClassMemberContainerDetailPane extends FixedListWithSignatureDetailPane {

    private ExtendedJLabel lblMemberCount;

    /**
     * Constructor.
     *
     * @param services the associated browser services.
     */
    public ClassMemberContainerDetailPane(BrowserServices services, SignatureMode signatureMode) {
        super(services, signatureMode);
    }

    @Override
    protected String getSignatureVerbose() {
        SignatureMode signatureMode = getSignatureMode();
        ClassFile classFile = getServices().getClassFile();
        ClassMember[] classMembers = signatureMode.getClassMembers(classFile);

        StringBuilder buffer = new StringBuilder();
        for (ClassMember classMember : classMembers) {
            signatureMode.appendSignature(classMember, buffer);
            buffer.append('\n');
        }

        return buffer.toString();
    }

    protected void addLabels() {

        addDetailPaneEntry(normalLabel("Member count:"),
                lblMemberCount = highlightLabel());


    }

    public void show(TreePath treePath) {

        ClassFile classFile = getServices().getClassFile();
        int count = getSignatureMode().getClassMembers(classFile).length;
        lblMemberCount.setText(count);

        super.show(treePath);
    }

    protected String getSignatureButtonText() {
        return "Copy signatures to clipboard";
    }
}

