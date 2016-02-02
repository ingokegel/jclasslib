/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.constants.ConstantClassInfo;
import org.gjt.jclasslib.util.ExtendedJLabel;
import org.jetbrains.annotations.NotNull;

import javax.swing.tree.TreePath;

/**
    Detail pane showing a <tt>CONSTANT_Class</tt> constant pool entry.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ConstantClassInfoDetailPane extends AbstractConstantInfoDetailPane<ConstantClassInfo> {

    // Visual components
    
    private ExtendedJLabel lblClass;
    private ExtendedJLabel lblClassVerbose;

    private ClassElementOpener classElementOpener;

    /**
        Constructor.
        @param services the associated browser services.
     */
    public ConstantClassInfoDetailPane(BrowserServices services) {
        super(services);
    }

    @NotNull
    @Override
    public Class<ConstantClassInfo> getConstantClass() {
        return ConstantClassInfo.class;
    }

    protected void addLabels() {
        
        addDetailPaneEntry(normalLabel("Class name:"),
                           lblClass = linkLabel(),
                           lblClassVerbose = highlightLabel());
    }

    protected int addSpecial(int gridy) {

        classElementOpener = new ClassElementOpener(this);
        if (getServices().canOpenClassFiles()) {
            return classElementOpener.addSpecial(this, gridy);
        } else {
            return 0;
        }
    }

    public void show(TreePath treePath) {
        
        ConstantClassInfo entry = getConstant(treePath);
        classElementOpener.setCPInfo(entry);

        constantPoolHyperlink(lblClass,
                              lblClassVerbose,
                              entry.getNameIndex());

        super.show(treePath);
    }
    
}

