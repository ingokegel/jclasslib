/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath;

import org.gjt.jclasslib.browser.BrowserMDIFrame;
import org.gjt.jclasslib.util.GUIHelper;
import org.gjt.jclasslib.util.ProgressDialog;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;

/**
    Classpath browser that shows a tree of the contents of a
    <tt>ClasspathComponent</tt>.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 08:10:15 $
*/
public class ClasspathBrowser extends JDialog
                              implements ActionListener, ClasspathChangeListener
{

    private static final int DIALOG_WIDTH = 450;
    private static final int DIALOG_HEIGHT = 450;

    private BrowserMDIFrame frame;
    private ClasspathComponent classpathComponent;

    private JLabel lblTitle;
    private JTree tree;
    private JScrollPane scpTree;
    private JButton btnSetup;
    private JButton btnSync;
    private JButton btnOk;
    private JButton btnCancel;

    private ProgressDialog progressDialog;
    private boolean resetOnNextMerge;
    private boolean needsMerge;

    private String selectedClassName;

    /**
     * Constructor.
     * @param frame the parent frame.
     * @param classpathComponent the classpath component to display initially.
     * @param title the disalog title.
     * @param setupVisible if the <i>setup classpath</i> button should be visible.
     */
    public ClasspathBrowser(BrowserMDIFrame frame, ClasspathComponent classpathComponent, String title, boolean setupVisible) {
        super(frame);
        this.frame = frame;

        setClasspathComponent(classpathComponent);
        setupControls(title, setupVisible);
        setupComponent();
        setupEventHandlers();
    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == btnCancel) {
            doCancel();
        } else if (source == btnOk) {
            doOk();
        } else if (source == btnSetup) {
            doSetup();
        } else if (source == btnSync) {
            doSync(true);
        }

    }

    public void classpathChanged(ClasspathChangeEvent event) {
        needsMerge = true;
        if (event.isRemoval()) {
            resetOnNextMerge = true;
        }
    }

    public void setVisible(boolean visible) {
        if (visible) {
            selectedClassName = null;
        }
        super.setVisible(visible);
    }

    /**
     * Get the name of the selected class.
     * @return the name
     */
    public String getSelectedClassName() {
        return selectedClassName;
    }

    /**
     * Set the new classpath component to be displayed by this dialog.
     * The previous content will be cleared.
     * @param classpathComponent the new classpath component.
     */
    public void setClasspathComponent(ClasspathComponent classpathComponent) {
        if (this.classpathComponent != null) {
            this.classpathComponent.removeClasspathChangeListener(this);
        }
        this.classpathComponent = classpathComponent;
        if (classpathComponent != null) {
            classpathComponent.addClasspathChangeListener(this);
        }
        resetOnNextMerge = true;
        needsMerge = true;
        clear();
    }

    /**
     * Clear the current contents of the dialog. The tree will not be synchronized
     * automatically on the next <tt>setVisible</tt>.
     */
    public void clear() {
        if (tree != null) {
            tree.setModel(new DefaultTreeModel(new ClassTreeNode()));
        }
    }

    private void setupControls(String title, boolean setupVisible) {

        lblTitle = new JLabel(title);
        tree = new JTree(new ClassTreeNode());
        tree.setRootVisible(false);
        tree.setShowsRootHandles(true);
        tree.putClientProperty("JTree.lineStyle", "Angled");
        scpTree = new JScrollPane(tree);

        btnSetup = new JButton("Setup classpath");
        btnSetup.setVisible(setupVisible);
        btnSync = new JButton("Synchronize");
        btnOk = new JButton("Ok");
        btnOk.setEnabled(false);
        btnCancel = new JButton("Cancel");
        btnOk.setPreferredSize(btnCancel.getPreferredSize());

        progressDialog = new ProgressDialog(this, null, "Scanning classpath ...");

    }

    private void setupComponent() {

        Container contentPane = getContentPane();
        contentPane.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(5, 5, 0, 5);
        gc.weightx = 1;
        gc.anchor = GridBagConstraints.NORTHWEST;
        contentPane.add(lblTitle, gc);
        gc.gridy++;

        gc.weighty = 1;
        gc.insets.top = 0;
        gc.fill = GridBagConstraints.BOTH;
        contentPane.add(scpTree, gc);
        gc.gridy++;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weighty = 0;
        gc.insets.top = 3;
        gc.insets.bottom = 5;
        contentPane.add(createButtonBox(), gc);
        getRootPane().setDefaultButton(btnOk);

        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setModal(true);
        setTitle("Choose a class");
        GUIHelper.centerOnParentWindow(this, getOwner());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    }

    private Box createButtonBox() {

        Box box = Box.createHorizontalBox();
        box.add(btnSetup);
        box.add(btnSync);
        box.add(Box.createHorizontalGlue());
        box.add(btnOk);
        box.add(btnCancel);

        return box;
    }

    private void setupEventHandlers() {

        btnCancel.addActionListener(this);
        btnOk.addActionListener(this);
        btnSetup.addActionListener(this);
        btnSync.addActionListener(this);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                doCancel();
            }
        });
        KeyStroke keyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        Object key = new Object();

        JComponent contentPane = (JComponent)getContentPane();
        contentPane.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, key);
        contentPane.getActionMap().put(key, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                doCancel();
            }
        });

        addComponentListener(new ComponentAdapter() {
            public void componentShown(ComponentEvent event) {
                conditionalUpdate();
            }
        });

        tree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent event) {
                checkTreeSelection();
            }
        });

        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2 && isValidDoubleClickPath(event)) {
                    doOk();
                }
            }
        });

    }

    private void conditionalUpdate() {
        if (needsMerge) {
            doSync(resetOnNextMerge);
        }
    }

    private boolean isValidDoubleClickPath(MouseEvent event) {

        TreePath locationPath = tree.getPathForLocation(event.getX(), event.getY());
        TreePath selectionPath = tree.getSelectionPath();
        if (selectionPath == null || locationPath == null || !selectionPath.equals(locationPath)) {
            return false;
        }
        ClassTreeNode lastPathComponent = (ClassTreeNode)selectionPath.getLastPathComponent();
        return !lastPathComponent.isPackageNode();
    }

    private void checkTreeSelection() {

        TreePath selectionPath = tree.getSelectionPath();
        boolean enabled = false;
        if (selectionPath != null) {
            ClassTreeNode classTreeNode = (ClassTreeNode)selectionPath.getLastPathComponent();
            enabled = !classTreeNode.isPackageNode();
        }
        btnOk.setEnabled(enabled);
    }

    private void doOk() {

        StringBuffer buffer = new StringBuffer();
        TreePath selectionPath = tree.getSelectionPath();
        for (int i = 1; i < selectionPath.getPathCount(); i++) {
            if (buffer.length() > 0) {
                buffer.append('/');
            }
            buffer.append(selectionPath.getPathComponent(i).toString());
        }
        selectedClassName = buffer.toString();

        setVisible(false);
    }


    private void doCancel() {
        setVisible(false);
    }

    private void doSetup() {
        frame.getActionSetupClasspath().actionPerformed(new ActionEvent(this, 0, null));
        conditionalUpdate();
    }

    private void doSync(final boolean reset) {

        final DefaultTreeModel model = reset ? new DefaultTreeModel(new ClassTreeNode()) : (DefaultTreeModel)tree.getModel();
        Runnable mergeTask = new Runnable() {
            public void run() {
                if (classpathComponent != null) {
                    classpathComponent.mergeClassesIntoTree(model, reset);
                }
            }
        };

        progressDialog.setRunnable(mergeTask);
        progressDialog.setVisible(true);

        if (reset) {
            tree.setModel(model);
        }
        tree.expandPath(new TreePath(model.getRoot()));
        resetOnNextMerge = false;
        needsMerge = false;
    }


}
