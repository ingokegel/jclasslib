/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.*;
import org.gjt.jclasslib.browser.detail.constants.*;
import org.gjt.jclasslib.structures.*;
import org.gjt.jclasslib.structures.constants.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;

/**
    Detail pane showing constant pool entries. This class is a container for
    the classes defined in the <tt>constants</tt> subpackage and switches between
    the contained panes as required.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:23 $
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
    
    public ConstantPoolDetailPane(BrowserInternalFrame parentFrame) {
        super(parentFrame);
    }

    protected void setupComponent() {
        setLayout(new CardLayout());
        constantTypeToDetailPane = new HashMap();
        JPanel pane;
        
        pane = new JPanel();
        pane.setBackground(Color.blue);
        add(pane, SCREEN_CONSTANT_UNKNOWN);
        
        addScreen(new ConstantUtf8InfoDetailPane(parentFrame),
            SCREEN_CONSTANT_UTF8_INFO);

        addScreen(new ConstantClassInfoDetailPane(parentFrame),
                  SCREEN_CONSTANT_CLASS_INFO);

        addScreen(new ConstantDoubleInfoDetailPane(parentFrame),
                  SCREEN_CONSTANT_DOUBLE_INFO);

        addScreen(new ConstantLongInfoDetailPane(parentFrame),
            SCREEN_CONSTANT_LONG_INFO);

        addScreen(new ConstantFloatInfoDetailPane(parentFrame),
            SCREEN_CONSTANT_FLOAT_INFO);

        addScreen(new ConstantIntegerInfoDetailPane(parentFrame),
            SCREEN_CONSTANT_INTEGER_INFO);

        addScreen(new ConstantNameAndTypeInfoDetailPane(parentFrame),
            SCREEN_CONSTANT_NAME_AND_TYPE_INFO);

        addScreen(new ConstantStringInfoDetailPane(parentFrame),
            SCREEN_CONSTANT_STRING_INFO);
                
        addScreen(new ConstantReferenceDetailPane(parentFrame),
            SCREEN_CONSTANT_REFERENCE);
                
    }
    
    public void show(TreePath treePath) {

        int constantPoolIndex = ((BrowserMutableTreeNode)treePath.getLastPathComponent()).getIndex();
        CPInfo constantPoolEntry = parentFrame.getClassFile().getConstantPool()[constantPoolIndex];
        
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
    
    private void addScreen(JPanel pane, String name) {
        add(pane, name);
        constantTypeToDetailPane.put(name, pane);
    }
    
}

