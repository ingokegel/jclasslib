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
import org.gjt.jclasslib.structures.attributes.InnerClassesAttribute;
import org.gjt.jclasslib.structures.attributes.InnerClassesEntry;

/**
    Detail pane showing an <tt>InnerClasses</tt> attribute.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.5 $ $Date: 2003-08-18 08:18:11 $
*/
public class InnerClassesAttributeDetailPane extends AbstractAttributeListDetailPane {

    /**
        Constructor.
        @param services the associated browser services.
     */
    public InnerClassesAttributeDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected AbstractAttributeTableModel createTableModel(AttributeInfo attribute) {
        return new AttributeTableModel(attribute);
    }

    protected float getRowHeightFactor() {
        return 2f;
    }

    private class AttributeTableModel extends AbstractAttributeTableModel {
        
        private static final int COLUMN_COUNT = BASE_COLUMN_COUNT + 4;
        
        private static final int INNER_CLASS_INFO_INDEX_COLUMN_INDEX = BASE_COLUMN_COUNT;
        private static final int OUTER_CLASS_INFO_INDEX_COLUMN_INDEX = BASE_COLUMN_COUNT + 1;
        private static final int INNER_NAME_INDEX_COLUMN_INDEX = BASE_COLUMN_COUNT + 2;
        private static final int INNER_CLASS_ACCESS_FLAGS_COLUMN_INDEX = BASE_COLUMN_COUNT + 3;

        private static final int CLASS_LINK_COLUMN_WIDTH = 160;
        private static final int NAME_LINK_COLUMN_WIDTH = 110;
        private static final int INNER_CLASS_ACCESS_FLAGS_COLUMN_WIDTH = 200;
        
        private InnerClassesEntry[] innerClasses;
        
        private AttributeTableModel(AttributeInfo attribute) {
            super(attribute);
            innerClasses = ((InnerClassesAttribute)attribute).getClasses();
        }

        public int getColumnWidth(int column) {
            switch (column) {
                case INNER_CLASS_INFO_INDEX_COLUMN_INDEX:
                case OUTER_CLASS_INFO_INDEX_COLUMN_INDEX:
                   return CLASS_LINK_COLUMN_WIDTH;
                case INNER_NAME_INDEX_COLUMN_INDEX:
                    return NAME_LINK_COLUMN_WIDTH;

                case INNER_CLASS_ACCESS_FLAGS_COLUMN_INDEX:
                   return INNER_CLASS_ACCESS_FLAGS_COLUMN_WIDTH;
                    
                default:
                   return LINK_COLUMN_WIDTH;
            }
        }
        
        public void link(int row, int column) {
            
            int constantPoolIndex;
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
                    return createCommentLink(innerClassesEntry.getInnerClassInfoIndex());
                case OUTER_CLASS_INFO_INDEX_COLUMN_INDEX:
                    return createCommentLink(innerClassesEntry.getOuterClassInfoIndex());
                case INNER_NAME_INDEX_COLUMN_INDEX:
                    return createCommentLink(innerClassesEntry.getInnerNameIndex());
                case INNER_CLASS_ACCESS_FLAGS_COLUMN_INDEX:
                    return innerClassesEntry.getInnerClassFormattedAccessFlags() +
                           " [" + innerClassesEntry.getInnerClassAccessFlagsVerbose() + "]";
                default:
                    return "";
            }
        }
    }
}
