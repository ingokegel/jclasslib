/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath;

import org.gjt.jclasslib.browser.BrowserMDIFrame;
import org.gjt.jclasslib.browser.config.BrowserConfig;
import org.gjt.jclasslib.mdi.BasicFileFilter;
import org.gjt.jclasslib.util.GUIHelper;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;

/**
    Dialog for viewing and modifying the classpath.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 08:10:15 $
*/
public class ClasspathSetupDialog extends JDialog
                                  implements ActionListener, ListSelectionListener {

    private static final int DIALOG_WIDTH = 500;
    private static final int DIALOG_HEIGHT = 300;

    private static final Dimension IMAGE_BUTTON_SIZE = new Dimension(28, 28);
    private static final Icon ICON_ADD = BrowserMDIFrame.loadIcon("add.png");
    private static final Icon ICON_REMOVE = BrowserMDIFrame.loadIcon("remove.png");
    private static final Icon ICON_UP = BrowserMDIFrame.loadIcon("up.png");
    private static final Icon ICON_DOWN = BrowserMDIFrame.loadIcon("down.png");

    private BrowserMDIFrame frame;

    private DefaultListModel listModel;

    private JList lstElements;
    private JScrollPane scpLstElements;
    private JButton btnAdd;
    private JButton btnRemove;
    private JButton btnUp;
    private JButton btnDown;

    private JButton btnOk;
    private JButton btnCancel;
    private JFileChooser fileChooser;

    /**
     * Constructor.
     * @param frame the parent frame.
     */
    public ClasspathSetupDialog(BrowserMDIFrame frame)  {
        super(frame);
        this.frame = frame;
        setupControls();
        setupAccelerators();
        setupComponent();
        setupEventHandlers();
        checkEnabledStatus();
    }

    public void valueChanged(ListSelectionEvent event) {
        checkEnabledStatus();
    }

    public void actionPerformed(ActionEvent event) {
        Object source = event.getSource();
        if (source == btnAdd) {
            doAdd();
        } else if (source == btnRemove) {
            doRemove();
        } else if (source == btnUp) {
            doUp();
        } else if (source == btnDown) {
            doDown();
        } else if (source == btnCancel) {
            doCancel();
        } else if (source == btnOk) {
            doOk();
        }
        checkEnabledStatus();
    }

    public void setVisible(boolean visible) {
        if (visible) {
            updateList();
        }
        super.setVisible(visible);
    }

    private void updateList() {

        listModel.clear();
        Iterator it = frame.getConfig().getClasspath().iterator();
        while (it.hasNext()) {
            listModel.addElement(it.next());
        }
    }

    private void setupControls() {

        listModel = new DefaultListModel();

        lstElements = new JList(listModel);
        lstElements.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        lstElements.setCellRenderer(new ClasspathCellRenderer());
        scpLstElements = new JScrollPane(lstElements);
        scpLstElements.setBorder(BorderFactory.createEtchedBorder());

        btnAdd = new JButton(ICON_ADD);
        btnAdd.setToolTipText("Add a classpath entry (INS)");
        makeImageButton(btnAdd);
        btnRemove = new JButton(ICON_REMOVE);
        btnRemove.setToolTipText("Remove a classpath entry (DEL)");
        makeImageButton(btnRemove);
        btnUp = new JButton(ICON_UP);
        btnUp.setToolTipText("Move a classpath entry up (ALT-UP)");
        makeImageButton(btnUp);
        btnDown = new JButton(ICON_DOWN);
        btnDown.setToolTipText("Move a classpath entry down (ALT-DOWN)");
        makeImageButton(btnDown);

        btnOk = new JButton("Ok");
        btnCancel = new JButton("Cancel");
        btnOk.setPreferredSize(btnCancel.getPreferredSize());

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
        contentPane.add(new JLabel("Classpath:"), gc);
        gc.gridy++;

        gc.weighty = 1;
        gc.insets.top = 0;
        gc.fill = GridBagConstraints.BOTH;
        contentPane.add(createListPanel(), gc);
        gc.gridy++;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weighty = 0;
        gc.insets.top = 3;
        gc.insets.bottom = 5;
        contentPane.add(createButtonBox(), gc);
        getRootPane().setDefaultButton(btnOk);

        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setModal(true);
        setTitle("Setup classpath");
        GUIHelper.centerOnParentWindow(this, getOwner());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

    }

    private void setupEventHandlers() {

        btnCancel.addActionListener(this);
        btnOk.addActionListener(this);

        btnAdd.addActionListener(this);
        btnRemove.addActionListener(this);
        btnUp.addActionListener(this);
        btnDown.addActionListener(this);

        lstElements.addListSelectionListener(this);

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                doCancel();
            }
        });
    }

    private void setupAccelerators() {

        addAccelerator((JComponent)getContentPane(), KeyEvent.VK_ESCAPE, 0, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                doCancel();
            }
        });

        addAccelerator(lstElements, KeyEvent.VK_INSERT, 0, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                doAdd();
            }
        });
        addAccelerator(lstElements, KeyEvent.VK_DELETE, 0, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                doRemove();
            }
        });
        addAccelerator(lstElements, KeyEvent.VK_UP, KeyEvent.ALT_MASK, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                doUp();
            }
        });
        addAccelerator(lstElements, KeyEvent.VK_DOWN, KeyEvent.ALT_MASK, new AbstractAction() {
            public void actionPerformed(ActionEvent event) {
                doDown();
            }
        });

    }

    private void addAccelerator(JComponent component, int keyCode, int keyMask, AbstractAction action) {

        KeyStroke keyStroke = KeyStroke.getKeyStroke(keyCode, keyMask);
        Object key = new Object();
        component.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(keyStroke, key);
        component.getActionMap().put(key, action);
    }

    private JPanel createListPanel() {

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        panel.add(scpLstElements, BorderLayout.CENTER);
        panel.add(createModificationButtonBox(), BorderLayout.EAST);

        return panel;
    }

    private Box createModificationButtonBox() {

        Box box = Box.createVerticalBox();
        box.add(btnAdd);
        box.add(btnRemove);
        box.add(Box.createVerticalGlue());
        box.add(btnUp);
        box.add(btnDown);
        return box;
    }

    private Box createButtonBox() {

        Box box = Box.createHorizontalBox();
        box.add(Box.createHorizontalGlue());
        box.add(btnOk);
        box.add(btnCancel);

        return box;
    }

    private void makeImageButton(AbstractButton button) {
        button.setMinimumSize(IMAGE_BUTTON_SIZE);
        button.setPreferredSize(IMAGE_BUTTON_SIZE);
        button.setMaximumSize(IMAGE_BUTTON_SIZE);
    }


    private void doCancel() {
        setVisible(false);
    }

    private void doOk() {

        List newEntries = new ArrayList();
        for (int i = 0; i < listModel.getSize(); i++) {
            newEntries.add(listModel.getElementAt(i));
        }
        BrowserConfig config = frame.getConfig();
        List oldEntries = new ArrayList(config.getClasspath());

        Iterator itOld = oldEntries.iterator();
        while (itOld.hasNext()) {
            ClasspathEntry entry = (ClasspathEntry)itOld.next();
            if (!newEntries.contains(entry)) {
                config.removeClasspathEntry(entry);
            }
        }
        Iterator itNew = newEntries.iterator();
        while (itNew.hasNext()) {
            ClasspathEntry entry = (ClasspathEntry)itNew.next();
            if (!oldEntries.contains(entry)) {
                config.addClasspathEntry(entry);
            }
        }

        config.setClasspath(newEntries);
        setVisible(false);
    }

    private void doAdd() {

        if (fileChooser == null) {
            fileChooser = new JFileChooser(frame.getClassesChooserPath());
            fileChooser.setDialogTitle("Choose directory or jar file");
            fileChooser.setFileFilter(new BasicFileFilter("jar", "jar files and directories"));
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setMultiSelectionEnabled(true);
        }

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            frame.setClassesChooserPath(fileChooser.getCurrentDirectory().getAbsolutePath());
            File[] files = fileChooser.getSelectedFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                ClasspathEntry entry;
                if (file.isDirectory()) {
                    entry = new ClasspathDirectoryEntry();
                    entry.setFileName(file.getPath());
                } else {
                    entry = new ClasspathArchiveEntry();
                    entry.setFileName(file.getPath());

                }
                if (!isInModel(entry)) {
                    listModel.addElement(entry);
                    selectIndex(listModel.getSize() - 1);
                }
            }
        }
    }

    private boolean isInModel(ClasspathEntry entry) {

        for (int i = 0; i < listModel.getSize(); i++) {
            if (listModel.getElementAt(i).equals(entry)) {
                return true;
            }
        }
        return false;
    }

    private void doRemove() {
        int selectedIndex = lstElements.getSelectedIndex();
        if (selectedIndex > -1) {
            listModel.remove(selectedIndex);
            selectIndex(selectedIndex);
        }
    }

    private void doUp() {
        int selectedIndex = lstElements.getSelectedIndex();
        if (selectedIndex > 0) {
            Object entry = listModel.remove(selectedIndex);
            int newSelectedIndex = selectedIndex - 1;
            listModel.insertElementAt(entry, newSelectedIndex);
            selectIndex(newSelectedIndex);
        }
    }

    private void doDown() {
        int selectedIndex = lstElements.getSelectedIndex();
        if (selectedIndex < listModel.getSize() - 1) {
            Object entry = listModel.remove(selectedIndex);
            int newSelectedIndex = selectedIndex + 1;
            listModel.insertElementAt(entry, newSelectedIndex);
            selectIndex(newSelectedIndex);
        }
    }

    private void selectIndex(int newSelectedIndex) {
        newSelectedIndex = Math.min(newSelectedIndex, listModel.getSize() - 1);
        if (newSelectedIndex > -1) {
            lstElements.setSelectedIndex(newSelectedIndex);
            lstElements.ensureIndexIsVisible(newSelectedIndex);
        }
    }

    private void checkEnabledStatus() {

        int selectedIndex = lstElements.getSelectedIndex();
        boolean removeEnabled = selectedIndex > -1;
        boolean upEnabled = selectedIndex > 0;
        boolean downEnabled = selectedIndex > -1 && selectedIndex < listModel.getSize() - 1;
        btnRemove.setEnabled(removeEnabled);
        btnUp.setEnabled(upEnabled);
        btnDown.setEnabled(downEnabled);
    }

}
