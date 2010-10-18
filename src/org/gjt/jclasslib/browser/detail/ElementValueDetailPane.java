/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail;

import org.gjt.jclasslib.browser.*;
import org.gjt.jclasslib.browser.detail.elementvalues.*;
import org.gjt.jclasslib.structures.elementvalues.*;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.HashMap;

/**
 * Container for the classes defined in the <tt>elementvalue</tt> subpackage
 * and switches between the contained panes as required.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 * @version $Revision: 1.1 $ $Date: 2004-12-28 13:04:31 $
 */
public class ElementValueDetailPane extends AbstractDetailPane {

    private static final String SCREEN_UNKNOWN = "Unknown";
    private static final String SCREEN_CONST_VALUE = "Const";
    private static final String SCREEN_CLASS_VALUE = "Class";
    private static final String SCREEN_ENUM_VALUE = "Enum";


    private HashMap elementTypeToDetailPane;

    // Visual components

    private JPanel specificInfoPane;
    private GenericElementValueDetailPane genericInfoPane;

    public ElementValueDetailPane(BrowserServices services) {
        super(services);
    }

    protected void setupComponent() {
        buildGenericInfoPane();
        buildSpecificInfoPane();

        setLayout(new BorderLayout());

        add(genericInfoPane, BorderLayout.NORTH);
        add(specificInfoPane, BorderLayout.CENTER);
    }

    public void show(TreePath treePath) {
        ElementValue eve = (ElementValue)
                ((BrowserTreeNode)treePath.getLastPathComponent()).getElement();

        String paneName = null;
        if (eve instanceof ConstElementValue) {
            paneName = SCREEN_CONST_VALUE;
        } else if (eve instanceof ClassElementValue) {
            paneName = SCREEN_CLASS_VALUE;
        } else if (eve instanceof EnumElementValue) {
            paneName = SCREEN_ENUM_VALUE;
        }

        CardLayout layout = (CardLayout)specificInfoPane.getLayout();
        if (paneName == null) {
            layout.show(specificInfoPane, SCREEN_UNKNOWN);
        } else {
            AbstractDetailPane pane = (AbstractDetailPane)elementTypeToDetailPane.get(paneName);
            pane.show(treePath);
            layout.show(specificInfoPane, paneName);
        }
        genericInfoPane.show(treePath);
    }


    private void buildGenericInfoPane() {
        genericInfoPane = new GenericElementValueDetailPane(services);
        genericInfoPane.setBorder(createTitledBorder("Generic info:"));
    }

    private void buildSpecificInfoPane() {
        specificInfoPane = new JPanel();
        specificInfoPane.setBorder(createTitledBorder("Specific info:"));

        specificInfoPane.setLayout(new CardLayout());
        elementTypeToDetailPane = new HashMap();
        JPanel pane;

        pane = new JPanel();
        specificInfoPane.add(pane, SCREEN_UNKNOWN);

        addScreen(new ConstElementValueEntryDetailPane(services),
                SCREEN_CONST_VALUE);
        addScreen(new ClassElementValueEntryDetailPane(services),
                SCREEN_CLASS_VALUE);
        addScreen(new EnumElementValueEntryDetailPane(services),
                SCREEN_ENUM_VALUE);
    }

    private void addScreen(AbstractDetailPane detailPane, String name) {
        if (detailPane instanceof FixedListDetailPane) {
            specificInfoPane.add(((FixedListDetailPane)detailPane).getScrollPane(), name);
        } else {
            specificInfoPane.add(detailPane, name);
        }
        elementTypeToDetailPane.put(name, detailPane);
    }

    private Border createTitledBorder(String title) {
        Border simpleBorder = BorderFactory.createEtchedBorder();
        Border titledBorder = BorderFactory.createTitledBorder(simpleBorder, title);

        return titledBorder;
    }
}

