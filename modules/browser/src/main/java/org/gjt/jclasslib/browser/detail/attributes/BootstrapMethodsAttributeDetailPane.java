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
import org.gjt.jclasslib.util.MultiLineHtmlCellHandler;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.TableColumn;

/**
 * Detail pane showing an <tt>BootstrapMethods</tt> attribute.
 */
public class BootstrapMethodsAttributeDetailPane extends AbstractAttributeListDetailPane {

    /**
     * Constructor.
     *
     * @param services the associated browser services.
     */
    public BootstrapMethodsAttributeDetailPane(BrowserServices services) {
        super(services);
    }

    protected AbstractAttributeTableModel createTableModel(AttributeInfo attribute) {
        return new AttributeTableModel(attribute);
    }

    @Override
    protected boolean isVariableRowHeight() {
        return true;
    }

    @Override
    protected int getAutoResizeMode() {
        return JTable.AUTO_RESIZE_LAST_COLUMN;
    }

    @Override
    protected void adjustColumn(TableColumn tableColumn, int column) {
        super.adjustColumn(tableColumn, column);
        if (column == AttributeTableModel.BOOTSTRAP_ARGUMENTS_INFO_INDEX_COLUMN_INDEX) {
            tableColumn.setCellRenderer(new MultiLineHtmlCellHandler());
            MultiLineHtmlCellHandler cellHandler = new MultiLineHtmlCellHandler();
            cellHandler.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        ConstantPoolHyperlinkListener.Companion.link(getBrowserServices(), Integer.parseInt(e.getDescription()));
                    }
                }
            });
            tableColumn.setCellEditor(cellHandler);
        }
    }

    private class AttributeTableModel extends AbstractAttributeTableModel {

        private static final int COLUMN_COUNT = BASE_COLUMN_COUNT + 2;

        private static final int BOOTSTRAP_METHOD_REF_INFO_INDEX_COLUMN_INDEX = BASE_COLUMN_COUNT;
        private static final int BOOTSTRAP_ARGUMENTS_INFO_INDEX_COLUMN_INDEX = BASE_COLUMN_COUNT + 1;

        private static final int METHOD_REF_LINK_COLUMN_WIDTH = 300;
        private static final int ARGUMENTS_REF_LINK_COLUMN_WIDTH = 400;

        private BootstrapMethodsEntry[] bootstrapMethods;

        private AttributeTableModel(AttributeInfo attribute) {
            super(attribute);
            bootstrapMethods = ((BootstrapMethodsAttribute)attribute).getMethods();
        }

        public int getColumnWidth(int column) {
            switch (column) {
                case BOOTSTRAP_METHOD_REF_INFO_INDEX_COLUMN_INDEX:
                    return METHOD_REF_LINK_COLUMN_WIDTH;
                case BOOTSTRAP_ARGUMENTS_INFO_INDEX_COLUMN_INDEX:
                    return ARGUMENTS_REF_LINK_COLUMN_WIDTH;

                default:
                    return LINK_COLUMN_WIDTH;
            }
        }

        public void link(int row, int column) {

            int constantPoolIndex;
            switch (column) {
                case BOOTSTRAP_METHOD_REF_INFO_INDEX_COLUMN_INDEX:
                    constantPoolIndex = bootstrapMethods[row].getMethodRefIndex();
                    break;
                default:
                    return;
            }
            ConstantPoolHyperlinkListener.Companion.link(services, constantPoolIndex);
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
                case BOOTSTRAP_ARGUMENTS_INFO_INDEX_COLUMN_INDEX:
                    return "arguments";
                default:
                    return "";
            }
        }

        protected Class doGetColumnClass(int column) {
            switch (column) {
                case BOOTSTRAP_METHOD_REF_INFO_INDEX_COLUMN_INDEX:
                    return Link.class;
                case BOOTSTRAP_ARGUMENTS_INFO_INDEX_COLUMN_INDEX:
                default:
                    return String.class;
            }
        }

        protected Object doGetValueAt(int row, int column) {

            BootstrapMethodsEntry bootstrapMethodsEntry = bootstrapMethods[row];
            switch (column) {
                case BOOTSTRAP_METHOD_REF_INFO_INDEX_COLUMN_INDEX:
                    return createCommentLink(bootstrapMethodsEntry.getMethodRefIndex());
                case BOOTSTRAP_ARGUMENTS_INFO_INDEX_COLUMN_INDEX:
                    return bootstrapMethodsEntry.getVerbose().replace("\n", "<br>");
                default:
                    return "";
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == BOOTSTRAP_ARGUMENTS_INFO_INDEX_COLUMN_INDEX;
        }
    }
}
