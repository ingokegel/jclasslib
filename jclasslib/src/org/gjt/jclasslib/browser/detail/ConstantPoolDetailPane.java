/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.*;
import org.gjt.jclasslib.browser.detail.constants.*;
import org.gjt.jclasslib.structures.CPInfo;
import org.gjt.jclasslib.structures.constants.*;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.HashMap;

/**
    Detail pane showing constant pool entries. This class is a container for
    the classes defined in the <tt>constants</tt> subpackage and switches between
    the contained panes as required.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.5 $ $Date: 2003-08-18 08:13:10 $
*/
public class ConstantPoolDetailPane extends AbstractDetailPane {

    private static final String SCREEN_CONSTANT_UTF8_INFO = "ConstantUtf8Info";
    private static final String SCREEN_CONSTANT_UNKNOWN = "ConstantUnknown";
    private static final String SCREEN_CONSTANT_CLASS_INFO = "ConstantClassInfo";
    private static final String SCREEN_CONSTANT_DOUBLE_INFO = "ConstantDoubleInfo";
    private static final String SCREEN_CONSTANT_LONG_INFO = "ConstantLongInfo";
    private static final String SCREEN_CONSTANT_FLOAT_INFO = "ConstantFloatInfo";
    private static final String SCREEN_CONSTANT_INTEGER_INFO = "ConstantIntegerInfo";
    private static final String SCREEN_CONSTANT_NAME_AND_TYPE_INFO = "ConstantNameAndTypeInfo";
    private static final String SCREEN_CONSTANT_STRING_INFO = "ConstantStringInfo";
    private static final String SCREEN_CONSTANT_REFERENCE = "ConstantReference";
    
    private HashMap constantTypeToDetailPane;

    /**
        Constructor.
        @param services the associated browser services.
     */
    public ConstantPoolDetailPane(BrowserServices services) {
        super(services);
    }

    protected void setupComponent() {
        setLayout(new CardLayout());
        constantTypeToDetailPane = new HashMap();
        JPanel pane;
        
        pane = new JPanel();
        pane.setBackground(Color.blue);
        add(pane, SCREEN_CONSTANT_UNKNOWN);
        
        addScreen(new ConstantUtf8InfoDetailPane(services),
            SCREEN_CONSTANT_UTF8_INFO);

        addScreen(new ConstantClassInfoDetailPane(services),
                  SCREEN_CONSTANT_CLASS_INFO);

        addScreen(new ConstantDoubleInfoDetailPane(services),
                  SCREEN_CONSTANT_DOUBLE_INFO);

        addScreen(new ConstantLongInfoDetailPane(services),
            SCREEN_CONSTANT_LONG_INFO);

        addScreen(new ConstantFloatInfoDetailPane(services),
            SCREEN_CONSTANT_FLOAT_INFO);

        addScreen(new ConstantIntegerInfoDetailPane(services),
            SCREEN_CONSTANT_INTEGER_INFO);

        addScreen(new ConstantNameAndTypeInfoDetailPane(services),
            SCREEN_CONSTANT_NAME_AND_TYPE_INFO);

        addScreen(new ConstantStringInfoDetailPane(services),
            SCREEN_CONSTANT_STRING_INFO);
                
        addScreen(new ConstantReferenceDetailPane(services),
            SCREEN_CONSTANT_REFERENCE);
                
    }
    
    public void show(TreePath treePath) {

        int constantPoolIndex = ((BrowserTreeNode)treePath.getLastPathComponent()).getIndex();
        CPInfo constantPoolEntry = services.getClassFile().getConstantPool()[constantPoolIndex];
        
        String paneName = null;
        if (constantPoolEntry instanceof ConstantUtf8Info) {
            paneName = SCREEN_CONSTANT_UTF8_INFO;
        } else if (constantPoolEntry instanceof ConstantClassInfo) {
            paneName = SCREEN_CONSTANT_CLASS_INFO;
        } else if (constantPoolEntry instanceof ConstantDoubleInfo) {
            paneName = SCREEN_CONSTANT_DOUBLE_INFO;
        } else if (constantPoolEntry instanceof ConstantLongInfo) {
            paneName = SCREEN_CONSTANT_LONG_INFO;
        } else if (constantPoolEntry instanceof ConstantFloatInfo) {
            paneName = SCREEN_CONSTANT_FLOAT_INFO;
        } else if (constantPoolEntry instanceof ConstantIntegerInfo) {
            paneName = SCREEN_CONSTANT_INTEGER_INFO;
        } else if (constantPoolEntry instanceof ConstantNameAndTypeInfo) {
            paneName = SCREEN_CONSTANT_NAME_AND_TYPE_INFO;
        } else if (constantPoolEntry instanceof ConstantStringInfo) {
            paneName = SCREEN_CONSTANT_STRING_INFO;
        } else if (constantPoolEntry instanceof ConstantReference) {
            paneName = SCREEN_CONSTANT_REFERENCE;
        }

    
        CardLayout layout = (CardLayout)getLayout();
        if (paneName == null) {
            layout.show(this, SCREEN_CONSTANT_UNKNOWN);
        } else {
            AbstractDetailPane pane = (AbstractDetailPane)constantTypeToDetailPane.get(paneName);
            pane.show(treePath);
            layout.show(this, paneName);
        }
        
    }
    
    private void addScreen(AbstractDetailPane detailPane, String name) {

        if (detailPane instanceof FixedListDetailPane) {
            add(((FixedListDetailPane)detailPane).getScrollPane(), name);
        } else {
            add(detailPane, name);
        }
        constantTypeToDetailPane.put(name, detailPane);
    }
    
}

