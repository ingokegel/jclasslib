/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.mdi;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.io.*;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.util.Properties;


/**
    Parent frame for MDI application. Handles window actions, state saving and loading
    and supplies various utility methods.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.5 $ $Date: 2003-07-08 14:04:28 $
*/
public class BasicMDIFrame extends JFrame {

    /**  Path where images are found relative to the class path root of this class
         in case <tt>SYSTEM_PROPERTY_BASE_PATH</tt> is not set */
    public static final String IMAGES_DIRECTORY = "images";

    /** Default width of the parent window */
    protected static final int DEFAULT_WINDOW_WIDTH = 800;
    /** Default height of the parent window */
    protected static final int DEFAULT_WINDOW_HEIGHT = 600;
    /** JVM system property which contains the path where images are found */
    protected static final String SYSTEM_PROPERTY_BASE_PATH = "images.basePath";

    private static final String SETTINGS_PROPERTY_WINDOW_WIDTH = "windowWidth";
    private static final String SETTINGS_PROPERTY_WINDOW_HEIGHT = "windowHeight";
    private static final String SETTINGS_PROPERTY_WINDOW_X = "windowX";
    private static final String SETTINGS_PROPERTY_WINDOW_Y = "windowY";

    private static final String SETTINGS_PROPERTY_NUMBER_OF_FRAMES = "numberOfFrames";
    private static final String SETTINGS_PROPERTY_FRAMES_CLASS_NAME = "frameClassName_";
    private static final String SETTINGS_PROPERTY_FRAMES_INIT_PARAM = "frameInitParam_";
    private static final String SETTINGS_PROPERTY_FRAMES_X = "frameX_";
    private static final String SETTINGS_PROPERTY_FRAMES_Y = "frameY_";
    private static final String SETTINGS_PROPERTY_FRAMES_WIDTH = "frameWidth_";
    private static final String SETTINGS_PROPERTY_FRAMES_HEIGHT = "frameHeight_";
    private static final String SETTINGS_PROPERTY_FRAMES_MAXIMIZED = "frameMaximized_";
    private static final String SETTINGS_PROPERTY_FRAMES_ICONIFIED = "frameIconified_";
    private static final String SETTINGS_PROPERTY_ACTIVE_FRAME = "activeFrame";
    
    /** Optional path where images are found */
    protected String imagePath;
    
    // Actions
    
    /** Action for selecting the next child window */
    protected Action actionNextWindow;
    /** Action for selecting the provious child window */
    protected Action actionPreviousWindow;
    /** Action for tiling all child windows */
    protected Action actionTileWindows;
    /** Action for stacking all child windows */
    protected Action actionStackWindows;

    // Visual components

    /** <tt>JDesktop</tt> pane which contains all child windows */
    protected JDesktopPane desktopPane;
    /** <tt>DesktopManager</tt> for this MDI parent frame */
    protected BasicDesktopManager desktopManager;    
    /** <tt>JMenu</tt> for window actions */
    protected JMenu menuWindow;

    public BasicMDIFrame() {

        setupImagePath();
        setupActions();
        setupMenu();
        setupFrame();
    }

    /**
        Create a <tt>BasicDesktopManager</tt> for this MDI parent window.
        @return the <tt>BasicDesktopManager</tt>
     */
    protected BasicDesktopManager createDesktopManager() {
        
        return new BasicDesktopManager(this);
    }
    
    /**
        Exit the application.
     */
    protected void doQuit() {
        
        dispose();
        System.exit(0);
    }
    
    /**
        Load an icon either from the path contained in <tt>SYSTEM_PROPERTY_BASE_PATH</tt>
        or from the directory <tt>IMAGES_DIRECTORY</tt> relative to the classpath
        root of this class.
        @param fileName the file name for the icon
        @return the icon
     */
    protected ImageIcon loadIcon(String fileName) {

        URL imageURL = getClass().getResource("/" + IMAGES_DIRECTORY + "/" + fileName);
        if (imageURL == null) {
            return new ImageIcon(imagePath + fileName);
        } else {
            return new ImageIcon(imageURL);
        }
    
    }

    

