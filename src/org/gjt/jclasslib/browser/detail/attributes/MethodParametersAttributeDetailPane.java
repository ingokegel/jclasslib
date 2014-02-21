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
import org.gjt.jclasslib.structures.attributes.BootstrapMethodsAttribute;
import org.gjt.jclasslib.structures.attributes.BootstrapMethodsEntry;
import org.gjt.jclasslib.structures.attributes.InnerClassesAttribute;
import org.gjt.jclasslib.structures.attributes.InnerClassesEntry;
import org.gjt.jclasslib.structures.attributes.MethodParametersAttribute;
import org.gjt.jclasslib.structures.attributes.MethodParametersEntry;
import org.gjt.jclasslib.structures.attributes.StackMapFrameEntry;
import org.gjt.jclasslib.structures.attributes.StackMapTableAttribute;

/**
    Detail pane showing an <tt>BootstrapMethods</tt> attribute.

*/
public class MethodParametersAttributeDetailPane extends AbstractAttributeListDetailPane {

    /**
        Constructor.
        @param services the associated browser services.
     */
    public MethodParametersAttributeDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected AbstractAttributeTableModel createTableModel(AttributeInfo attribute) {
        return new AttributeTableModel(attribute);
    }

    protected float getRowHeightFactor() {
        return 2f;
    }

    private class AttributeTableModel extends AbstractAttributeTableModel {
        
        private static final int COLUMN_COUNT = BASE_COLUMN_COUNT + 2;
        
        private static final int PARAM_NAME_INDEX = BASE_COLUMN_COUNT;
        private static final int ACCESS_FLAG_INDEX = BASE_COLUMN_COUNT + 1;
            
        private static final int PARAM_NAME_COLUMN_WIDTH = 200;
        private static final int ACCESS_FLAG_COLUMN_WIDTH = 200;
        
        private MethodParametersEntry[] entries;
        
        private AttributeTableModel(AttributeInfo attribute) {
            super(attribute);
            entries = ((MethodParametersAttribute)attribute).getEntries();
        }

        public int getColumnWidth(int column) {
            switch (column) {
             case PARAM_NAME_INDEX:
            	 return PARAM_NAME_COLUMN_WIDTH;
             case ACCESS_FLAG_INDEX:
                 return ACCESS_FLAG_COLUMN_WIDTH;
             default:
            	 return LINK_COLUMN_WIDTH;
            }
        }
        
        
        public int getRowCount() {
            return entries.length;
        }
        
        public int getColumnCount() {
            return COLUMN_COUNT;
        }
        
        protected String doGetColumnName(int column) {
            switch (column) {
            case PARAM_NAME_INDEX:
                return "param_name";
            case ACCESS_FLAG_INDEX:
                return "access_flags";
                default:
                   return "";
            }
        }
        
        protected Class doGetColumnClass(int column) {
            switch (column) {
                case PARAM_NAME_INDEX:
                	return Link.class;
                case ACCESS_FLAG_INDEX:
                default:
                   return String.class;
            }
        }
        
        protected Object doGetValueAt(int row, int column) {

        	MethodParametersEntry entry = entries[row];
            switch (column) {
                 case PARAM_NAME_INDEX:
                	 return createCommentLink(entry.getNameIndex());
                 case ACCESS_FLAG_INDEX:
                	 return ""+entry.getAccessFlags();
                default:
                    return "";
            }
        }
    }
}
