/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.browser.detail.*;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.util.*;

/**
    The right half of a child window of the class file browser application
    showing detailed information for the specific tree node selected in
    <tt>BrowserTreePane</tt>.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.4 $ $Date: 2002-05-30 17:56:27 $
*/
public class BrowserDetailPane extends JPanel {

    private static final Dimension detailMinimumSize = new Dimension(150,150);
    private static final Dimension detailPreferredSize = new Dimension(150,150);

    private BrowserServices services;
    private HashMap nodeTypeToDetailPane = new HashMap();

    public BrowserDetailPane(BrowserServices services) {
        this.services = services;
        setupComponent();
    }

    /**
        Show details for the specific tree node selected in
        <tt>BrowserTreePane</tt>.
        @param nodeType the type of the node as defined in the <tt>NODE_</tt>
                        constants in <tt>BrowserMutableTreeNode</tt>
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
        Setup the internal state of the component at the beginning of its life cycle.
     */
    public void setupComponent() {

        setLayout(new CardLayout());

        add(new JPanel(), BrowserMutableTreeNode.NODE_NO_CONTENT);

        addScreen(new GeneralDetailPane(services),
                  BrowserMutableTreeNode.NODE_GENERAL);
        addScreen(new ConstantPoolDetailPane(services),
                  BrowserMutableTreeNode.NODE_CONSTANT_POOL);
        addScreen(new InterfaceDetailPane(services),
                  BrowserMutableTreeNode.NODE_INTERFACE);
        addScreen(new ClassMemberDetailPane(services, ClassMemberDetailPane.FIELDS),
                  BrowserMutableTreeNode.NODE_FIELD);
        addScreen(new ClassMemberDetailPane(services, ClassMemberDetailPane.METHODS),
                  BrowserMutableTreeNode.NODE_METHOD);
        addScreen(new AttributeDetailPane(services),
                  BrowserMutableTreeNode.NODE_ATTRIBUTE);

        setMinimumSize(detailMinimumSize);
        setPreferredSize(detailPreferredSize);

    }

    /**
        Get the <tt>AttributeDetailPane</tt> detail pane associated with the
        node type <tt>BrowserMutableTreeNode.NODE_ATTRIBUTE</tt>. This is necessary for
        hyperlinks within <tt>Code</tt> attributes.
        @return the <tt>AttributeDetailPane</tt>
     */
    public AttributeDetailPane getAttributeDetailPane() {
        return (AttributeDetailPane)nodeTypeToDetailPane.get(BrowserMutableTreeNode.NODE_ATTRIBUTE);
    }

    private void addScreen(JPanel panel, String nodeType) {
        add(panel, nodeType);
        nodeTypeToDetailPane.put(nodeType, panel);
    }

}
