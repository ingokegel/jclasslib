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
 *
 */
public class BrowserDetailPane extends JPanel {

    private static final Dimension detailMinimumSize = new Dimension(150, 150);
    private static final Dimension detailPreferredSize = new Dimension(150, 150);

    private BrowserServices services;
    private HashMap<NodeType, AbstractDetailPane> nodeTypeToDetailPane = new HashMap<NodeType, AbstractDetailPane>();
    private AbstractDetailPane currentDetailPane;

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
    public void showPane(NodeType nodeType, TreePath treePath) {
        if (services.getClassFile() == null) {
            return;
        }
        CardLayout layout = (CardLayout)getLayout();
        currentDetailPane = getDetailPane(nodeType);
        if (currentDetailPane != null) {
            currentDetailPane.show(treePath);
        }

        layout.show(this, nodeType.name());
    }

    public AbstractDetailPane getCurrentDetailPane() {
        return currentDetailPane;
    }

    /**
     * Get the <tt>AttributeDetailPane</tt> detail pane associated with the
     * node type <tt>BrowserTreeNode.NODE_ATTRIBUTE</tt>. This is necessary for
     * hyperlinks within <tt>Code</tt> attributes.
     *
     * @return the <tt>AttributeDetailPane</tt>
     */
    public AttributeDetailPane getAttributeDetailPane() {
        return (AttributeDetailPane)getDetailPane(NodeType.ATTRIBUTE);
    }

    private AbstractDetailPane getDetailPane(NodeType nodeType) {
        AbstractDetailPane detailPane = nodeTypeToDetailPane.get(nodeType);
        if (detailPane == null) {
            detailPane = createDetailPanel(nodeType);
            if (detailPane != null) {
                if (detailPane instanceof FixedListDetailPane) {
                    add(((FixedListDetailPane)detailPane).getScrollPane(), nodeType.name());
                } else {
                    add(detailPane, nodeType.name());
                }
                nodeTypeToDetailPane.put(nodeType, detailPane);
            }
        }
        return detailPane;
    }

    private AbstractDetailPane createDetailPanel(NodeType nodeType) {
        if (nodeType.equals(NodeType.GENERAL)) {
            return new GeneralDetailPane(services);
        } else if (nodeType.equals(NodeType.CONSTANT_POOL)) {
            return new ConstantPoolDetailPane(services);
        } else if (nodeType.equals(NodeType.INTERFACE)) {
            return new InterfaceDetailPane(services);
        } else if (nodeType.equals(NodeType.FIELDS)) {
            return new ClassMemberContainerDetailPane(services, FixedListWithSignatureDetailPane.SignatureMode.FIELD);
        } else if (nodeType.equals(NodeType.METHODS)) {
            return new ClassMemberContainerDetailPane(services, FixedListWithSignatureDetailPane.SignatureMode.METHOD);
        } else if (nodeType.equals(NodeType.FIELD)) {
            return new ClassMemberDetailPane(services, FixedListWithSignatureDetailPane.SignatureMode.FIELD);
        } else if (nodeType.equals(NodeType.METHOD)) {
            return new ClassMemberDetailPane(services, FixedListWithSignatureDetailPane.SignatureMode.METHOD);
        } else if (nodeType.equals(NodeType.ATTRIBUTE)) {
            return new AttributeDetailPane(services);
        } else if (nodeType.equals(NodeType.ANNOTATION)) {
            return new AnnotationDetailPane(services);
        } else if (nodeType.equals(NodeType.TYPE_ANNOTATION)) {
            return new TypeAnnotationDetailPane(services);
        } else if (nodeType.equals(NodeType.ELEMENTVALUE)) {
            return new ElementValueDetailPane(services);
        } else if (nodeType.equals(NodeType.ELEMENTVALUEPAIR)) {
            return new ElementValuePairDetailPane(services);
        } else if (nodeType.equals(NodeType.ARRAYELEMENTVALUE)) {
            return new ArrayElementValueDetailPane(services);
        } else {
            return null;
        }
    }

    private void setupComponent() {

        setLayout(new CardLayout());

        add(new JPanel(), NodeType.NO_CONTENT.name());

        setMinimumSize(detailMinimumSize);
        setPreferredSize(detailPreferredSize);

    }

}
