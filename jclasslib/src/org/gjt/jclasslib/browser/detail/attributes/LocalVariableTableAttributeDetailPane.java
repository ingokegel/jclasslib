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
    Detail pane showing a <tt>LocalVariableTable</tt> attribute.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.3 $ $Date: 2002-02-27 16:47:42 $
*/
public class LocalVariableTableAttributeDetailPane extends AbstractAttributeListDetailPane {

    public LocalVariableTableAttributeDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected AbstractAttributeTableModel createTableModel(AttributeInfo attribute) {
        return new AttributeTableModel(attribute);
    }
    
    private class AttributeTableModel extends AbstractAttributeTableModel {
        
        private static final int COLUMN_COUNT = BASE_COLUMN_COUNT + 7;
        
        private static final int START_PC_COLUMN_INDEX = BASE_COLUMN_COUNT;
        private static final int LENGTH_COLUMN_INDEX = BASE_COLUMN_COUNT + 1;
        private static final int INDEX_COLUMN_INDEX = BASE_COLUMN_COUNT + 2;
        private static final int NAME_INDEX_COLUMN_INDEX = BASE_COLUMN_COUNT + 3;
        private static final int NAME_INDEX_COLUMN_VERBOSE_INDEX = BASE_COLUMN_COUNT + 4;
        private static final int DESCRIPTOR_INDEX_COLUMN_INDEX = BASE_COLUMN_COUNT + 5;
        private static final int DESCRIPTOR_INDEX_VERBOSE_COLUMN_INDEX = BASE_COLUMN_COUNT + 6;
        
        private static final int DESCRIPTOR_INDEX_COLUMN_WIDTH = 100;
        
        private LocalVariableTableEntry[] localVariableTable;
        
        public AttributeTableModel(AttributeInfo attribute) {
            super(attribute);
            localVariableTable = ((LocalVariableTableAttribute)attribute).getLocalVariableTable();
            initRowNumberStrings();
        }

        public int getColumnWidth(int column) {
            switch (column) {
                case START_PC_COLUMN_INDEX:
                case LENGTH_COLUMN_INDEX:
                case INDEX_COLUMN_INDEX:
                   return NUMBER_COLUMN_WIDTH;
                   
                case DESCRIPTOR_INDEX_COLUMN_INDEX:
                   return DESCRIPTOR_INDEX_COLUMN_WIDTH;
                
                case NAME_INDEX_COLUMN_VERBOSE_INDEX:
                case DESCRIPTOR_INDEX_VERBOSE_COLUMN_INDEX:
                    return SHORT_VERBOSE_COLUMN_WIDTH;
                
                case NAME_INDEX_COLUMN_INDEX:
                default:
                   return LINK_COLUMN_WIDTH;
            }
        }
        
        public void link(int row, int column) {
            
            int constantPoolIndex = 0;
            switch (column) {
                case NAME_INDEX_COLUMN_INDEX:
                    constantPoolIndex = localVariableTable[row].getNameIndex();
                    break;
                case DESCRIPTOR_INDEX_COLUMN_INDEX:
                    constantPoolIndex = localVariableTable[row].getDescriptorIndex();
                    break;
                default:
                    return;
            }
            ConstantPoolHyperlinkListener.link(services, constantPoolIndex);
        }
        
        public int getRowCount() {
            return localVariableTable.length;
        }
        
        public int getColumnCount() {
            return COLUMN_COUNT;
        }
        
        protected String doGetColumnName(int column) {
            switch (column) {
                case START_PC_COLUMN_INDEX:
                   return "start_pc";
                case LENGTH_COLUMN_INDEX:
                   return "length";
                case INDEX_COLUMN_INDEX:
                   return "index";
                case NAME_INDEX_COLUMN_INDEX:
                   return "name_index";
                case NAME_INDEX_COLUMN_VERBOSE_INDEX:
                    return "name verbose";
                case DESCRIPTOR_INDEX_COLUMN_INDEX:
                   return "descriptor_index";
                case DESCRIPTOR_INDEX_VERBOSE_COLUMN_INDEX:
                    return "descriptor verbose";
                default:
                   return "";
            }
        }
        
        protected Class doGetColumnClass(int column) {
            switch (column) {
                case START_PC_COLUMN_INDEX:
                case LENGTH_COLUMN_INDEX:
                case INDEX_COLUMN_INDEX:
                   return Number.class;
                case NAME_INDEX_COLUMN_INDEX:
                case DESCRIPTOR_INDEX_COLUMN_INDEX:
                   return Link.class;
                default:
                   return String.class;
            }
        }
        
        protected Object doGetValueAt(int row, int column) {

            LocalVariableTableEntry localVariableTableEntry = localVariableTable[row];
            
            switch (column) {
                case START_PC_COLUMN_INDEX:
                    return String.valueOf(localVariableTableEntry.getStartPc());
                case LENGTH_COLUMN_INDEX:
                    return String.valueOf(localVariableTableEntry.getLength());
                case INDEX_COLUMN_INDEX:
                    return String.valueOf(localVariableTableEntry.getIndex());
                case NAME_INDEX_COLUMN_INDEX:
                    return CPINFO_LINK_TEXT + String.valueOf(localVariableTableEntry.getNameIndex());
                case NAME_INDEX_COLUMN_VERBOSE_INDEX:
                    return getConstantPoolEntryName(localVariableTableEntry.getNameIndex());
                case DESCRIPTOR_INDEX_COLUMN_INDEX:
                    return CPINFO_LINK_TEXT + String.valueOf(localVariableTableEntry.getDescriptorIndex());
                case DESCRIPTOR_INDEX_VERBOSE_COLUMN_INDEX:
                    return getConstantPoolEntryName(localVariableTableEntry.getDescriptorIndex());
                default:
                    return "";
            }
        }
    }
}
