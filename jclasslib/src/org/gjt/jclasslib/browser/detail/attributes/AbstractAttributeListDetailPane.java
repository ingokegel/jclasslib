/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes;

import org.gjt.jclasslib.browser.*;
import org.gjt.jclasslib.browser.detail.*;
import org.gjt.jclasslib.structures.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import java.util.*;

/**
    Base class for all detail panes showing specific information for
    a specific attribute tree node selected in <tt>BrowserTreePane</tt> 
    which can be displayed as a list of row entries with the same number of
    columns.
    
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:25 $
*/
public abstract class AbstractAttributeListDetailPane extends ListDetailPane {

    /** Default width in pixels for a column displaying a number */
    protected static final int NUMBER_COLUMN_WIDTH = 60;
    /** Default width in pixels for a column displaying a hyperlink */
    protected static final int LINK_COLUMN_WIDTH = 80;
    /** Default width in pixels for a column displaying a verbose entry */
    protected static final int VERBOSE_COLUMN_WIDTH = 250;
    /** Default width in pixels for a column displaying a short verbose entry */
    protected static final int SHORT_VERBOSE_COLUMN_WIDTH = 125;
    
    private static final int COLUMN_MIN_WIDTH = 20;
    private static final int ROW_NUMBER_COLUMN_WIDTH = 35;

    private static WeakHashMap attributeToTableModel = new WeakHashMap();

    /** table model for this detail pane */
    protected AbstractAttributeTableModel tableModel;
    
    public AbstractAttributeListDetailPane(BrowserInternalFrame parentFrame) {
        super(parentFrame);
    }
    
    protected TableModel getTableModel(TreePath treePath) {
        AttributeInfo attribute = findAttribute(treePath);
        
        tableModel = getCachedTableModel(attribute);
        return tableModel;
    }

    protected void link(int row, int column) {
        tableModel.link(row, column);
    }

    /**
        Create the table model for a specific attribute. This method is called
        by <tt>getTableModel()</tt>.
        @param attribute the attribute
        @return the table model
     */
    protected abstract AbstractAttributeTableModel createTableModel(AttributeInfo attribute);
    
    /**
        Get the width in pixels for a specific column.
        @param column the index of the column in the model
        @return the width
     */
    protected int getColumnWidth(int column) {
        return tableModel.getColumnWidth(column);
    }

    protected void createTableColumnModel(JTable table, TableModel tableModel) {
        AbstractAttributeTableModel attributeTableModel =
                (AbstractAttributeTableModel)tableModel;
        
        TableColumnModel tableColumnModel = attributeTableModel.getTableColumnModel();
        if (tableColumnModel == null) {
            table.createDefaultColumnsFromModel();
            tableColumnModel = table.getColumnModel();
            attributeTableModel.setTableColumnModel(tableColumnModel);

        } else {
            table.setColumnModel(tableColumnModel);
        }
        adjustColumns(table, tableColumnModel);
    }
    
    
    private void adjustColumns(JTable table, TableColumnModel tableColumnModel) {
        
        TableColumn tableColumn;
        for (int i = 0; i < tableColumnModel.getColumnCount(); i++) {
            tableColumn = tableColumnModel.getColumn(i);
            tableColumn.setMinWidth(COLUMN_MIN_WIDTH);
            
            int width = (i == 0) ? ROW_NUMBER_COLUMN_WIDTH : getColumnWidth(i);
            
            tableColumn.setWidth(width);
            tableColumn.setPreferredWidth(width);
            
        }

    }
    
    private AbstractAttributeTableModel getCachedTableModel(AttributeInfo attribute) {
        
        AbstractAttributeTableModel tableModel = 
            (AbstractAttributeTableModel)attributeToTableModel.get(attribute);

        if (tableModel == null) {
            tableModel = createTableModel(attribute);
                                
            attributeToTableModel.put(attribute, tableModel);
            
        }
        
        return tableModel;
    }

}

