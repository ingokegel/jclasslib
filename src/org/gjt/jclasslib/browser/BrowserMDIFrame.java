/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.browser.config.BrowserConfig;
import org.gjt.jclasslib.browser.config.classpath.*;
import org.gjt.jclasslib.browser.config.window.WindowState;
import org.gjt.jclasslib.mdi.*;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.util.GUIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.*;
import java.io.*;
import java.net.URL;
import java.util.prefs.Preferences;


/**
 * MDI Frame and entry point for the class file browser application.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
 * @version $Revision: 1.11 $ $Date: 2006-03-02 11:42:37 $
 */
public class BrowserMDIFrame extends BasicMDIFrame {

    static final ImageIcon ICON_APPLICATION = loadIcon("jclasslib.gif");

    private static final String SETTINGS_WORKSPACE_CHOOSER_PATH = "workspaceChooserPath";
    private static final String SETTINGS_CLASSES_CHOOSER_PATH = "classesChooserPath";

    private static final ImageIcon ICON_OPEN_CLASS_FILE = loadIcon("open_small.png");
    private static final ImageIcon ICON_OPEN_CLASS_FILE_LARGE = loadIcon("open_large.png");
    private static final ImageIcon ICON_OPEN_WORKSPACE = loadIcon("open_ws_small.png");
    private static final ImageIcon ICON_OPEN_WORKSPACE_LARGE = loadIcon("open_ws_large.png");
    private static final ImageIcon ICON_SAVE_WORKSPACE = loadIcon("save_ws_small.png");
    private static final ImageIcon ICON_SAVE_WORKSPACE_LARGE = loadIcon("save_ws_large.png");
    private static final ImageIcon ICON_BACKWARD = loadIcon("browser_backward_small.png");
    private static final ImageIcon ICON_BACKWARD_LARGE = loadIcon("browser_backward_large.png");
    private static final ImageIcon ICON_FORWARD = loadIcon("browser_forward_small.png");
    private static final ImageIcon ICON_FORWARD_LARGE = loadIcon("browser_forward_large.png");
    private static final ImageIcon ICON_RELOAD = loadIcon("reload_small.png");
    private static final ImageIcon ICON_RELOAD_LARGE = loadIcon("reload_large.png");
    private static final ImageIcon ICON_WEB = loadIcon("web_small.png");
    private static final ImageIcon ICON_WEB_LARGE = loadIcon("web_large.png");
    private static final ImageIcon ICON_BROWSE_CLASSPATH = loadIcon("tree_small.png");
    private static final ImageIcon ICON_BROWSE_CLASSPATH_LARGE = loadIcon("tree_large.png");
    private static final ImageIcon ICON_HELP = loadIcon("help.png");

    /**
     * Load an icon from the <tt>images</tt> directory.
     *
     * @param fileName the file name for the icon
     * @return the icon
     */
    public static ImageIcon loadIcon(String fileName) {

        URL imageURL = BrowserMDIFrame.class.getResource("images/" + fileName);
        return new ImageIcon(imageURL);
    }

    private Action actionOpenClassFile;
    private Action actionBrowseClasspath;
    private Action actionSetupClasspath;
    private Action actionNewWorkspace;
    private Action actionOpenWorkspace;
    private Action actionSaveWorkspace;
    private Action actionSaveWorkspaceAs;
    private Action actionQuit;
    private Action actionShowHomepage;
    private Action actionShowEJT;
    private Action actionBackward;
    private Action actionForward;
    private Action actionReload;
    private Action actionShowHelp;
    private Action actionAbout;

    private File workspaceFile;
    private String workspaceChooserPath = "";
    private String classesChooserPath = "";
    private BrowserConfig config;

    // Visual Components

    private JFileChooser workspaceFileChooser;
    private JFileChooser classesFileChooser;

    private RecentMenu recentMenu;
    private ClasspathSetupDialog classpathSetupDialog;
    private ClasspathBrowser classpathBrowser;
    private ClasspathBrowser jarBrowser;

    /**
     * Constructor.
     */
    public BrowserMDIFrame() {

        doNewWorkspace();

        recentMenu = new RecentMenu(this);
        loadSettings();
        setupActions();
        setupMenu();
        setupFrame();
    }

