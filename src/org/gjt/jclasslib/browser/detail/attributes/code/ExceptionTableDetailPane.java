/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes.code;

import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.ConstantPoolHyperlinkListener;
import org.gjt.jclasslib.browser.detail.ListDetailPane;
import org.gjt.jclasslib.browser.detail.attributes.AbstractAttributeListDetailPane;
import org.gjt.jclasslib.browser.detail.attributes.AbstractAttributeTableModel;
import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.attributes.CodeAttribute;
import org.gjt.jclasslib.structures.attributes.ExceptionTableEntry;

/**
    Detail pane showing the exception table about a <tt>Code</tt> attribute.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 08:19:37 $
*/
public class ExceptionTableDetailPane extends AbstractAttributeListDetailPane {

    /**
        Constructor.
        @param services the associated browser services.
     */
    public ExceptionTableDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected AbstractAttributeTableModel createTableModel(AttributeInfo attribute) {
        return new AttributeTableModel(attribute);
    }
    
    private class AttributeTableModel extends AbstractAttributeTableModel {
        
        private static final int COLUMN_COUNT = BASE_COLUMN_COUNT + 5;
        
        private static final int START_PC_COLUMN_INDEX = BASE_COLUMN_COUNT;
        private static final int END_PC_COLUMN_INDEX = BASE_COLUMN_COUNT + 1;
        private static final int HANDLER_PC_COLUMN_INDEX = BASE_COLUMN_COUNT + 2;
        private static final int CATCH_TYPE_COLUMN_INDEX = BASE_COLUMN_COUNT + 3;
        private static final int CATCH_TYPE_VERBOSE_COLUMN_INDEX = BASE_COLUMN_COUNT + 4;
        
        private static final int HANDLER_PC_COLUMN_WIDTH = 70;
        
        private ExceptionTableEntry[] exceptionTable;
        
        private AttributeTableModel(AttributeInfo attribute) {
            super(attribute);
            exceptionTable = ((CodeAttribute)attribute).getExceptionTable();
        }

        public int getColumnWidth(int column) {
            switch (column) {
                case START_PC_COLUMN_INDEX:
                case END_PC_COLUMN_INDEX:
                   return NUMBER_COLUMN_WIDTH;

                case HANDLER_PC_COLUMN_INDEX:
                   return HANDLER_PC_COLUMN_WIDTH;
                   
                case CATCH_TYPE_COLUMN_INDEX:
                   return LINK_COLUMN_WIDTH;
                    
                case CATCH_TYPE_VERBOSE_COLUMN_INDEX:
                default:
                   return VERBOSE_COLUMN_WIDTH;
            }
        }
        
        public void link(int row, int column) {
            
            if (column == CATCH_TYPE_COLUMN_INDEX) {
                int constantPoolIndex = exceptionTable[row].getCatchType();
                ConstantPoolHyperlinkListener.link(services, constantPoolIndex);
            }
        }
        
        public int getRowCount() {
            return exceptionTable.length;
        }
        
        public int getColumnCount() {
            return COLUMN_COUNT;
        }
        
        protected String doGetColumnName(int column) {
            switch (column) {
                case START_PC_COLUMN_INDEX:
                   return "start_pc";
                case END_PC_COLUMN_INDEX:
                   return "end_pc";
                case HANDLER_PC_COLUMN_INDEX:
                   return "handler_pc";
                case CATCH_TYPE_COLUMN_INDEX:
                   return "catch_type";
                case CATCH_TYPE_VERBOSE_COLUMN_INDEX:
                   return "verbose";
                default:
                   return "";
            }
        }
        
        protected Class doGetColumnClass(int column) {
            switch (column) {
                case START_PC_COLUMN_INDEX:
                case END_PC_COLUMN_INDEX:
                case HANDLER_PC_COLUMN_INDEX:
                   return Number.class;
                case CATCH_TYPE_COLUMN_INDEX:
                   return ListDetailPane.Link.class;
                case CATCH_TYPE_VERBOSE_COLUMN_INDEX:
                default:
                   return String.class;
            }
        }
        
        protected Object doGetValueAt(int row, int column) {

            ExceptionTableEntry exceptionTableEntry = exceptionTable[row];
            int catchType = exceptionTableEntry.getCatchType();
            
            switch (column) {
                case START_PC_COLUMN_INDEX:
                    return String.valueOf(exceptionTableEntry.getStartPc());
                case END_PC_COLUMN_INDEX:
                    return String.valueOf(exceptionTableEntry.getEndPc());
                case HANDLER_PC_COLUMN_INDEX:
                    return String.valueOf(exceptionTableEntry.getHandlerPc());
                case CATCH_TYPE_COLUMN_INDEX:
                    if (catchType == 0) {
                        return "";
                    } else {
                        return CPINFO_LINK_TEXT + String.valueOf(catchType);
                    }
                case CATCH_TYPE_VERBOSE_COLUMN_INDEX:
                    if (catchType == 0) {
                        return "any";
                    } else {
                        return getConstantPoolEntryName(exceptionTableEntry.getCatchType());
                    }
                default:
                    return "";
            }
        }
    }
}
