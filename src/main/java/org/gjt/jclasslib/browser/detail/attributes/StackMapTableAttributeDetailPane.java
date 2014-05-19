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
import org.gjt.jclasslib.structures.attributes.StackMapFrameEntry;
import org.gjt.jclasslib.structures.attributes.StackMapTableAttribute;
import org.gjt.jclasslib.util.MultiLineHtmlCellHandler;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.table.TableColumn;

/**
 * Detail pane showing an <tt>BootstrapMethods</tt> attribute.
 */
public class StackMapTableAttributeDetailPane extends AbstractAttributeListDetailPane {

    /**
     * Constructor.
     *
     * @param services the associated browser services.
     */
    public StackMapTableAttributeDetailPane(BrowserServices services) {
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
        if (column == AttributeTableModel.STACK_MAP_FRAME_COLUMN_INDEX) {
            tableColumn.setCellRenderer(new MultiLineHtmlCellHandler());
            MultiLineHtmlCellHandler cellHandler = new MultiLineHtmlCellHandler();
            cellHandler.addHyperlinkListener(new HyperlinkListener() {
                @Override
                public void hyperlinkUpdate(HyperlinkEvent e) {
                    if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                        ConstantPoolHyperlinkListener.link(getBrowserServices(), Integer.parseInt(e.getDescription()));
                    }
                }
            });
            tableColumn.setCellEditor(cellHandler);
        } else {
            tableColumn.setMaxWidth(tableColumn.getWidth());
        }
    }

    private class AttributeTableModel extends AbstractAttributeTableModel {

        private static final int COLUMN_COUNT = BASE_COLUMN_COUNT + 1;

        private static final int STACK_MAP_FRAME_COLUMN_INDEX = BASE_COLUMN_COUNT;

        private static final int STACK_MAP_FRAME_COLUMN_WIDTH = 600;

        private StackMapFrameEntry[] entries;

        private AttributeTableModel(AttributeInfo attribute) {
            super(attribute);
            entries = ((StackMapTableAttribute)attribute).getEntries();
        }

        public int getColumnWidth(int column) {
            switch (column) {
                case STACK_MAP_FRAME_COLUMN_INDEX:
                    return STACK_MAP_FRAME_COLUMN_WIDTH;
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
                case STACK_MAP_FRAME_COLUMN_INDEX:
                    return "stack_map_frame";
                default:
                    return "";
            }
        }

        protected Class doGetColumnClass(int column) {
            switch (column) {
                case STACK_MAP_FRAME_COLUMN_INDEX:
                default:
                    return String.class;
            }
        }

        protected Object doGetValueAt(int row, int column) {

            StackMapFrameEntry entry = entries[row];
            switch (column) {
                case STACK_MAP_FRAME_COLUMN_INDEX:
                    return entry.getVerbose();
                default:
                    return "";
            }
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == STACK_MAP_FRAME_COLUMN_INDEX;
        }
    }

}
