/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes;

import org.gjt.jclasslib.browser.detail.ListDetailPane.ColumnCache;
import org.gjt.jclasslib.structures.AttributeInfo;

import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

/**
 * Base class for all table models for attributes displayed by a
 * <tt>AbstractAttributeListDetailPane</tt>.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
 * @version $Revision: 1.5 $ $Date: 2004-12-28 13:04:30 $
 */
public abstract class AbstractAttributeTableModel extends AbstractTableModel {

    /**
     * Number of default columns.
     */
    protected static final int BASE_COLUMN_COUNT = 1;

    /**
     * The associated attribute.
     */
    protected AttributeInfo attribute;

    private ColumnCache columnCache;
    private TableColumnModel tableColumnModel;

    /**
     * Constructor.
     *
     * @param attribute the associated attribute.
     */
    protected AbstractAttributeTableModel(AttributeInfo attribute) {
        this.attribute = attribute;
    }

    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

    public String getColumnName(int column) {
        if (column == 0) {
            return "Nr.";
        } else {
            return doGetColumnName(column);
        }
    }

    public Class getColumnClass(int column) {
        if (column == 0) {
            return Number.class;
        } else {
            return doGetColumnClass(column);
        }
    }

    public Object getValueAt(int row, int column) {

        if (column == 0) {
            return String.valueOf(row);
        } else {
            if (columnCache == null) {
                columnCache = new ColumnCache(getRowCount(), getColumnCount() - 1);
            }
            Object value = columnCache.getValueAt(row, column - 1);
            if (value == null) {
                value = doGetValueAt(row, column);
                columnCache.setValueAt(row, column - 1, value);
            }

            return value;
        }
    }

    /**
     * Get the associated table column model.
     *
     * @return the model
     */
    public TableColumnModel getTableColumnModel() {
        return tableColumnModel;
    }

    /**
     * Set the associated table column model.
     *
     * @param tableColumnModel the model
     */
    public void setTableColumnModel(TableColumnModel tableColumnModel) {
        this.tableColumnModel = tableColumnModel;
    }

    /**
     * Get the width of a specified column in pixels.
     *
     * @param column the index ofthe column in the table model
     * @return the width
     */
    public abstract int getColumnWidth(int column);

    /**
     * Attribute specific <tt>getValueAt()</tt>.
     *
     * @param row    the row number
     * @param column the column number
     * @return the value
     */
    protected abstract Object doGetValueAt(int row, int column);

    /**
     * Attribute specific <tt>getColumnName()</tt>.
     *
     * @param column the column number
     * @return the name
     */
    protected abstract String doGetColumnName(int column);

    /**
     * Attribute specific <tt>getColumnClass()</tt>.
     *
     * @param column the column number
     * @return the class
     */
    protected abstract Class doGetColumnClass(int column);


    /**
     * Link to the destination described by the target of the hyperlink
     * contained in a specific cell.
     *
     * @param row    the row number of the hyperlink
     * @param column the column number of the hyperlink
     */
    public void link(int row, int column) {
    }

}
