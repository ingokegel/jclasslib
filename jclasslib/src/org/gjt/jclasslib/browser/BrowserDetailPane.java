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
 * The right half of a child window of the class file browser application
 * showing detailed information for the specific tree node selected in
 * <tt>BrowserTreePane</tt>.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>, <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 * @version $Revision: 1.9 $ $Date: 2007-10-31 12:04:15 $
 */
public class BrowserDetailPane extends JPanel {

    private static final Dimension detailMinimumSize = new Dimension(150, 150);
    private static final Dimension detailPreferredSize = new Dimension(150, 150);

    private BrowserServices services;
    private HashMap nodeTypeToDetailPane = new HashMap();

    /**
     * Constructor.
     *
     * @param services the associated browser services.
     */
    public BrowserDetailPane(BrowserServices services) {
        this.services = services;
        setupComponent();
    }

    /**
     * Show details for the specific tree node selected in
     * <tt>BrowserTreePane</tt>.
     *
     * @param nodeType the type of the node as defined in the <tt>NODE_</tt>
     *                 constants in <tt>BrowserTreeNode</tt>
     * @param treePath the tree path of the selected node
     */
    public void showPane(String nodeType, TreePath treePath) {
        if (services.getClassFile() == null) {
            return;
        }
        CardLayout layout = (CardLayout)getLayout();
        AbstractDetailPane detailPane = getDetailPane(nodeType);
        if (detailPane != null) {
            detailPane.show(treePath);
        }

        layout.show(this, nodeType);
    }

    /**
     * Get the <tt>AttributeDetailPane</tt> detail pane associated with the
     * node type <tt>BrowserTreeNode.NODE_ATTRIBUTE</tt>. This is necessary for
     * hyperlinks within <tt>Code</tt> attributes.
     *
     * @return the <tt>AttributeDetailPane</tt>
     */
    public AttributeDetailPane getAttributeDetailPane() {
        return (AttributeDetailPane)getDetailPane(BrowserTreeNode.NODE_ATTRIBUTE);
    }

    private AbstractDetailPane getDetailPane(String nodeType) {
        AbstractDetailPane detailPane = (AbstractDetailPane)nodeTypeToDetailPane.get(nodeType);
        if (detailPane == null) {
            detailPane = createDetailPanel(nodeType);
            if (detailPane != null) {
                if (detailPane instanceof FixedListDetailPane) {
                    add(((FixedListDetailPane)detailPane).getScrollPane(), nodeType);
                } else {
                    add(detailPane, nodeType);
                }
                nodeTypeToDetailPane.put(nodeType, detailPane);
            }
        }
        return detailPane;
    }

    private AbstractDetailPane createDetailPanel(String nodeType) {
        if (nodeType.equals(BrowserTreeNode.NODE_GENERAL)) {
            return new GeneralDetailPane(services);
        } else if (nodeType.equals(BrowserTreeNode.NODE_CONSTANT_POOL)) {
            return new ConstantPoolDetailPane(services);
        } else if (nodeType.equals(BrowserTreeNode.NODE_INTERFACE)) {
            return new InterfaceDetailPane(services);
        } else if (nodeType.equals(BrowserTreeNode.NODE_FIELDS)) {
            return new ClassMemberContainerDetailPane(services, ClassMemberContainerDetailPane.FIELDS);
        } else if (nodeType.equals(BrowserTreeNode.NODE_METHODS)) {
            return new ClassMemberContainerDetailPane(services, ClassMemberContainerDetailPane.METHODS);
        } else if (nodeType.equals(BrowserTreeNode.NODE_FIELD)) {
            return new ClassMemberDetailPane(services, ClassMemberDetailPane.FIELDS);
        } else if (nodeType.equals(BrowserTreeNode.NODE_METHOD)) {
            return new ClassMemberDetailPane(services, ClassMemberDetailPane.METHODS);
        } else if (nodeType.equals(BrowserTreeNode.NODE_ATTRIBUTE)) {
            return new AttributeDetailPane(services);
        } else if (nodeType.equals(BrowserTreeNode.NODE_ANNOTATION)) {
            return new AnnotationDetailPane(services);
        } else if (nodeType.equals(BrowserTreeNode.NODE_ELEMENTVALUE)) {
            return new ElementValueDetailPane(services);
        } else if (nodeType.equals(BrowserTreeNode.NODE_ELEMENTVALUEPAIR)) {
            return new ElementValuePairDetailPane(services);
        } else if (nodeType.equals(BrowserTreeNode.NODE_ARRAYELEMENTVALUE)) {
            return new ArrayElementValueDetailPane(services);
        } else {
            return null;
        }
    }

    private void setupComponent() {

        setLayout(new CardLayout());

        add(new JPanel(), BrowserTreeNode.NODE_NO_CONTENT);

        setMinimumSize(detailMinimumSize);
        setPreferredSize(detailPreferredSize);

    }

}
