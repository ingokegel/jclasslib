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
import org.gjt.jclasslib.structures.attributes.LocalVariableCommonAttribute;
import org.gjt.jclasslib.structures.attributes.LocalVariableCommonEntry;

/**
 * Contains common attributes to a local variable detail pane.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 * @version $Revision: 1.1 $ $Date: 2004-12-28 13:04:30 $
 */
public abstract class LocalVariableCommonAttributeDetailPane extends AbstractAttributeListDetailPane {

    /**
     * Constructor.
     *
     * @param services the associated browser services.
     */
    public LocalVariableCommonAttributeDetailPane(BrowserServices services) {
        super(services);
    }

    protected AbstractAttributeTableModel createTableModel(AttributeInfo attribute, String descriptorOrSignatureDescription) {
        return new AttributeTableModel(attribute, descriptorOrSignatureDescription);
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
        private static final int DESCRIPTOR_OR_SIGNATURE_COLUMN_INDEX = BASE_COLUMN_COUNT + 4;

        private final String descriptorOrSignatureVerbose;

        private static final int COMMENT_LINK_COLUMN_WIDTH = 200;

        private LocalVariableCommonEntry[] localVariableEntries;

        private AttributeTableModel(AttributeInfo attribute,
                                    String descriptorOrSignatureVerbose) {

            super(attribute);
            localVariableEntries = ((LocalVariableCommonAttribute)attribute).getLocalVariableEntries();
            this.descriptorOrSignatureVerbose = descriptorOrSignatureVerbose;
        }

        public int getColumnWidth(int column) {
            switch (column) {
                case START_PC_COLUMN_INDEX:
                case LENGTH_COLUMN_INDEX:
                case INDEX_COLUMN_INDEX:
                    return NUMBER_COLUMN_WIDTH;

                case NAME_COLUMN_INDEX:
                case DESCRIPTOR_OR_SIGNATURE_COLUMN_INDEX:
                    return COMMENT_LINK_COLUMN_WIDTH;

                default:
                    return LINK_COLUMN_WIDTH;
            }
        }

        public void link(int row, int column) {
            int constantPoolIndex;
            switch (column) {
                case NAME_COLUMN_INDEX:
                    constantPoolIndex = localVariableEntries[row].getNameIndex();
                    break;
                case DESCRIPTOR_OR_SIGNATURE_COLUMN_INDEX:
                    constantPoolIndex = localVariableEntries[row].getDescriptorOrSignatureIndex();
                    break;
                default:
                    return;
            }
            ConstantPoolHyperlinkListener.link(services, constantPoolIndex);
        }

        public int getRowCount() {
            return localVariableEntries.length;
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
                case DESCRIPTOR_OR_SIGNATURE_COLUMN_INDEX:
                    return descriptorOrSignatureVerbose;
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
                case DESCRIPTOR_OR_SIGNATURE_COLUMN_INDEX:
                    return Link.class;
                default:
                    return String.class;
            }
        }

        protected Object doGetValueAt(int row, int column) {
            LocalVariableCommonEntry localVariableEntry = localVariableEntries[row];

            switch (column) {
                case START_PC_COLUMN_INDEX:
                    return String.valueOf(localVariableEntry.getStartPc());
                case LENGTH_COLUMN_INDEX:
                    return String.valueOf(localVariableEntry.getLength());
                case INDEX_COLUMN_INDEX:
                    return String.valueOf(localVariableEntry.getIndex());
                case NAME_COLUMN_INDEX:
                    return createCommentLink(localVariableEntry.getNameIndex());
                case DESCRIPTOR_OR_SIGNATURE_COLUMN_INDEX:
                    return createCommentLink(localVariableEntry.getDescriptorOrSignatureIndex());
                default:
                    return "";
            }
        }
    }
}
