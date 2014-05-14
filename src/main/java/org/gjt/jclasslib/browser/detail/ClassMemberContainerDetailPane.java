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
import org.gjt.jclasslib.util.ExtendedJLabel;
import org.gjt.jclasslib.util.GUIHelper;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Detail pane showing aggregated information about class members.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
 *
 */
public class ClassMemberContainerDetailPane extends FixedListDetailPane {

    /**
     * Constant which indicates that a <tt>ClassMemberContainerDetailPane</tt> shows fields.
     */
    public static final int FIELDS = 1;
    /**
     * Constant which indicates that a <tt>ClassMemberContainerDetailPane</tt> shows methods.
     */
    public static final int METHODS = 2;

    // Visual components
    
    private ExtendedJLabel lblMemberCount;
    private int mode;
    private JButton btnCopyToClipboard;

    /**
     * Constructor.
     *
     * @param services the associated browser services.
     */
    public ClassMemberContainerDetailPane(BrowserServices services, int mode) {
        super(services);
        this.mode = mode;
    }

    protected void setupLabels() {

        addDetailPaneEntry(normalLabel("Member count:"),
                lblMemberCount = highlightLabel());


    }

    protected int addSpecial(int gridy) {

        btnCopyToClipboard = new JButton("Copy signatures to clipboard");
        btnCopyToClipboard.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                copySignaturesToClipboard();
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

    private void copySignaturesToClipboard() {
        ClassFile classFile = services.getClassFile();
        ClassMember[] classMembers;
        if (mode == FIELDS) {
            classMembers = classFile.getFields();
        } else {
            classMembers = classFile.getMethods();
        }

        StringBuilder buffer = new StringBuilder();
        for (ClassMember classMember : classMembers) {
            try {
                if (mode == FIELDS) {
                    buffer.append(classMember.getDescriptor());
                    buffer.append(' ');
                    buffer.append(classMember.getName());
                } else {
                    buffer.append(classMember.getName());
                    buffer.append(classMember.getDescriptor());
                }
            } catch (InvalidByteCodeException e) {
            }
            buffer.append('\n');
        }

        StringSelection stringSelection = new StringSelection(buffer.toString());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(stringSelection, stringSelection);

        GUIHelper.showMessage(this, "The output has been copied to the clipboard", JOptionPane.INFORMATION_MESSAGE);
    }

    public void show(TreePath treePath) {

        ClassFile classFile = services.getClassFile();
        int count;
        if (mode == FIELDS) {
            count = classFile.getFields().length;
        } else {
            count = classFile.getMethods().length;
        }

        lblMemberCount.setText(count);

        super.show(treePath);
    }

}

