/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.browser.config.window.*;
import org.gjt.jclasslib.structures.*;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Visual component displaying a class file.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
 * @version $Revision: 1.10 $ $Date: 2006-03-02 19:23:13 $
 */
public class BrowserComponent extends JComponent
        implements TreeSelectionListener {

    private BrowserHistory history;
    private BrowserServices services;

    // Visual Components

    private JSplitPane splitPane;
    private BrowserTreePane treePane;
    private BrowserDetailPane detailPane;

    /**
     * Constructor.
     *
     * @param services the associated browser services
     */
    public BrowserComponent(BrowserServices services) {

        this.services = services;
        setupComponent();
    }

    /**
     * Get the pane containing the tree structure for the shown class file.
     *
     * @return the pane
     */
    public BrowserTreePane getTreePane() {
        return treePane;
    }

    /**
     * Get the pane containing the detail area for the specific tree node selected
     * in the <tt>BrowserTreePane</tt>.
     *
     * @return the pane
     */
    public BrowserDetailPane getDetailPane() {
        return detailPane;
    }

    /**
     * Get the navigation history of this child window.
     *
     * @return the history
     */
    public BrowserHistory getHistory() {
        return history;
    }

    /**
     * Construct a <tt>BrowserPath</tt> object for the curently selected path in the tree.
     *
     * @return the browser path
     */
    public BrowserPath getBrowserPath() {

        TreePath selectionPath = treePane.getTree().getSelectionPath();
        if (selectionPath == null || selectionPath.getPathCount() < 3) {
            return null;
        }

        BrowserTreeNode categoryNode = (BrowserTreeNode)selectionPath.getPathComponent(2);
        String category = categoryNode.getType();
        if (category.equals(BrowserTreeNode.NODE_NO_CONTENT)) {
            return null;
        }

        BrowserPath browserPath = new BrowserPath();
        browserPath.addPathComponent(new CategoryHolder(category));
        int categoryNodeIndex = categoryNode.getIndex();
        if (category.equals(BrowserTreeNode.NODE_CONSTANT_POOL)) {
            --categoryNodeIndex;
        }
        if (category.equals(BrowserTreeNode.NODE_METHOD)) {
            MethodInfo methodInfo = services.getClassFile().getMethods()[categoryNodeIndex];
            addClassMemberPathComponent(methodInfo, browserPath, selectionPath);
        } else if (category.equals(BrowserTreeNode.NODE_FIELD)) {
            FieldInfo fieldInfo = services.getClassFile().getFields()[categoryNodeIndex];
            addClassMemberPathComponent(fieldInfo, browserPath, selectionPath);
        } else {
            browserPath.addPathComponent(new IndexHolder(categoryNodeIndex));
        }

        return browserPath;
    }

    /**
     * Set the currently selected path in the tree by analyzing a <tt>BrowserPath</tt> object.
     * In the case the given path is <code>null</code> nothing happens.
     *
     * @param browserPath the browser path
     */
    public void setBrowserPath(BrowserPath browserPath) {

        if (browserPath == null) {
            return;
        }
        LinkedList pathComponents = browserPath.getPathComponents();
        Iterator it = pathComponents.iterator();
        if (!it.hasNext()) {
            return;
        }
        CategoryHolder categoryComponent = (CategoryHolder)it.next();
        String category = categoryComponent.getCategory();
        TreePath path = treePane.getPathForCategory(category);
        if (path == null) {
            return;
        }
        while (it.hasNext()) {
            PathComponent pathComponent = (PathComponent)it.next();
            int childIndex;
            if (pathComponent instanceof ReferenceHolder) {
                ReferenceHolder referenceHolder = (ReferenceHolder)pathComponent;
                try {
                    if (category.equals(BrowserTreeNode.NODE_METHOD)) {
                        childIndex = services.getClassFile().getMethodIndex(referenceHolder.getName(), referenceHolder.getType());
                    } else if (category.equals(BrowserTreeNode.NODE_FIELD)) {
                        childIndex = services.getClassFile().getFieldIndex(referenceHolder.getName(), referenceHolder.getType());
                    } else {
                        break;
                    }
                } catch (InvalidByteCodeException ex) {
                    break;
                }
            } else if (pathComponent instanceof IndexHolder) {
                childIndex = ((IndexHolder)pathComponent).getIndex();
            } else {
                break;
            }
            BrowserTreeNode lastNode = (BrowserTreeNode)path.getLastPathComponent();
            if (childIndex >= lastNode.getChildCount()) {
                break;
            }
            path = path.pathByAddingChild(lastNode.getChildAt(childIndex));
        }

        JTree tree = treePane.getTree();
        tree.expandPath(path);
        tree.setSelectionPath(path);
        Object[] pathObjects = path.getPath();
        if (pathObjects.length > 2) {
            TreePath categoryPath = new TreePath(new Object[]{pathObjects[0], pathObjects[1], pathObjects[2]});
            tree.scrollPathToVisible(categoryPath);
        }

    }

    /**
     * Rebuild tree view, clear history and try to set the same path in the browser as before.
     */
    public void rebuild() {


        BrowserPath browserPath = getBrowserPath();
        reset();
        if (browserPath != null) {
            setBrowserPath(browserPath);
        }
    }

    /**
     * Rebuild tree view and clear history.
     */
    public void reset() {

        JTree tree = treePane.getTree();
        tree.removeTreeSelectionListener(this);
        treePane.rebuild();
        history.clear();
        tree.addTreeSelectionListener(this);
        checkSelection();
    }


    /**
     * Check whether anything is selected. If not select the first node.
     */
    public void checkSelection() {

        JTree tree = treePane.getTree();
        if (services.getClassFile() == null) {
            ((CardLayout)detailPane.getLayout()).show(detailPane, BrowserTreeNode.NODE_NO_CONTENT);
        } else {
            if (tree.getSelectionPath() == null) {
                BrowserTreeNode rootNode = (BrowserTreeNode)tree.getModel().getRoot();
                tree.setSelectionPath(new TreePath(new Object[]{rootNode, rootNode.getFirstChild()}));
            }
        }
    }

    public void valueChanged(TreeSelectionEvent selectionEvent) {

        services.activate();

        TreePath selectedPath = selectionEvent.getPath();

        history.updateHistory(selectedPath);
        showDetailPaneForPath(selectedPath);

    }

    private void addClassMemberPathComponent(ClassMember classMember, BrowserPath browserPath, TreePath selectionPath) {

        try {
            browserPath.addPathComponent(new ReferenceHolder(classMember.getName(), classMember.getDescriptor()));
            if (selectionPath.getPathCount() > 3) {
                for (int i = 3; i < selectionPath.getPathCount(); i++) {
                    BrowserTreeNode attributeNode = (BrowserTreeNode)selectionPath.getPathComponent(i);
                    browserPath.addPathComponent(new IndexHolder(attributeNode.getIndex()));
                }
            }
        } catch (InvalidByteCodeException ex) {
        }
    }

    private void showDetailPaneForPath(TreePath path) {

        BrowserTreeNode node = (BrowserTreeNode)path.getLastPathComponent();
        String nodeType = node.getType();
        detailPane.showPane(nodeType, path);
    }


    private void setupComponent() {

        setLayout(new BorderLayout());

        detailPane = new BrowserDetailPane(services);

        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildTreePane(),
                detailPane);

        add(splitPane, BorderLayout.CENTER);

    }

    private BrowserTreePane buildTreePane() {

        treePane = new BrowserTreePane(services);

        JTree tree = treePane.getTree();
        tree.addTreeSelectionListener(this);
        history = new BrowserHistory(services);

        return treePane;
    }

}
