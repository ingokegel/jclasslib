/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.attributes.LineNumberTableAttribute;
import org.gjt.jclasslib.structures.attributes.LineNumberTableEntry;

/**
    Detail pane showing a <tt>LineNumberTable</tt> attribute.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.5 $ $Date: 2003-08-18 08:18:35 $
*/
public class LineNumberTableAttributeDetailPane extends AbstractAttributeListDetailPane {

    /**
        Constructor.
        @param services the associated browser services.
     */
    public LineNumberTableAttributeDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected AbstractAttributeTableModel createTableModel(AttributeInfo attribute) {
        return new AttributeTableModel(attribute);
    }
    
    private class AttributeTableModel extends AbstractAttributeTableModel {
        
        private static final int COLUMN_COUNT = BASE_COLUMN_COUNT + 2;
        
        private static final int START_PC_COLUMN_INDEX = BASE_COLUMN_COUNT;
        private static final int LINE_NUMBER_COLUMN_INDEX = BASE_COLUMN_COUNT + 1;
        
        private static final int LINE_NUMBER_COLUMN_WIDTH = 100;
        
        private LineNumberTableEntry[] lineNumberTable;
        
        private AttributeTableModel(AttributeInfo attribute) {
            super(attribute);
            lineNumberTable = ((LineNumberTableAttribute)attribute).getLineNumberTable();
        }
        
        public int getColumnWidth(int column) {
            switch (column) {
                case START_PC_COLUMN_INDEX:
                    return NUMBER_COLUMN_WIDTH;
                case LINE_NUMBER_COLUMN_INDEX:
                    return LINE_NUMBER_COLUMN_WIDTH;
                default:
                   return NUMBER_COLUMN_WIDTH;
            }
        }
        
        public int getRowCount() {
            return lineNumberTable.length;
        }
        
        public int getColumnCount() {
            return COLUMN_COUNT;
        }
        
        protected String doGetColumnName(int column) {
            switch (column) {
                case START_PC_COLUMN_INDEX:
                   return "start_pc";
                case LINE_NUMBER_COLUMN_INDEX:
                   return "line_number";
                default:
                   return "";
            }
        }
        
        protected Class doGetColumnClass(int column) {
            return Number.class;
        }
        
        protected Object doGetValueAt(int row, int column) {

            LineNumberTableEntry lineNumberTableEntry = lineNumberTable[row];
            
            switch (column) {
                case START_PC_COLUMN_INDEX:
                    return String.valueOf(lineNumberTableEntry.getStartPc());
                case LINE_NUMBER_COLUMN_INDEX:
                    return String.valueOf(lineNumberTableEntry.getLineNumber());
                default:
                    return "";
            }
        }
    }
}

