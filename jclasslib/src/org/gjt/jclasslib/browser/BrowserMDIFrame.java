/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.mdi.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import java.io.*;
import java.util.*;
import java.beans.*;


/**
    MDI Frame and entry point for the class file browser application.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.4 $ $Date: 2002-02-26 16:29:24 $
*/
public class BrowserMDIFrame extends BasicMDIFrame {

    /** System property used to choose default look and feel */
    public static final String SYSTEM_PROPERTY_LAF_DEFAULT = "classlib.laf.default";

    /** Title of the applicaton */
    protected static final String APPLICATION_TITLE = "Class file browser";
    /** Title for message windows */
    protected static final String MESSAGE_TITLE = "jclasslib";

    private static final String SETTINGS_DOT_FILE = ".jclasslib.properties";
    private static final String SETTINGS_HEADER = APPLICATION_TITLE + " properties";
    private static final String SETTINGS_PROPERTY_CHOOSER_PATH = "chooserPath";
    
    private File chooserPath = null;

    // Actions
    
    /** Action for choosing a class file and displaying it a new child window */
    protected Action actionChoose;
    /** Action for saving the application state to disk */
    protected Action actionSaveSettings;
    /** Action for exiting the application */
    protected Action actionQuit;
    /** Action for moving backward in the navigation history */
    protected Action actionBackward;
    /** Action for moving forward in the navigation history */
    protected Action actionForward;
    /** Action for reloading a class file */
    protected Action actionReload;

    // Visual Components

    private JMenu menuFile;
    private JMenu menuBrowse;

