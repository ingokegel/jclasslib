/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserTreeNode;
import org.gjt.jclasslib.browser.config.window.*;
import org.gjt.jclasslib.structures.CPInfo;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.constants.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
    Component that opens named references to methods and fields.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 08:14:43 $
*/
public class ClassElementOpener implements ActionListener {

    private JButton btnShow;
    private CPInfo cpInfo;
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
            if (cpInfo instanceof ConstantClassInfo) {
                classInfo = (ConstantClassInfo)cpInfo;
            } else if (cpInfo instanceof ConstantReference) {
                ConstantReference reference = (ConstantReference)cpInfo;
                ConstantNameAndTypeInfo nameAndType = reference.getNameAndTypeInfo();
                classInfo = reference.getClassInfo();
                String category = null;
                if (cpInfo instanceof ConstantFieldrefInfo) {
                    category = BrowserTreeNode.NODE_FIELD;
                } else if (cpInfo instanceof ConstantMethodrefInfo || cpInfo instanceof ConstantInterfaceMethodrefInfo){
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
     * @param cpInfo the contant pool info.
     */
    public void setCPInfo(CPInfo cpInfo) {

        this.cpInfo = cpInfo;

        String buttonText = null;
        if (cpInfo instanceof ConstantClassInfo) {
            buttonText = "Show class";
            try {
                if (((ConstantClassInfo)cpInfo).getName().equals(detailPane.getBrowserServices().getClassFile().getThisClassName())) {
                    buttonText = null;
                }
            } catch (InvalidByteCodeException e) {
            }
        } else if (cpInfo instanceof ConstantFieldrefInfo) {
            buttonText = "Show field";
        } else if (cpInfo instanceof ConstantMethodrefInfo) {
            buttonText = "Show method";
        } else if (cpInfo instanceof ConstantInterfaceMethodrefInfo) {
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
