/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.ConstantPoolHyperlinkListener;
import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.attributes.LocalVariableTableAttribute;
import org.gjt.jclasslib.structures.attributes.LocalVariableTableEntry;

/**
    Detail pane showing a <tt>LocalVariableTable</tt> attribute.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.5 $ $Date: 2003-08-18 08:18:11 $
*/
public class LocalVariableTableAttributeDetailPane extends AbstractAttributeListDetailPane {

    /**
        Constructor.
        @param services the associated browser services.
     */
    public LocalVariableTableAttributeDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected AbstractAttributeTableModel createTableModel(AttributeInfo attribute) {
        return new AttributeTableModel(attribute);
    }
    
    protected float getRowHeightFactor() {
        return 2f;
    }

    private class AttributeTableModel extends AbstractAttributeTableModel {
        
        private static final int COLUMN_COUNT = BASE_COLUMN_COUNT + 5;
        
        private static final int START_PC_COLUMN_INDEX = BASE_COLUMN_COUNT;
        private static final int LENGTH_COLUMN_INDEX = BASE_COLUMN_COUNT + 1;
        private static final int INDEX_COLUMN_INDEX = BASE_COLUMN_COUNT + 2;
        private static final int NAME_COLUMN_INDEX = BASE_COLUMN_COUNT + 3;
        private static final int DESCRIPTOR_COLUMN_INDEX = BASE_COLUMN_COUNT + 4;
        
        private static final int COMMENT_LINK_COLUMN_WIDTH = 200;

        private LocalVariableTableEntry[] localVariableTable;
        
        private AttributeTableModel(AttributeInfo attribute) {
            super(attribute);
            localVariableTable = ((LocalVariableTableAttribute)attribute).getLocalVariableTable();
        }

        public int getColumnWidth(int column) {
            switch (column) {
                case START_PC_COLUMN_INDEX:
                case LENGTH_COLUMN_INDEX:
                case INDEX_COLUMN_INDEX:
                   return NUMBER_COLUMN_WIDTH;
                   
                case NAME_COLUMN_INDEX:
                case DESCRIPTOR_COLUMN_INDEX:
                    return COMMENT_LINK_COLUMN_WIDTH;
                
                default:
                   return LINK_COLUMN_WIDTH;
            }
        }
        
        public void link(int row, int column) {
            
            int constantPoolIndex;
            switch (column) {
                case NAME_COLUMN_INDEX:
                    constantPoolIndex = localVariableTable[row].getNameIndex();
                    break;
                case DESCRIPTOR_COLUMN_INDEX:
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
                case NAME_COLUMN_INDEX:
                    return "name";
                case DESCRIPTOR_COLUMN_INDEX:
                    return "descriptor";
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
                case NAME_COLUMN_INDEX:
                case DESCRIPTOR_COLUMN_INDEX:
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
                case NAME_COLUMN_INDEX:
                    return createCommentLink(localVariableTableEntry.getNameIndex());
                case DESCRIPTOR_COLUMN_INDEX:
                    return createCommentLink(localVariableTableEntry.getDescriptorIndex());
                default:
                    return "";
            }
        }

    }
}