    /**
     * Get the current browser config.
     *
     * @return the browser config
     */
    public BrowserConfig getConfig() {
        return config;
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            desktopManager.showAll();
        }
    }

    /**
     * Get the action for displaying the classpath setup dialog.
     *
     * @return the action
     */
    public Action getActionSetupClasspath() {
        return actionSetupClasspath;
    }

    /**
     * Get the action for going backward in the navigation history.
     *
     * @return the action
     */
    public Action getActionBackward() {
        return actionBackward;
    }

    /**
     * Get the action for going forward in the navigation history.
     *
     * @return the action
     */
    public Action getActionForward() {
        return actionForward;
    }

    /**
     * Get the action for reloading the current frame.
     *
     * @return the action
     */
    public Action getActionReload() {
        return actionReload;
    }

    /**
     * Get the last path for the classes file chooser.
     *
     * @return the path
     */
    public String getClassesChooserPath() {
        return classesChooserPath;
    }

    /**
     * Set the last path for the classes file chooser.
     *
     * @param classesChooserPath the path
     */
    public void setClassesChooserPath(String classesChooserPath) {
        this.classesChooserPath = classesChooserPath;
    }

    /**
     * Open a workspace file.
     *
     * @param file the file.
     */
    public void openWorkspace(File file) {

        repaintNow();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        closeAllFrames();
        try {
            FileInputStream fos = new FileInputStream(file);
            XMLDecoder decoder = new XMLDecoder(fos);
            config = (BrowserConfig)decoder.readObject();
            readMDIConfig(config.getMDIConfig());
            decoder.close();
            recentMenu.addRecentWorkspace(file);
            if (classpathBrowser != null) {
                classpathBrowser.setClasspathComponent(config);
            }
        } catch (FileNotFoundException e) {
            GUIHelper.showMessage(this, "An error occured while reading " + file.getPath(), JOptionPane.ERROR_MESSAGE);
        } finally {
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
        workspaceFile = file;
        updateTitle();
        actionSaveWorkspaceAs.setEnabled(true);
    }

    /**
     * Open an internal frame for a given file.
     *
     * @param file the file
     * @return the created internal frame
     */
    public BrowserInternalFrame openClassFromFile(File file) {

        BrowserInternalFrame frame = new BrowserInternalFrame(desktopManager, new WindowState(file.getPath()));
        ClassFile classFile = frame.getClassFile();

        if (classFile != null) {
            try {
                String className = classFile.getThisClassName();
                String[] pathComponents = className.split("/");
                File currentDirectory = file.getParentFile();
                boolean validClasspathEntry = true;
                for (int i = pathComponents.length - 2; i >= 0; i--) {
                    String pathComponent = pathComponents[i];
                    if (!currentDirectory.getName().equals(pathComponent)) {
                        validClasspathEntry = false;
                        break;
                    }
                    currentDirectory = currentDirectory.getParentFile();
                }
                if (validClasspathEntry) {
                    config.addClasspathDirectory(currentDirectory.getPath());
                }
            } catch (InvalidByteCodeException e) {
            }
        }
        return frame;
    }

    protected void doQuit() {
        saveSettings();
        super.doQuit();
    }

    protected BasicDesktopManager createDesktopManager() {
        return new BrowserDesktopManager(this);
    }

    protected Class[] getFrameConstructorArguments(Class frameClass) {
        return BrowserInternalFrame.CONSTRUCTOR_ARGUMENTS;
    }

    private void setupActions() {

        actionOpenClassFile = new DefaultAction("Open class file", ICON_OPEN_CLASS_FILE);
        actionOpenClassFile.putValue(Action.SHORT_DESCRIPTION, "Open a class file");

        actionBrowseClasspath = new DefaultAction("Browse classpath", ICON_BROWSE_CLASSPATH);
        actionBrowseClasspath.putValue(Action.SHORT_DESCRIPTION, "Browse the current classpath to open a class file");

        actionSetupClasspath = new DefaultAction("Setup classpath", GUIHelper.ICON_EMPTY);
        actionSetupClasspath.putValue(Action.SHORT_DESCRIPTION, "Configure the classpath");

        actionNewWorkspace = new DefaultAction("New workspace", GUIHelper.ICON_EMPTY);
        actionNewWorkspace.putValue(Action.SHORT_DESCRIPTION, "Close all frames and open a new workspace");

        actionOpenWorkspace = new DefaultAction("Open workspace", ICON_OPEN_WORKSPACE);
        actionOpenWorkspace.putValue(Action.SHORT_DESCRIPTION, "Open workspace from disk");

        actionSaveWorkspace = new DefaultAction("Save workspace", ICON_SAVE_WORKSPACE);
        actionSaveWorkspace.putValue(Action.SHORT_DESCRIPTION, "Save current workspace to disk");

        actionSaveWorkspaceAs = new DefaultAction("Save workspace as", GUIHelper.ICON_EMPTY);
        actionSaveWorkspaceAs.putValue(Action.SHORT_DESCRIPTION, "Save current workspace to a different file");
        actionSaveWorkspaceAs.setEnabled(false);

        actionQuit = new DefaultAction("Quit", GUIHelper.ICON_EMPTY);

        actionBackward = new DefaultAction("Backward", ICON_BACKWARD);
        actionBackward.putValue(Action.SHORT_DESCRIPTION, "Move backward in the navigation history");
        actionBackward.setEnabled(false);

        actionForward = new DefaultAction("Forward", ICON_FORWARD);
        actionForward.putValue(Action.SHORT_DESCRIPTION, "Move forward in the navigation history");
        actionForward.setEnabled(false);

        actionReload = new DefaultAction("Reload", ICON_RELOAD);
        actionReload.putValue(Action.SHORT_DESCRIPTION, "Reload class file");
        actionReload.setEnabled(false);

        actionShowHomepage = new DefaultAction("jclasslib on the web", ICON_WEB);
        actionShowHomepage.putValue(Action.SHORT_DESCRIPTION, "Visit jclasslib on the web");

        actionShowEJT = new DefaultAction("ej-technologies on the web", ICON_WEB);
        actionShowEJT.putValue(Action.SHORT_DESCRIPTION, "Visit ej-technologies on the web");

        actionShowHelp = new DefaultAction("Show help", ICON_HELP);
        actionShowHelp.putValue(Action.SHORT_DESCRIPTION, "Show the jclasslib documentation");

        actionAbout = new DefaultAction("About the jclasslib bytecode viewer", GUIHelper.ICON_EMPTY);
        actionAbout.putValue(Action.SHORT_DESCRIPTION, "Show the jclasslib documentation");
    }

    private void setupMenu() {

        JMenuItem menuItem;
        JMenuBar menuBar = new JMenuBar();

        JMenu menuFile = new JMenu("File");
        menuFile.add(actionOpenClassFile);
        menuFile.addSeparator();
        menuFile.add(actionNewWorkspace);
        menuFile.add(actionOpenWorkspace);
        menuFile.add(recentMenu);
        menuFile.addSeparator();
        menuFile.add(actionSaveWorkspace);
        menuFile.add(actionSaveWorkspaceAs);
        menuFile.addSeparator();
        menuFile.add(actionShowHomepage);
        menuFile.add(actionShowEJT);
        menuFile.addSeparator();
        menuFile.add(actionQuit);

        JMenu menuClasspath = new JMenu("Classpath");
        menuClasspath.add(actionBrowseClasspath);
        menuClasspath.add(actionSetupClasspath);

        JMenu menuBrowse = new JMenu("Browse");
        menuItem = menuBrowse.add(actionBackward);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, Event.ALT_MASK));
        menuItem = menuBrowse.add(actionForward);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, Event.ALT_MASK));

        menuBrowse.addSeparator();
        menuItem = menuBrowse.add(actionReload);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));

        JMenu menuHelp = new JMenu("Help");
        menuItem = menuHelp.add(actionShowHelp);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        menuHelp.add(actionAbout);


        menuBar.add(menuFile);
        menuBar.add(menuClasspath);
        menuBar.add(menuBrowse);
        menuBar.add(menuWindow);
        menuBar.add(menuHelp);
        setJMenuBar(menuBar);

    }

    private void setupFrame() {

        Container contentPane = getContentPane();

        contentPane.add(buildToolbar(), BorderLayout.NORTH);
        setIconImage(ICON_APPLICATION.getImage());
    }

    private void updateTitle() {

        if (workspaceFile == null) {
            setTitle(BrowserApplication.APPLICATION_TITLE);
            if (actionSaveWorkspaceAs != null) {
                actionSaveWorkspaceAs.setEnabled(false);
            }
        } else {
            setTitle(BrowserApplication.APPLICATION_TITLE + " [" + workspaceFile.getName() + "]");
        }
    }

    private JToolBar buildToolbar() {

        JToolBar toolBar = new JToolBar();
        toolBar.add(actionOpenClassFile).setIcon(ICON_OPEN_CLASS_FILE_LARGE);
        toolBar.add(actionBrowseClasspath).setIcon(ICON_BROWSE_CLASSPATH_LARGE);
        toolBar.addSeparator();
        toolBar.add(actionOpenWorkspace).setIcon(ICON_OPEN_WORKSPACE_LARGE);
        toolBar.add(actionSaveWorkspace).setIcon(ICON_SAVE_WORKSPACE_LARGE);
        toolBar.addSeparator();
        toolBar.add(actionBackward).setIcon(ICON_BACKWARD_LARGE);
        toolBar.add(actionForward).setIcon(ICON_FORWARD_LARGE);
        toolBar.addSeparator();
        toolBar.add(actionReload).setIcon(ICON_RELOAD_LARGE);
        toolBar.addSeparator();
        toolBar.add(actionShowHomepage).setIcon(ICON_WEB_LARGE);

        toolBar.setFloatable(false);

        return toolBar;
    }

    private void repaintNow() {

        JComponent contentPane = (JComponent)getContentPane();
        contentPane.paintImmediately(0, 0, contentPane.getWidth(), contentPane.getHeight());
        JMenuBar menuBar = getJMenuBar();
        menuBar.paintImmediately(0, 0, menuBar.getWidth(), menuBar.getHeight());
    }

    private void loadSettings() {

        Preferences preferences = Preferences.userNodeForPackage(getClass());

        workspaceChooserPath = preferences.get(SETTINGS_WORKSPACE_CHOOSER_PATH, workspaceChooserPath);
        classesChooserPath = preferences.get(SETTINGS_CLASSES_CHOOSER_PATH, classesChooserPath);
        recentMenu.read(preferences);
    }

    private void saveSettings() {

        Preferences preferences = Preferences.userNodeForPackage(getClass());
        preferences.put(SETTINGS_WORKSPACE_CHOOSER_PATH, workspaceChooserPath);
        preferences.put(SETTINGS_CLASSES_CHOOSER_PATH, classesChooserPath);
        recentMenu.save(preferences);
    }

    private void doSaveWorkspace(boolean saveAs) {

        config.setMDIConfig(createMDIConfig());
        if (workspaceFile != null && !saveAs) {
            saveWorkspaceToFile(workspaceFile);
            return;
        }

        JFileChooser fileChooser = getWorkspaceFileChooser();
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase().endsWith("." + BrowserApplication.WORKSPACE_FILE_SUFFIX)) {
                selectedFile = new File(selectedFile.getPath() + "." + BrowserApplication.WORKSPACE_FILE_SUFFIX);
            }
            if (selectedFile.exists() &&
                    GUIHelper.showOptionDialog(this,
                            "The file " + selectedFile.getPath() + "\nexists. Do you want to overwrite this file?",
                            GUIHelper.YES_NO_OPTIONS,
                            JOptionPane.QUESTION_MESSAGE) != 0) {
                return;
            }
            saveWorkspaceToFile(selectedFile);
            workspaceFile = selectedFile;
            updateTitle();
            workspaceChooserPath = fileChooser.getCurrentDirectory().getAbsolutePath();
        }
    }

    private void saveWorkspaceToFile(File file) {

        try {
            FileOutputStream fos = new FileOutputStream(file);
            XMLEncoder encoder = new XMLEncoder(fos);
            encoder.writeObject(config);
            encoder.close();
            recentMenu.addRecentWorkspace(file);
        } catch (FileNotFoundException e) {
            GUIHelper.showMessage(this, "An error occured while saving to " + file.getPath(), JOptionPane.ERROR_MESSAGE);
        }
        GUIHelper.showMessage(this, "Workspace saved to " + file.getPath(), JOptionPane.INFORMATION_MESSAGE);
        actionSaveWorkspaceAs.setEnabled(true);
    }

    private void doNewWorkspace() {

        closeAllFrames();
        workspaceFile = null;
        config = new BrowserConfig();
        config.addRuntimeLib();
        if (classpathBrowser != null) {
            classpathBrowser.setClasspathComponent(config);
        }
        updateTitle();
    }

    private void doOpenWorkspace() {

        JFileChooser fileChooser = getWorkspaceFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            openWorkspace(selectedFile);
            workspaceChooserPath = fileChooser.getCurrentDirectory().getAbsolutePath();
        }
    }

    private void doOpenClassFile() {

        JFileChooser fileChooser = getClassesFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            repaintNow();
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            File file = fileChooser.getSelectedFile();
            classesChooserPath = fileChooser.getCurrentDirectory().getAbsolutePath();

            BrowserInternalFrame frame;
            if (file.getPath().toLowerCase().endsWith(".class")) {
                frame = openClassFromFile(file);
            } else {
                frame = openClassFromJar(file);
            }

            if (frame != null) {
                try {
                    frame.setMaximum(true);
                } catch (PropertyVetoException ex) {
                }
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private BrowserInternalFrame openClassFromJar(File file) {

        ClasspathArchiveEntry entry = new ClasspathArchiveEntry();
        entry.setFileName(file.getPath());
        if (jarBrowser == null) {
            jarBrowser = new ClasspathBrowser(this, null, "Classes in selected JAR file:", false);
        }
        jarBrowser.clear();
        jarBrowser.setClasspathComponent(entry);
        jarBrowser.setVisible(true);
        String selectedClassName = jarBrowser.getSelectedClassName();
        if (selectedClassName == null) {
            return null;
        }

        String fileName = file.getPath() + "!" + selectedClassName + ".class";

        BrowserInternalFrame frame = new BrowserInternalFrame(desktopManager, new WindowState(fileName));
        ClassFile classFile = frame.getClassFile();
        if (classFile != null) {
            config.addClasspathArchive(file.getPath());
        }

        return frame;
    }

    private void doBrowseClasspath() {

        if (classpathBrowser == null) {
            classpathBrowser = new ClasspathBrowser(this, config, "Configured classpath:", true);
        }
        classpathBrowser.setVisible(true);
        String selectedClassName = classpathBrowser.getSelectedClassName();
        if (selectedClassName == null) {
            return;
        }

        FindResult findResult = config.findClass(selectedClassName);
        if (findResult == null) {
            GUIHelper.showMessage(this, "Error loading " + selectedClassName, JOptionPane.ERROR_MESSAGE);
            return;
        }

        repaintNow();
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        BrowserInternalFrame frame = new BrowserInternalFrame(desktopManager, new WindowState(findResult.getFileName()));
        try {
            frame.setMaximum(true);
        } catch (PropertyVetoException ex) {
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

    }

    private void doSetupClasspath() {
        if (classpathSetupDialog == null) {
            classpathSetupDialog = new ClasspathSetupDialog(this);

        }
        classpathSetupDialog.setVisible(true);
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

    private JFileChooser getWorkspaceFileChooser() {

        if (workspaceFileChooser == null) {
            workspaceFileChooser = new JFileChooser(workspaceChooserPath);
            workspaceFileChooser.setDialogTitle("Choose workspace file");
            workspaceFileChooser.setFileFilter(new BasicFileFilter(BrowserApplication.WORKSPACE_FILE_SUFFIX, "jclasslib workspace files"));
        }

        return workspaceFileChooser;
    }

    private JFileChooser getClassesFileChooser() {

        if (classesFileChooser == null) {
            classesFileChooser = new JFileChooser(classesChooserPath);
            classesFileChooser.setDialogTitle("Choose class file or jar file");
            classesFileChooser.addChoosableFileFilter(new BasicFileFilter("class", "class files"));
            classesFileChooser.addChoosableFileFilter(new BasicFileFilter("jar", "jar files"));
            classesFileChooser.setFileFilter(new BasicFileFilter(new String[]{"class", "jar"}, "class files and jar files"));
        }

        return classesFileChooser;
    }

    private void doAbout() {
        new BrowserAboutDialog(this).setVisible(true);
    }

    private class DefaultAction extends AbstractAction {

        private DefaultAction(String name, Icon icon) {
            super(name, icon);
        }

        public void actionPerformed(ActionEvent ev) {

            if (this == actionOpenClassFile) {
                doOpenClassFile();
            } else if (this == actionBrowseClasspath) {
                doBrowseClasspath();
            } else if (this == actionSetupClasspath) {
                doSetupClasspath();
            } else if (this == actionNewWorkspace) {
                doNewWorkspace();
            } else if (this == actionOpenWorkspace) {
                doOpenWorkspace();
            } else if (this == actionSaveWorkspace) {
                doSaveWorkspace(false);
            } else if (this == actionSaveWorkspaceAs) {
                doSaveWorkspace(true);
            } else if (this == actionQuit) {
                doQuit();
            } else if (this == actionBackward) {
                doBackward();
            } else if (this == actionForward) {
                doForward();
            } else if (this == actionReload) {
                doReload();
            } else if (this == actionShowHomepage) {
                GUIHelper.showURL("http://www.ej-technologies.com/products/jclasslib/overview.html");
            } else if (this == actionShowEJT) {
                GUIHelper.showURL("http://www.ej-technologies.com");
            } else if (this == actionShowHelp) {
                try {
                    GUIHelper.showURL(new File("doc/help.html").getCanonicalFile().toURL().toExternalForm());
                } catch (IOException e) {
                }
            } else if (this == actionAbout) {
                doAbout();
            }
        }

    }

}
