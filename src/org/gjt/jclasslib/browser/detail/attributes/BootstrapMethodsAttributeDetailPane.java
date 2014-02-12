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

/**
    Detail pane showing an <tt>BootstrapMethods</tt> attribute.

*/
public class BootstrapMethodsAttributeDetailPane extends AbstractAttributeListDetailPane {

    /**
        Constructor.
        @param services the associated browser services.
     */
    public BootstrapMethodsAttributeDetailPane(BrowserServices services) {
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
        
        private static final int BOOTSTRAP_METHOD_REF_INFO_INDEX_COLUMN_INDEX = BASE_COLUMN_COUNT;
        private static final int BOOTSTAP_ARGUMENTS_INFO_INDEX_COLUMN_INDEX = BASE_COLUMN_COUNT + 1;
     
        private static final int METHOD_REF_LINK_COLUMN_WIDTH = 200;
        private static final int ARGUMENTS_REF_LINK_COLUMN_WIDTH = 50;
        
        private BootstrapMethodsEntry[] bootstrapMethods;
        
        private AttributeTableModel(AttributeInfo attribute) {
            super(attribute);
            bootstrapMethods = ((BootstrapMethodsAttribute)attribute).getMethods();
        }

        public int getColumnWidth(int column) {
            switch (column) {
                case BOOTSTRAP_METHOD_REF_INFO_INDEX_COLUMN_INDEX:
                   return METHOD_REF_LINK_COLUMN_WIDTH;
                case BOOTSTAP_ARGUMENTS_INFO_INDEX_COLUMN_INDEX:
                    return ARGUMENTS_REF_LINK_COLUMN_WIDTH;
                    
                default:
                   return LINK_COLUMN_WIDTH;
            }
        }
        
        public void link(int row, int column) {
            
            int constantPoolIndex;
            switch (column) {
                case BOOTSTRAP_METHOD_REF_INFO_INDEX_COLUMN_INDEX:
                    constantPoolIndex = bootstrapMethods[row].getMethodRefIndexIndex();
                    break;
                default:
                    return;
            }
            ConstantPoolHyperlinkListener.link(services, constantPoolIndex);
        }
        
        public int getRowCount() {
            return bootstrapMethods.length;
        }
        
        public int getColumnCount() {
            return COLUMN_COUNT;
        }
        
        protected String doGetColumnName(int column) {
            switch (column) {
                case BOOTSTRAP_METHOD_REF_INFO_INDEX_COLUMN_INDEX:
                   return "bootstrap_method";
                case BOOTSTAP_ARGUMENTS_INFO_INDEX_COLUMN_INDEX:
                   return "arguments";
                default:
                   return "";
            }
        }
        
        protected Class doGetColumnClass(int column) {
            switch (column) {
                case BOOTSTRAP_METHOD_REF_INFO_INDEX_COLUMN_INDEX:
                   return Link.class;
                case BOOTSTAP_ARGUMENTS_INFO_INDEX_COLUMN_INDEX:
                default:
                   return String.class;
            }
        }
        
        protected Object doGetValueAt(int row, int column) {

        	BootstrapMethodsEntry bootstrapMethodsEntry = bootstrapMethods[row];
            switch (column) {
                case BOOTSTRAP_METHOD_REF_INFO_INDEX_COLUMN_INDEX:
                    return createCommentLink(bootstrapMethodsEntry.getMethodRefIndexIndex());
                case BOOTSTAP_ARGUMENTS_INFO_INDEX_COLUMN_INDEX:
                    return bootstrapMethodsEntry.printArguments();
                default:
                    return "";
            }
        }
    }
}
