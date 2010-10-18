/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;
import java.awt.*;


/**
    A <tt>TableCellRenderer</tt> which is based on an <tt>ExtendedJLabel</tt> rather than
    a <tt>JLabel</tt> like the <tt>javax.swing.table.DefaultTableCellRenderer</tt>.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.4 $ $Date: 2003-08-18 07:48:28 $
*/
public class ExtendedTableCellRenderer extends ExtendedJLabel
                                       implements TableCellRenderer {

    private static final Border NO_FOCUS_BORDER = new EmptyBorder(1, 1, 1, 1);

    private Color unselectedForeground;
    private Color unselectedBackground;

    /**
     * Constructor.
     */
    public ExtendedTableCellRenderer() {
        super();
        setOpaque(true);
        setBorder(NO_FOCUS_BORDER);
    }

    public void setForeground(Color c) {
        super.setForeground(c); 
        unselectedForeground = c; 
    }
    
    public void setBackground(Color c) {
        super.setBackground(c); 
        unselectedBackground = c; 
    }

    public void updateUI() {
        super.updateUI(); 
        setForeground(null);
        setBackground(null);
    }
    
    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column)
    {
        if (isSelected) {
           super.setForeground(table.getSelectionForeground());
           super.setBackground(table.getSelectionBackground());

        } else {
            super.setForeground((unselectedForeground != null) ? unselectedForeground : table.getForeground());
            super.setBackground((unselectedBackground != null) ? unselectedBackground : table.getBackground());
        }

        setFont(table.getFont());

        if (hasFocus) {
            setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
            if (table.isCellEditable(row, column)) {
                super.setForeground(UIManager.getColor("Table.focusCellForeground"));
                super.setBackground(UIManager.getColor("Table.focusCellBackground"));
            }
        } else {
            setBorder(NO_FOCUS_BORDER);
        }

        setValue(value); 

        Color background = getBackground();
        boolean colorMatch = (background != null) && (background.equals(table.getBackground())) && table.isOpaque();
        setOpaque(!colorMatch);

        return this;
    }
    

    public void validate() {}

    public void revalidate() {}

    public void repaint(long tm, int x, int y, int width, int height) {}

    public void repaint(Rectangle r) { }

    protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
        if (propertyName == "text") {
            super.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) { }


    private void setValue(Object value) {
        setText((value == null) ? "" : value.toString());
    }
    

}


