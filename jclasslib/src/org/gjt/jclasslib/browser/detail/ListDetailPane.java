/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.AbstractDetailPane;
import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.detail.attributes.LinkRenderer;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;

/**
    Base class for all detail panes with a structure of
    a variable number of row entries with the same number of columns.
    
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.5 $ $Date: 2003-08-18 08:11:01 $
*/
public abstract class ListDetailPane extends AbstractDetailPane {

    // Visual components

    private JTable table;
    
    /**
        Constructor.
        @param services the associated browser services.
     */
    protected ListDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupComponent() {

        setLayout(new BorderLayout());
        
        table = new JTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        float rowHeightFactor = getRowHeightFactor();
        if (rowHeightFactor != 1f) {
            table.setRowHeight((int)(table.getRowHeight() * rowHeightFactor));
        }

        TableLinkListener linkListener = new TableLinkListener();
        table.addMouseListener(linkListener);
        table.addMouseMotionListener(linkListener);


        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void show(TreePath treePath) {
        TableModel tableModel = getTableModel(treePath);
        table.setModel(tableModel);

        createTableColumnModel(table, tableModel);
        ((JLabel)table.getDefaultRenderer(Number.class)).setVerticalAlignment(JLabel.TOP);
        ((JLabel)table.getDefaultRenderer(String.class)).setVerticalAlignment(JLabel.TOP);
        table.setDefaultRenderer(Link.class, new LinkRenderer());

    }

    /**
        Get the factor for calculating the row height as a multiple of the normal row
        height of a single label.
        @return the factor.
     */
    protected float getRowHeightFactor() {
        return 1f;
    }

    /**
        Create the table column model for the given table and table column model.
        @param table the table
        @param tableModel the table model
     */
    protected void createTableColumnModel(JTable table, TableModel tableModel) {
        table.createDefaultColumnsFromModel();
    }

    /**
        Get the table model for the selected tree node.
        @param treePath the tree path selected in <tt>BrowserTreePane</tt>
        @return the table model
     */
    protected abstract TableModel getTableModel(TreePath treePath);

    /**
        Create a link value object with a comment for use of a <tt>LinkRenderer</tt>.
        @param index the constant pool index to link to.
        @return the link value object.
     */
    protected Object createCommentLink(int index) {
        return new LinkRenderer.LinkCommentValue(
                        CPINFO_LINK_TEXT + String.valueOf(index),
                        getConstantPoolEntryName(index)
        );
    }

    /**
        Link to the destination described by the target of the hyperlink
        contained in a specific cell.
        @param row the row number of the hyperlink
        @param column the column number of the hyperlink
     */
    protected void link(int row, int column) {
    }
    
    /**
        Class for caching dynamically computed values in a read only table.
     */
    public static class ColumnCache {
        
        private Object[][] cache;

        /**
         * Constructor.
         * @param rowNumber the row number.
         * @param columnNumber the column number.
         */
        public ColumnCache(int rowNumber, int columnNumber) {
            cache = new Object[rowNumber][columnNumber];
        }
        
        /**
            Get the cached value of a specific cell.
            @param row the row number of the cell
            @param column the column number of the cell
            @return the value
         */
        public Object getValueAt(int row, int column) {
            return cache[row][column];
        }

        /**
            Set the cached value of a specific cell.
            @param row the row number of the cell
            @param column the column number of the cell
            @param value the value
         */
        public void setValueAt(int row, int column, Object value) {
            cache[row][column] = value;
        }
        
    }

    /**
        Marker class returned by <tt>getColumnClass()</tt> to indicate a hyperlink
        in a table.
     */
    public static class Link {
    }
    
    private class TableLinkListener extends MouseAdapter
                                    implements MouseMotionListener
    {

        private Cursor defaultCursor;
        private int defaultCursorType;
        private Cursor handCursor;
        
        private TableLinkListener() {

            defaultCursor = Cursor.getDefaultCursor();
            defaultCursorType = defaultCursor.getType();
            handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        }
        
        public void mouseClicked(MouseEvent event) {
            
            Point point = event.getPoint();

            if (isLink(point)) {
                link(table.rowAtPoint(point), table.columnAtPoint(point));
            }
        }

        public void mouseDragged(MouseEvent event) {
        }
        
        public void mouseMoved(MouseEvent event) {

            Point point = event.getPoint();

            if (table.getCursor().getType() == defaultCursorType && isLink(point)) {
                table.setCursor(handCursor);
            } else if (!isLink(point)) {
                table.setCursor(defaultCursor);
            }
        }
        
        private boolean isLink(Point point) {

            int column = table.columnAtPoint(point);
            int row = table.rowAtPoint(point);

            return row >= 0 && column >= 0 &&
                   table.getColumnClass(column).equals(Link.class) &&
                   !table.getModel().getValueAt(row, column).toString().equals(CPINFO_LINK_TEXT + "0");
        }

        
    }
    
}

