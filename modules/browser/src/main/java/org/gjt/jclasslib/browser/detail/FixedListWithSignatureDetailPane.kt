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
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public abstract class FixedListWithSignatureDetailPane extends FixedListDetailPane {

    private final SignatureMode signatureMode;
    private JButton btnCopyToClipboard;

    protected FixedListWithSignatureDetailPane(BrowserServices services, SignatureMode signatureMode) {
        super(services);
        this.signatureMode = signatureMode;
    }

    protected abstract String getSignatureVerbose();
    protected abstract String getSignatureButtonText();

    protected SignatureMode getSignatureMode() {
        return signatureMode;
    }

    @Override
    public String getClipboardText() {
        return getSignatureVerbose();
    }

    protected int addSpecial(int gridy) {

        btnCopyToClipboard = new JButton(getSignatureButtonText());
        btnCopyToClipboard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                copySignatureToClipboard();
            }
        });

        GridBagConstraints gc = new GridBagConstraints();
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(5, 10, 0, 10);
        gc.gridy = gridy;
        gc.gridx = 0;
        gc.gridwidth = 3;

        add(btnCopyToClipboard, gc);

        return 1;
    }

    private void copySignatureToClipboard() {
        String signatureVerbose = getSignatureVerbose();
        if (signatureVerbose == null) {
            return;
        }
        StringSelection stringSelection = new StringSelection(signatureVerbose);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);

    }

    public enum SignatureMode {
        FIELD {
            @Override
            public ClassMember[] getClassMembers(ClassFile classFile) {
                return classFile.getFields();
            }

            @Override
            public void appendSignature(ClassMember classMember, StringBuilder buffer) {
                try {
                    buffer.append(classMember.getDescriptor());
                    buffer.append(' ');
                    buffer.append(classMember.getName());
                } catch (InvalidByteCodeException e) {
                }
            }
        },
        METHOD {
            @Override
            public ClassMember[] getClassMembers(ClassFile classFile) {
                return classFile.getMethods();
            }

            @Override
            public void appendSignature(ClassMember classMember, StringBuilder buffer) {
                try {
                    buffer.append(classMember.getName());
                    buffer.append(classMember.getDescriptor());
                } catch (InvalidByteCodeException e) {
                }
            }
        };

        public abstract ClassMember[] getClassMembers(ClassFile classFile);
        public abstract void appendSignature(ClassMember classMember, StringBuilder buffer);
    }

}
