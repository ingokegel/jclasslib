/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

public class MultiLineHtmlCellHandler extends HtmlDisplayTextArea implements TableCellRenderer, TableCellEditor {

    private List<CellEditorListener> listeners = new ArrayList<CellEditorListener>();
    private JTable table;

    public MultiLineHtmlCellHandler() {
        setOpaque(true);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                select();
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                if (table != null) {
                    // Implement single selection dragging while dragging
                    MouseEvent tableEvent = SwingUtilities.convertMouseEvent(MultiLineHtmlCellHandler.this, e, table);
                    Point point = tableEvent.getPoint();
                    int row = table.rowAtPoint(point);
                    if (row == -1) {
                        int lastRow = table.getRowCount() - 1;
                        Rectangle lastCellRect = table.getCellRect(lastRow, 0, true);
                        if (point.y > lastCellRect.y + lastCellRect.height) {
                            row = lastRow;
                        }
                    }
                    table.getSelectionModel().setSelectionInterval(row, row);
                    if (row == table.getEditingRow()) {
                        switchToSelected(table);
                    } else {
                        switchToUnselected(table);
                    }
                }
            }
        });

    }

    private void select() {
        if (table != null) {
            int row = table.getEditingRow();
            table.getSelectionModel().setSelectionInterval(row, row);
            switchToSelected(table);
        }
    }

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (isSelected) {
            switchToSelected(table);
        } else {
            switchToUnselected(table);
        }
        setFont(table.getFont());
        setBorder(new EmptyBorder(1, 1, 1, 1));
        setText((value == null) ? "" : value.toString());
        return this;
    }

    private void switchToUnselected(JTable table) {
        setForeground(table.getForeground());
        setBackground(table.getBackground());
        setInverted(false);
    }

    private void switchToSelected(JTable table) {
        setForeground(table.getSelectionForeground());
        setBackground(table.getSelectionBackground());
        setInverted(!table.getSelectionForeground().equals(table.getForeground()));
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        this.table = table;
        return getTableCellRendererComponent(this.table, value, isSelected, false, row, column);
    }

    @Override
    public Object getCellEditorValue() {
        return null;
    }

    @Override
    public boolean isCellEditable(EventObject anEvent) {
        return true;
    }

    @Override
    public boolean shouldSelectCell(EventObject eventObject) {
        return false;
    }

    @Override
    public boolean stopCellEditing() {
        ChangeEvent event = new ChangeEvent(this);
        for (CellEditorListener listener : new ArrayList<CellEditorListener>(listeners)) {
            listener.editingStopped(event);
        }
        return true;
    }

    @Override
    public void cancelCellEditing() {
        ChangeEvent event = new ChangeEvent(this);
        for (CellEditorListener listener : new ArrayList<CellEditorListener>(listeners)) {
            listener.editingCanceled(event);
        }
    }

    @Override
    public void addCellEditorListener(CellEditorListener l) {
        listeners.add(l);
    }

    @Override
    public void removeCellEditorListener(CellEditorListener l) {
        listeners.remove(l);
    }
}