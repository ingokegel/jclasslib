/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes;

import org.gjt.jclasslib.browser.AbstractDetailPane;
import org.gjt.jclasslib.browser.detail.ListDetailPane;
import org.gjt.jclasslib.util.ExtendedTableCellRenderer;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
    Renderer for links in <tt>ListDetailPane</tt>s.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 08:17:02 $
*/
public class LinkRenderer implements TableCellRenderer  {

    private ExtendedTableCellRenderer linkLineRenderer;
    private ExtendedTableCellRenderer infoLineRenderer;
    private Color standardForeground;
    private JPanel panel;

    /**
     * Constructor.
     */
    public LinkRenderer() {

        linkLineRenderer = new ExtendedTableCellRenderer();
        linkLineRenderer.setVerticalAlignment(JLabel.TOP);
        infoLineRenderer = new ExtendedTableCellRenderer();

        standardForeground = linkLineRenderer.getForeground();

        panel = new JPanel(new BorderLayout());
        panel.add(linkLineRenderer, BorderLayout.NORTH);
        panel.add(infoLineRenderer, BorderLayout.SOUTH);
    }

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column)
    {

        boolean standardLabel = value.toString().equals(ListDetailPane.CPINFO_LINK_TEXT + "0");
        linkLineRenderer.setForeground(standardLabel ? standardForeground : AbstractDetailPane.COLOR_LINK);
        linkLineRenderer.setUnderlined(!standardLabel);
        linkLineRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof LinkCommentValue) {
            infoLineRenderer.getTableCellRendererComponent(table, ((LinkCommentValue)value).commentValue, isSelected, false, row, column);
            panel.setBorder(linkLineRenderer.getBorder());
            linkLineRenderer.setBorder(infoLineRenderer.getBorder());
            infoLineRenderer.setVisible(true);
        } else {
            infoLineRenderer.setVisible(false);
        }

        panel.setBackground(linkLineRenderer.getBackground());

        return panel;
    }

    /**
     * Value object for a link with a comment.
     */
    public static class LinkCommentValue {

        private String linkValue;
        private String commentValue;

        /**
         * Constructor.
         * @param linkValue the text for the link.
         * @param commentValue the text for the comment.
         */
        public LinkCommentValue(String linkValue, String commentValue) {
            this.linkValue = linkValue;
            this.commentValue = commentValue;
        }

        public String toString() {
            return linkValue;
        }
    }

}
