/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.attributes.BootstrapMethodsAttribute;
import org.gjt.jclasslib.structures.constants.ConstantInvokeDynamicInfo;
import org.gjt.jclasslib.util.ExtendedJLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

/**
    Detail pane showing a <tt>CONSTANT_Invoke_Dynamic</tt> constant pool entry.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ConstantInvokeDynamicInfoDetailPane extends AbstractConstantInfoDetailPane<ConstantInvokeDynamicInfo> {

    // Visual components

    private ExtendedJLabel lblNameAndType;
    private ExtendedJLabel lblNameAndTypeVerbose;
    private ExtendedJLabel lblBootstrap;

    /**
        Constructor.
        @param services the associated browser services.
     */
    public ConstantInvokeDynamicInfoDetailPane(BrowserServices services) {
        super(services);
    }

    @NotNull
    @Override
    public Class<ConstantInvokeDynamicInfo> getConstantClass() {
        return ConstantInvokeDynamicInfo.class;
    }

    protected void addLabels() {

        addDetailPaneEntry(normalLabel("Name and type:"),
                           lblNameAndType = linkLabel(),
                           lblNameAndTypeVerbose = highlightLabel());

        addDetailPaneEntry(normalLabel("Bootstrap method:"),
                           lblBootstrap = linkLabel());
    }

    public void show(TreePath treePath) {

        ConstantInvokeDynamicInfo entry = getConstant(treePath);

        constantPoolHyperlink(lblNameAndType,
            lblNameAndTypeVerbose,
            entry.getNameAndTypeIndex());

        classAttributeIndexHyperlink(lblBootstrap,
            null,
            entry.getBootstrapMethodAttributeIndex(), BootstrapMethodsAttribute.class, "BootstrapMethods #");


        super.show(treePath);

    }

}

