/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.*;
import org.gjt.jclasslib.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;

/**
    Base class for all detail panes with a structure of
    a variable number of row entries with the same number of columns.
    
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.3 $ $Date: 2002-02-27 16:47:42 $
*/
public abstract class ListDetailPane extends AbstractDetailPane {

    private TableLinkListener linkListener;
    
    // Visual components

    private JTable table;
    
    public ListDetailPane(BrowserServices services) {
        super(services);
    }
    
    protected void setupComponent() {

        setLayout(new BorderLayout());
        
        table = new JTable();
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        
        linkListener = new TableLinkListener();

        JScrollPane scrollPane = new JScrollPane(table);

        add(scrollPane, BorderLayout.CENTER);
    }

    public void show(TreePath treePath) {
        TableModel tableModel = getTableModel(treePath);
        table.setModel(tableModel);

        ExtendedTableCellRenderer linkRenderer = new ExtendedTableCellRenderer();
        linkRenderer.setForeground(COLOR_LINK);
        linkRenderer.setUnderlined(true);

        createTableColumnModel(table, tableModel);
        table.setDefaultRenderer(Link.class, linkRenderer);

    }

    /**
        Create the table column model for the given table and table column model.
        @param table the table
        @return tableModel the model
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
        
        public TableLinkListener() {

            table.addMouseListener(this);
            table.addMouseMotionListener(this);
            
            defaultCursor = Cursor.getDefaultCursor();
            defaultCursorType = defaultCursor.getType();
            handCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
        }
        
        public void mouseClicked(MouseEvent event) {
            
            Point point = event.getPoint();
            int column = table.columnAtPoint(point);
            
            if (isLink(column)) {
                int row = table.rowAtPoint(point);
                link(row, column);
            }
        }

        public void mouseDragged(MouseEvent event) {
        }
        
        public void mouseMoved(MouseEvent event) {
            
            int column = table.columnAtPoint(event.getPoint());

            if (table.getCursor().getType() == defaultCursorType && isLink(column)) {
                table.setCursor(handCursor);
            } else if (!isLink(column)) {
                table.setCursor(defaultCursor);
            }
        }
        
        private boolean isLink(int column) {
            return column >= 0 && table.getColumnClass(column).equals(Link.class);
        }

        
    }
    
}

