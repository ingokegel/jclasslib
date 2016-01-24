/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes;

import org.gjt.jclasslib.browser.detail.ListDetailPane;
import org.gjt.jclasslib.util.ExtendedTableCellRenderer;
import org.gjt.jclasslib.util.HtmlDisplayTextArea;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

/**
    Renderer for links in <tt>ListDetailPane</tt>s.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
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
        infoLineRenderer = new ExtendedTableCellRenderer();

        standardForeground = linkLineRenderer.getForeground();

        panel = new JPanel(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.gridx = 0;
        gc.gridy = GridBagConstraints.RELATIVE;
        gc.insets = new Insets(0, 0, 0, 0);

        panel.add(linkLineRenderer, gc);
        panel.add(infoLineRenderer, gc);
        gc.weighty = 1;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.BOTH;
        JPanel dummyPanel = new JPanel();
        dummyPanel.setOpaque(false);
        panel.add(dummyPanel, gc);

    }

    public Component getTableCellRendererComponent(JTable table,
                                                   Object value,
                                                   boolean isSelected,
                                                   boolean hasFocus,
                                                   int row,
                                                   int column)
    {

        boolean standardLabel = value == null || value.toString().equals(ListDetailPane.CPINFO_LINK_TEXT + "0");
        linkLineRenderer.setForeground(standardLabel ? standardForeground : HtmlDisplayTextArea.COLOR_LINK);
        linkLineRenderer.setUnderlined(!standardLabel);
        linkLineRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

        if (value instanceof LinkCommentValue) {
            infoLineRenderer.getTableCellRendererComponent(table, ((LinkCommentValue)value).commentValue, isSelected, false, row, column);
            infoLineRenderer.setVisible(true);
        } else {
            infoLineRenderer.setVisible(false);
        }

        panel.setBackground(linkLineRenderer.getBackground());
        panel.setBorder(linkLineRenderer.getBorder());
        linkLineRenderer.setBorder(null);

        return panel;
    }

    public boolean isLinkLabelHit(Point point) {
        return linkLineRenderer.getBounds().contains(point);
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