    /**
        Get an <tt>int</tt> property from a <tt>Properties</tt> object,
        supplying a default value if this property does not exist.
        @param props the <tt>Properties</tt> object
        @param propertyName the name of the requested property
        @param defaultValue the default value
        @return the <tt>int</tt>
     */
    protected int getIntProperty(Properties props, String propertyName, int defaultValue) {

        String propertyValue = (String)props.get(propertyName);
        int propertyIntValue = defaultValue;
        if (propertyValue != null) {
            try {
                propertyIntValue = Integer.parseInt(propertyValue);
            } catch (NumberFormatException ex) {
            }
        }
        return propertyIntValue;
    }

    /**
        Get an <tt>boolean</tt> property from a <tt>Properties</tt> object,
        supplying a default value if this property does not exist.
        @param props the <tt>Properties</tt> object
        @param propertyName the name of the requested property
        @param defaultValue the default value
        @return the <tt>boolean</tt>
     */
    protected boolean getBooleanProperty(Properties props, String propertyName, boolean defaultValue) {

        String propertyValue = (String)props.get(propertyName);
        boolean propertyBooleanValue = defaultValue;
        if (propertyValue != null) {
            propertyBooleanValue = (new Boolean(propertyValue)).booleanValue();
        }
        return propertyBooleanValue;
    }
    