    public BrowserMDIFrame() {
        
        loadSettings();
        setupActions();
        setupMenu();
        setupFrame();
    }
    
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            desktopManager.showAll();
        }
    }

    protected BasicDesktopManager createDesktopManager() {
        
        return new BrowserDesktopManager(this);
    }

    private void setupActions() {

        actionChoose = new DefaultAction("Open class file", loadIcon("open_large.gif"));
        actionChoose.putValue(Action.SHORT_DESCRIPTION, "Open a class file");

        actionSaveSettings = new DefaultAction("Save settings", loadIcon("save_large.gif"));
        actionSaveSettings.putValue(Action.SHORT_DESCRIPTION, "Save option settings, file and window state to current directory");
        
        actionQuit = new DefaultAction("Quit");

        actionBackward = new DefaultAction("Backward", loadIcon("browser_backward_large.gif"));
        actionBackward.putValue(Action.SHORT_DESCRIPTION, "Move backward in the navigation history");
        actionBackward.setEnabled(false);

        actionForward = new DefaultAction("Forward", loadIcon("browser_forward_large.gif"));
        actionForward.putValue(Action.SHORT_DESCRIPTION, "Move forward in the navigation history");
        actionForward.setEnabled(false);

        actionReload = new DefaultAction("Reload", loadIcon("reload_large.gif"));
        actionReload.putValue(Action.SHORT_DESCRIPTION, "Reload class file");
        actionReload.setEnabled(false);
        
    }

    private void setupMenu() {

        JMenuItem menuItem;
        JMenuBar menuBar = new JMenuBar();
        
        menuFile = new JMenu("File");
            menuFile.add(actionChoose).setIcon(loadIcon("open_small.gif"));
            menuFile.addSeparator();
            menuFile.add(actionSaveSettings).setIcon(loadIcon("save_small.gif"));
            menuFile.addSeparator();
            menuFile.add(actionQuit).setIcon(loadIcon("exit.gif"));

        menuBrowse = new JMenu("Browse");
            menuItem = menuBrowse.add(actionBackward);
                menuItem.setIcon(loadIcon("browser_backward_small.gif"));
                menuItem.setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.ALT_MASK));
            menuItem = menuBrowse.add(actionForward);
                menuItem.setIcon(loadIcon("browser_forward_small.gif"));
                menuItem.setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Event.ALT_MASK));
                
            menuBrowse.addSeparator();
            menuItem = menuBrowse.add(actionReload);
                menuItem.setIcon(loadIcon("reload_small.gif"));
                menuItem.setAccelerator(
                    KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
            
        menuBar.add(menuFile);
        menuBar.add(menuBrowse);
        menuBar.add(menuWindow);
        setJMenuBar(menuBar);
        
    }

    private void setupFrame() {

        Container contentPane = getContentPane();
        
        setTitle(APPLICATION_TITLE);
        contentPane.add(buildToolbar(), BorderLayout.NORTH);
        setIconImage(loadIcon("jclasslib.gif").getImage());
    }
    
    private JToolBar buildToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.add(actionChoose);
        toolBar.addSeparator();
        toolBar.add(actionSaveSettings);
        toolBar.addSeparator();
        toolBar.add(actionBackward);
        toolBar.add(actionForward);
        toolBar.addSeparator();
        toolBar.add(actionReload);
        toolBar.setFloatable(false);
        
        return toolBar;
    }
    
    private void loadSettings() {

        Properties props = loadSettings(SETTINGS_DOT_FILE);
        if (props == null) {
            return;
        }

        String chooserPathName = (String)props.get(SETTINGS_PROPERTY_CHOOSER_PATH);

        if (chooserPathName != null) {
            chooserPath = new File(chooserPathName);
        }

    }
    
    private void doSaveSettings() {
        
        Properties props = new Properties();
        props.put(SETTINGS_PROPERTY_CHOOSER_PATH, chooserPath == null ? "" : chooserPath.getAbsolutePath());

        saveSettings(props, SETTINGS_DOT_FILE, SETTINGS_HEADER, MESSAGE_TITLE);
    }

    private void doChoose() {
        
        JFileChooser fileChooser = new JFileChooser(chooserPath);
        fileChooser.addChoosableFileFilter(new BasicFileFilter("class", "Class files"));
        int returnValue = fileChooser.showOpenDialog(this);
        
        if(returnValue == JFileChooser.APPROVE_OPTION) {

            File file = fileChooser.getSelectedFile();
            String fileName = file.getAbsolutePath();
            chooserPath = new File(fileName.substring(0, fileName.lastIndexOf(File.separatorChar) + 1));            
            
            createInternalFrame(file);
        }        
    }
    
    private void createInternalFrame(File file) {

        BrowserInternalFrame frame = new BrowserInternalFrame(desktopManager, file);
        try {
            frame.setMaximum(true);
        } catch (PropertyVetoException ex) {
        }
    }

    private void doBackward() {
        BrowserInternalFrame frame = (BrowserInternalFrame)desktopPane.getSelectedFrame();
        if (frame != null) {
            frame.getBrowserComponent().getHistory().historyBackward();
        }
    }

    private void doForward() {
        BrowserInternalFrame frame = (BrowserInternalFrame)desktopPane.getSelectedFrame();
        if (frame != null) {
            frame.getBrowserComponent().getHistory().historyForward();
        }
    }

    private void doReload() {
        BrowserInternalFrame frame = (BrowserInternalFrame)desktopPane.getSelectedFrame();
        if (frame != null) {
            frame.reload();
        }
    }

    private class DefaultAction extends AbstractAction {

        public DefaultAction(String name) {
            super(name);
        }

        public DefaultAction(String name, Icon icon) {
            super(name, icon);
        }

        public void actionPerformed(ActionEvent ev) {

            if (this == actionChoose) {
                doChoose();
            } else if (this == actionSaveSettings) {
                doSaveSettings();
            } else if (this == actionQuit) {
                doQuit();
            } else if (this == actionBackward) {
                doBackward();
            } else if (this == actionForward) {
                doForward();
            } else if (this == actionReload) {
                doReload();
            }
        }
    }
    
    /**
        Entry point for the class file browser application.
        @param args arguments for the application. Not evaluated.
     */
    public static void main(String[] args) {
        
        if (!Boolean.getBoolean(SYSTEM_PROPERTY_LAF_DEFAULT)) {
            Class lookAndFeelClass = com.incors.plaf.kunststoff.KunststoffLookAndFeel.class;
            try {
                UIManager.setLookAndFeel(lookAndFeelClass.getName());
            } catch (Exception ex) {
            }
        }

        BrowserMDIFrame frame = new BrowserMDIFrame();
        frame.setVisible(true);
        
        // JInternalFrame can only be selected in visible JDesktopPane
        JInternalFrame[] frames = frame.desktopPane.getAllFrames();
        if (frames.length > 0) {
            try {
                frames[0].setSelected(true);
            } catch (PropertyVetoException ex) {
            }
        }
    }

}
