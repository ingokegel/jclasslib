/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.browser.detail.*;

import javax.swing.*;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.HashMap;

/**
    The right half of a child window of the class file browser application
    showing detailed information for the specific tree node selected in
    <tt>BrowserTreePane</tt>.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.6 $ $Date: 2003-08-18 08:05:39 $
*/
public class BrowserDetailPane extends JPanel {

    private static final Dimension detailMinimumSize = new Dimension(150,150);
    private static final Dimension detailPreferredSize = new Dimension(150,150);

    private BrowserServices services;
    private HashMap nodeTypeToDetailPane = new HashMap();

    /**
        Constructor.
        @param services the associated browser services.
     */
    public BrowserDetailPane(BrowserServices services) {
        this.services = services;
        setupComponent();
    }

    /**
        Show details for the specific tree node selected in
        <tt>BrowserTreePane</tt>.
        @param nodeType the type of the node as defined in the <tt>NODE_</tt>
                        constants in <tt>BrowserTreeNode</tt>
        @param treePath the tree path of the selected node
     */
    public void showPane(String nodeType, TreePath treePath) {
		if (services.getClassFile() == null) {
			return;
		}
        CardLayout layout = (CardLayout)getLayout();
        AbstractDetailPane detailPane = (AbstractDetailPane)nodeTypeToDetailPane.get(nodeType);
        if (detailPane != null) {
            detailPane.show(treePath);
        }

        layout.show(this, nodeType);
    }

    /**
        Get the <tt>AttributeDetailPane</tt> detail pane associated with the
        node type <tt>BrowserTreeNode.NODE_ATTRIBUTE</tt>. This is necessary for
        hyperlinks within <tt>Code</tt> attributes.
        @return the <tt>AttributeDetailPane</tt>
     */
    public AttributeDetailPane getAttributeDetailPane() {
        return (AttributeDetailPane)nodeTypeToDetailPane.get(BrowserTreeNode.NODE_ATTRIBUTE);
    }

    private void setupComponent() {

        setLayout(new CardLayout());

        add(new JPanel(), BrowserTreeNode.NODE_NO_CONTENT);

        addScreen(new GeneralDetailPane(services),
                  BrowserTreeNode.NODE_GENERAL);
        addScreen(new ConstantPoolDetailPane(services),
                  BrowserTreeNode.NODE_CONSTANT_POOL);
        addScreen(new InterfaceDetailPane(services),
                  BrowserTreeNode.NODE_INTERFACE);
        addScreen(new ClassMemberDetailPane(services, ClassMemberDetailPane.FIELDS),
                  BrowserTreeNode.NODE_FIELD);
        addScreen(new ClassMemberDetailPane(services, ClassMemberDetailPane.METHODS),
                  BrowserTreeNode.NODE_METHOD);
        addScreen(new AttributeDetailPane(services),
                  BrowserTreeNode.NODE_ATTRIBUTE);

        setMinimumSize(detailMinimumSize);
        setPreferredSize(detailPreferredSize);

    }

    private void addScreen(AbstractDetailPane detailPane, String nodeType) {

        if (detailPane instanceof FixedListDetailPane) {
            add(((FixedListDetailPane)detailPane).getScrollPane(), nodeType);
        } else {
            add(detailPane, nodeType);
        }
        nodeTypeToDetailPane.put(nodeType, detailPane);
    }

}
