/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserTreeNode;
import org.gjt.jclasslib.browser.config.window.BrowserPath;
import org.gjt.jclasslib.browser.config.window.CategoryHolder;
import org.gjt.jclasslib.browser.config.window.ReferenceHolder;
import org.gjt.jclasslib.structures.Constant;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.constants.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
    Component that opens named references to methods and fields.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ClassElementOpener implements ActionListener {

    private JButton btnShow;
    private Constant constant;
    private AbstractConstantInfoDetailPane detailPane;

    /**
     * Constructor.
     * @param detailPane the parent detail pane.
     */
    public ClassElementOpener(AbstractConstantInfoDetailPane detailPane) {
        this.detailPane = detailPane;

        btnShow = new JButton("Show");
        btnShow.addActionListener(this);
    }

    public void actionPerformed(ActionEvent event) {

        try {
            ConstantClassInfo classInfo = null;
            BrowserPath browserPath = null;
            if (constant instanceof ConstantClassInfo) {
                classInfo = (ConstantClassInfo)constant;
            } else if (constant instanceof ConstantReference) {
                ConstantReference reference = (ConstantReference)constant;
                ConstantNameAndTypeInfo nameAndType = reference.getNameAndTypeInfo();
                classInfo = reference.getClassInfo();
                String category = null;
                if (constant instanceof ConstantFieldrefInfo) {
                    category = BrowserTreeNode.NODE_FIELD;
                } else if (constant instanceof ConstantMethodrefInfo || constant instanceof ConstantInterfaceMethodrefInfo){
                    category = BrowserTreeNode.NODE_METHOD;
                }
                if (category != null) {
                    browserPath = new BrowserPath();
                    browserPath.addPathComponent(new CategoryHolder(category));
                    browserPath.addPathComponent(new ReferenceHolder(nameAndType.getName(), nameAndType.getDescriptor()));
                }
            }
            if (classInfo == null) {
                return;
            }
            String className = classInfo.getName().replace('/', '.');
            detailPane.getBrowserServices().openClassFile(className, browserPath);
        } catch (InvalidByteCodeException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Add an opening button to the supplied detail pane.
     * @param detailPane the detail pane.
     * @param gridy the current <tt>gridy</tt> of the <tt>GridBagLayout</tt> of the detail pane.
     * @return the number of added rows.
     */
    public int addSpecial(AbstractConstantInfoDetailPane detailPane, int gridy) {

        GridBagConstraints gc = new GridBagConstraints();
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.WEST;
        gc.insets = new Insets(5, 10, 0, 10);
        gc.gridy = gridy;
        gc.gridx = 0;
        gc.gridwidth = 3;

        detailPane.add(btnShow, gc);

        return 1;
    }

    /**
     * Set the constant pool info that is to be the source of the link.
     * @param constant the constant pool info.
     */
    public void setCPInfo(Constant constant) {

        this.constant = constant;

        String buttonText = null;
        if (constant instanceof ConstantClassInfo) {
            buttonText = "Show class";
            try {
                if (((ConstantClassInfo)constant).getName().equals(detailPane.getBrowserServices().getClassFile().getThisClassName())) {
                    buttonText = null;
                }
            } catch (InvalidByteCodeException e) {
            }
        } else if (constant instanceof ConstantFieldrefInfo) {
            buttonText = "Show field";
        } else if (constant instanceof ConstantMethodrefInfo) {
            buttonText = "Show method";
        } else if (constant instanceof ConstantInterfaceMethodrefInfo) {
            buttonText = "Show interface method";
        }

        if (buttonText != null) {
            btnShow.setVisible(true);
            btnShow.setText(buttonText);
        } else {
            btnShow.setVisible(false);
        }
    }

}