    /**
        Save the state of the application to a properties file.
        @param props the <tt>Properties</tt> object to fill with information
        @param filename the file name to save to
        @param header the header for the properties file
        @param messageTitle the title for the message box confirming the operation
     */
    protected void saveSettings(Properties props, String filename, String header, String messageTitle) {

        saveWindowSettings(props);
        saveFrameSettings(props);
        
        OutputStream out = null;
        try {
            out = new BufferedOutputStream(
                      new FileOutputStream(
                      new File(filename)));
            
            props.store(out, header);
            out.flush();
            out.close();
            
            JOptionPane.showMessageDialog(this,
                                         "Settings saved to current directory",
                                         messageTitle,
                                         JOptionPane.INFORMATION_MESSAGE);
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                                          "Cannot save settings to " + filename,
                                          messageTitle,
                                          JOptionPane.ERROR_MESSAGE);
            
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex2) {}
            }
        }

    }
    
    /**
        Load the state of the application from a properties file and restore
        the application as far as possible.
        @param filename the file name to read from
        @return the <tt>Properties</tt> object filled with state information
     */
    protected Properties loadSettings(String filename) {

        setSize(DEFAULT_WINDOW_WIDTH, DEFAULT_WINDOW_HEIGHT);
        
        Properties props = new Properties();
        InputStream in = null;
        try {
            in = new BufferedInputStream(
                     new FileInputStream(
                     new File(filename)));
            
            props.load(in);
            loadWindowSettings(props);
            loadFrameSettings(props);
            loadActiveFrameSettings(props);
        }
        catch (IOException ex) {
            props = null;
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex2) {}
            }
        }
        return props;
    }

    private void loadFrameSettings(Properties props) {

        int numberOfFrames = getIntProperty(props, SETTINGS_PROPERTY_NUMBER_OF_FRAMES, 0);
        boolean anyFrameMaximized = false;
        for (int i = 0; i < numberOfFrames; i++) {
            String className = (String)props.get(SETTINGS_PROPERTY_FRAMES_CLASS_NAME + i);
            String initParam = (String)props.get(SETTINGS_PROPERTY_FRAMES_INIT_PARAM + i);
            if (className == null || initParam == null) {
                continue;
            }
            Constructor frameConstructor = null;
            try {
                Class frameClass = Class.forName(className);
                frameConstructor = frameClass.getConstructor(BasicInternalFrame.DEFAULT_CONSTRUCTOR);
                
            } catch (ClassNotFoundException ex) {
                continue;
            } catch (NoSuchMethodException ex) {
                continue;
            }

            int frameX = getIntProperty(props, SETTINGS_PROPERTY_FRAMES_X + i, -1);
            int frameY = getIntProperty(props, SETTINGS_PROPERTY_FRAMES_Y + i, -1);
            int frameWidth = getIntProperty(props, SETTINGS_PROPERTY_FRAMES_WIDTH + i, -1);
            int frameHeight = getIntProperty(props, SETTINGS_PROPERTY_FRAMES_HEIGHT + i, -1);
            boolean frameMaximized = getBooleanProperty(props, SETTINGS_PROPERTY_FRAMES_MAXIMIZED + i, false);
            anyFrameMaximized = anyFrameMaximized & frameMaximized;
            boolean frameIconified = getBooleanProperty(props, SETTINGS_PROPERTY_FRAMES_ICONIFIED + i, false);
            
            BasicInternalFrame frame = null;
            try {
                frame = (BasicInternalFrame)frameConstructor.newInstance(new Object[] {desktopManager, initParam});
            } catch (Exception ex) {
                ex.printStackTrace();
                continue;
            }
            desktopManager.resizeFrame(frame, frameX, frameY, frameWidth, frameHeight);
            
            try {
                if (frameMaximized & frameMaximized) {
                    frame.setMaximum(true);
                } else if (frameIconified) {
                    frame.setIcon(frameIconified);
                }
            } catch (PropertyVetoException ex) {
            }

        }
    }

    private void loadActiveFrameSettings(Properties props) {

        int activeFrameIndex = getIntProperty(props, SETTINGS_PROPERTY_ACTIVE_FRAME, -1);
        java.util.List openFrames = desktopManager.getOpenFrames();
        if (activeFrameIndex > -1 && activeFrameIndex < desktopManager.getOpenFrames().size()) {
            desktopManager.setActiveFrameIndex(activeFrameIndex);
        }

    }
    
    private void saveWindowSettings(Properties props) {

        props.put(SETTINGS_PROPERTY_WINDOW_WIDTH, String.valueOf(getSize().width));
        props.put(SETTINGS_PROPERTY_WINDOW_HEIGHT, String.valueOf(getSize().height));
        props.put(SETTINGS_PROPERTY_WINDOW_X, String.valueOf(getBounds().x));
        props.put(SETTINGS_PROPERTY_WINDOW_Y, String.valueOf(getBounds().y));
    }
    
    private void saveFrameSettings(Properties props) {

        java.util.List openFrames = desktopManager.getOpenFrames();
        props.put(SETTINGS_PROPERTY_NUMBER_OF_FRAMES, String.valueOf(openFrames.size()));

        int activeFrameIndex = -1;
        JInternalFrame activeFrame = desktopPane.getSelectedFrame();
        if (activeFrame != null) {
            activeFrameIndex = openFrames.indexOf(activeFrame);
        }
        props.put(SETTINGS_PROPERTY_ACTIVE_FRAME, String.valueOf(activeFrameIndex));
        
        BasicInternalFrame currentFrame;
        Rectangle currentBounds;
        for (int i = 0; i < openFrames.size(); i++) {
            currentFrame = (BasicInternalFrame)openFrames.get(i);
            currentBounds = currentFrame.getNormalBounds();
            
            props.put(SETTINGS_PROPERTY_FRAMES_CLASS_NAME + i,
                      currentFrame.getClass().getName());
            props.put(SETTINGS_PROPERTY_FRAMES_INIT_PARAM + i,
                      currentFrame.getInitParam());
            props.put(SETTINGS_PROPERTY_FRAMES_X + i,
                      String.valueOf(currentBounds.x));
            props.put(SETTINGS_PROPERTY_FRAMES_Y + i,
                      String.valueOf(currentBounds.y));
            props.put(SETTINGS_PROPERTY_FRAMES_WIDTH + i,
                      String.valueOf(currentBounds.width));
            props.put(SETTINGS_PROPERTY_FRAMES_HEIGHT + i,
                      String.valueOf(currentBounds.height));
            props.put(SETTINGS_PROPERTY_FRAMES_MAXIMIZED + i,
                      String.valueOf(currentFrame.isMaximum()));
            props.put(SETTINGS_PROPERTY_FRAMES_ICONIFIED + i,
                      String.valueOf(currentFrame.isIcon()));
        }
        
    }
    
    private void loadWindowSettings(Properties props) {

        int windowX = getIntProperty(props, SETTINGS_PROPERTY_WINDOW_X, -1);
        int windowY = getIntProperty(props, SETTINGS_PROPERTY_WINDOW_Y, -1);
        int windowHeight = getIntProperty(props, SETTINGS_PROPERTY_WINDOW_HEIGHT, -1);
        int windowWidth = getIntProperty(props, SETTINGS_PROPERTY_WINDOW_WIDTH, -1);

        if (windowX > -1 && windowY  > -1) {
            setBounds(windowX,
                      windowY,
                      getSize().width,
                      getSize().height);
        }

        if (windowHeight  > -1 && windowWidth  > -1) {
            setSize(windowWidth, windowHeight);
        }
    }
    
    private void setupImagePath() {

        String basePath = System.getProperty(SYSTEM_PROPERTY_BASE_PATH);
        if (basePath != null) {
            if (basePath.endsWith(File.separator)) {
                basePath = basePath.substring(0, basePath.length() - 1);
            }
            imagePath=basePath + File.separator + IMAGES_DIRECTORY + File.separator;
        } else {
            imagePath = IMAGES_DIRECTORY + File.separator;
        }
    }

    private void setupActions() {

        actionNextWindow = new WindowAction("Next window");
        actionNextWindow.putValue(Action.SHORT_DESCRIPTION,
                                  "Cycle to the next opened window");
        actionNextWindow.setEnabled(false);
        
        actionPreviousWindow = new WindowAction("Previous window");
        actionPreviousWindow.putValue(Action.SHORT_DESCRIPTION,
                                     "Cycle to the previous opened window");
        actionPreviousWindow.setEnabled(false);
        
        actionTileWindows = new WindowAction("Tile windows");
        actionTileWindows.putValue(Action.SHORT_DESCRIPTION,
                                   "Tile all windows in the main frame");
        actionTileWindows.setEnabled(false);

        actionStackWindows = new WindowAction("Stack windows");
        actionStackWindows.putValue(Action.SHORT_DESCRIPTION,
                                    "Stack all windows in the main frame");
        actionStackWindows.setEnabled(false);
    }

    private void setupMenu() {

        menuWindow = new JMenu("Window");
            menuWindow.add(actionPreviousWindow).setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_F2, Event.CTRL_MASK));
            menuWindow.add(actionNextWindow).setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_F3, Event.CTRL_MASK));
            menuWindow.add(actionTileWindows);
            menuWindow.add(actionStackWindows);
    }

    private void setupFrame() {

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout(5,5));
        contentPane.add(buildDesktopPane(), BorderLayout.CENTER);
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                doQuit();
            }
        });
    }
    
    private JDesktopPane buildDesktopPane() {

        desktopPane = new JDesktopPane();
        desktopManager = createDesktopManager();
        desktopPane.setDesktopManager(desktopManager);
        
        return desktopPane;
    }
    
    private class WindowAction extends AbstractAction {

        public WindowAction(String name) {
            super(name);
        }

        public WindowAction(String name, Icon icon) {
            super(name, icon);
        }

        public void actionPerformed(ActionEvent ev) {

            if (this == actionPreviousWindow) {
                desktopManager.cycleToPreviousWindow();
            } else if (this == actionNextWindow) {
                desktopManager.cycleToNextWindow();
            } else if (this == actionTileWindows) {
                desktopManager.tileWindows();
            } else if (this == actionStackWindows) {
                desktopManager.stackWindows();
            }

        }
    }

}
