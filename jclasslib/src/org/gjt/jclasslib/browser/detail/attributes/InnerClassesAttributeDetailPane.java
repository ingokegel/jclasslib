/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes;

import org.gjt.jclasslib.browser.*;
import org.gjt.jclasslib.structures.*;
import org.gjt.jclasslib.structures.attributes.*;

/**
    Detail pane showing an <tt>InnerClasses</tt> attribute.

    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2001-05-31 13:15:25 $
*/
public class InnerClassesAttributeDetailPane extends AbstractAttributeListDetailPane {

    public InnerClassesAttributeDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected AbstractAttributeTableModel createTableModel(AttributeInfo attribute) {
        return new AttributeTableModel(attribute);
    }
    
    private class AttributeTableModel extends AbstractAttributeTableModel {
        
        private static final int COLUMN_COUNT = BASE_COLUMN_COUNT + 4;
        
        private static final int INNER_CLASS_INFO_INDEX_COLUMN_INDEX = BASE_COLUMN_COUNT;
        private static final int OUTER_CLASS_INFO_INDEX_COLUMN_INDEX = BASE_COLUMN_COUNT + 1;
        private static final int INNER_NAME_INDEX_COLUMN_INDEX = BASE_COLUMN_COUNT + 2;
        private static final int INNER_CLASS_ACCESS_FLAGS_COLUMN_INDEX = BASE_COLUMN_COUNT + 3;
        
        private static final int INNER_CLASS_ACCESS_FLAGS_COLUMN_WIDTH = 250;
        
        private InnerClassesEntry[] innerClasses;
        
        public AttributeTableModel(AttributeInfo attribute) {
            super(attribute);
            innerClasses = ((InnerClassesAttribute)attribute).getClasses();
            initRowNumberStrings();
        }

        public int getColumnWidth(int column) {
            switch (column) {
                case INNER_CLASS_INFO_INDEX_COLUMN_INDEX:
                case OUTER_CLASS_INFO_INDEX_COLUMN_INDEX:
                case INNER_NAME_INDEX_COLUMN_INDEX:
                   return LINK_COLUMN_WIDTH;
                   
                case INNER_CLASS_ACCESS_FLAGS_COLUMN_INDEX:
                   return INNER_CLASS_ACCESS_FLAGS_COLUMN_WIDTH;
                    
                default:
                   return LINK_COLUMN_WIDTH;
            }
        }
        
        public void link(int row, int column) {
            
            int constantPoolIndex = 0;
            switch (column) {
                case INNER_CLASS_INFO_INDEX_COLUMN_INDEX:
                    constantPoolIndex = innerClasses[row].getInnerClassInfoIndex();
                    break;
                case OUTER_CLASS_INFO_INDEX_COLUMN_INDEX:
                    constantPoolIndex = innerClasses[row].getOuterClassInfoIndex();
                    break;
                case INNER_NAME_INDEX_COLUMN_INDEX:
                    constantPoolIndex = innerClasses[row].getInnerNameIndex();
                    break;
                default:
                    return;
            }
            ConstantPoolHyperlinkListener.link(services, constantPoolIndex);
        }
        
        public int getRowCount() {
            return innerClasses.length;
        }
        
        public int getColumnCount() {
            return COLUMN_COUNT;
        }
        
        protected String doGetColumnName(int column) {
            switch (column) {
                case INNER_CLASS_INFO_INDEX_COLUMN_INDEX:
                   return "inner_class";
                case OUTER_CLASS_INFO_INDEX_COLUMN_INDEX:
                   return "outer_class";
                case INNER_NAME_INDEX_COLUMN_INDEX:
                   return "inner_name";
                case INNER_CLASS_ACCESS_FLAGS_COLUMN_INDEX:
                   return "access flags";
                default:
                   return "";
            }
        }
        
        protected Class doGetColumnClass(int column) {
            switch (column) {
                case INNER_CLASS_INFO_INDEX_COLUMN_INDEX:
                case OUTER_CLASS_INFO_INDEX_COLUMN_INDEX:
                case INNER_NAME_INDEX_COLUMN_INDEX:
                   return Link.class;
                case INNER_CLASS_ACCESS_FLAGS_COLUMN_INDEX:
                default:
                   return String.class;
            }
        }
        
        protected Object doGetValueAt(int row, int column) {

            InnerClassesEntry innerClassesEntry = innerClasses[row];
            
            switch (column) {
                case INNER_CLASS_INFO_INDEX_COLUMN_INDEX:
                    return CPINFO_LINK_TEXT + String.valueOf(innerClassesEntry.getInnerClassInfoIndex());
                case OUTER_CLASS_INFO_INDEX_COLUMN_INDEX:
                    return CPINFO_LINK_TEXT + String.valueOf(innerClassesEntry.getOuterClassInfoIndex());
                case INNER_NAME_INDEX_COLUMN_INDEX:
                    return CPINFO_LINK_TEXT + String.valueOf(innerClassesEntry.getInnerNameIndex());
                case INNER_CLASS_ACCESS_FLAGS_COLUMN_INDEX:
                    return innerClassesEntry.getInnerClassFormattedAccessFlags() + 
                           " [" + innerClassesEntry.getInnerClassAccessFlagsVerbose() + "]";
                default:
                    return "";
            }
        }
    }
}
