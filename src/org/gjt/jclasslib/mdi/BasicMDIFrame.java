/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.mdi;

import org.gjt.jclasslib.util.GUIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyVetoException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.prefs.Preferences;


/**
    Parent frame for MDI application. Handles window actions, state saving and loading
    and supplies various utility methods.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.8 $ $Date: 2004-06-29 15:41:44 $
*/
public class BasicMDIFrame extends JFrame {

    private static final int DEFAULT_WINDOW_WIDTH = 800;
    private static final int DEFAULT_WINDOW_HEIGHT = 600;

    private static final String SETTINGS_WINDOW_WIDTH = "windowWidth";
    private static final String SETTINGS_WINDOW_HEIGHT = "windowHeight";
    private static final String SETTINGS_WINDOW_X = "windowX";
    private static final String SETTINGS_WINDOW_Y = "windowY";
    private static final String SETTINGS_WINDOW_MAXIMIZED = "windowMaximized";

    // Actions
    
    /** Action for selecting the next child window. */
    protected Action actionNextWindow;
    /** Action for selecting the previous child window. */
    protected Action actionPreviousWindow;
    /** Action for tiling all child windows. */
    protected Action actionTileWindows;
    /** Action for stacking all child windows. */
    protected Action actionStackWindows;

    // Visual components

    /** <tt>JDesktop</tt> pane which contains all child windows. */
    protected JScrollPane scpDesktop;
    /** The desktop pane. */
    protected JDesktopPane desktopPane;
    /** <tt>DesktopManager</tt> for this MDI parent frame. */
    protected BasicDesktopManager desktopManager;    
    /** <tt>JMenu</tt> for window actions. */
    protected JMenu menuWindow;

    private Rectangle lastNormalFrameBounds;

    /**
        Constructor.
     */
    public BasicMDIFrame() {

        setupActions();
        setupMenu();
        setupFrame();
        setupEventHandlers();
        loadWindowSettings();
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

        saveWindowSettings();
        dispose();
        System.exit(0);
    }

    /**
        Close all internal frames.
     */
    protected void closeAllFrames() {

        List frames = desktopManager.getOpenFrames();
        while (frames.size() > 0) {
            BasicInternalFrame frame = (BasicInternalFrame)frames.get(0);
            frame.doDefaultCloseAction();
        }
    }

    /**
         Create an <tt>MDIConfig</tt> object that describes the current configuration of
         all internal frames. This object can be serialized and reactivated with
         <tt>readMDIConfig</tt>.
         @return the <tt>MDIConfig</tt> object
     */
    protected MDIConfig createMDIConfig() {

        MDIConfig config = new MDIConfig();
        java.util.List openFrames = desktopManager.getOpenFrames();
        List internalFrameDescs = new ArrayList(openFrames.size());

        for (int i = 0; i < openFrames.size(); i++) {

            BasicInternalFrame internalFrame = (BasicInternalFrame)openFrames.get(i);

            Rectangle bounds = internalFrame.getNormalBounds();
            MDIConfig.InternalFrameDesc internalFrameDesc = new MDIConfig.InternalFrameDesc();
            internalFrameDesc.setClassName(internalFrame.getClass().getName());
            internalFrameDesc.setInitParam(internalFrame.getInitParam());
            internalFrameDesc.setX(bounds.x);
            internalFrameDesc.setY(bounds.y);
            internalFrameDesc.setWidth(bounds.width);
            internalFrameDesc.setHeight(bounds.height);
            internalFrameDesc.setMaximized(internalFrame.isMaximum());
            internalFrameDesc.setIconified(internalFrame.isIcon());

            if (internalFrame == desktopPane.getSelectedFrame()) {
                config.setActiveFrameDesc(internalFrameDesc);
            }
            internalFrameDescs.add(internalFrameDesc);

        }
        config.setInternalFrameDescs(internalFrameDescs);

        return config;
    }

    /**
         Takes an <tt>MDIConfig</tt> object that describes a configuration of
         internal frames and populates the MDI frame with this configuration.
         @param config the <tt>MDIConfig</tt> object to be read
     */
    protected void readMDIConfig(MDIConfig config) {

        boolean anyFrameMaximized = false;
        Iterator it = config.getInternalFrameDescs().iterator();
        while (it.hasNext()) {
            MDIConfig.InternalFrameDesc internalFrameDesc = (MDIConfig.InternalFrameDesc)it.next();


            Constructor frameConstructor;
            try {
                Class frameClass = Class.forName(internalFrameDesc.getClassName());
                frameConstructor = frameClass.getConstructor(getFrameConstructorArguments(frameClass));
            } catch (ClassNotFoundException ex) {
                System.out.println("class not found:" + ex.getMessage());
                continue;
            } catch (NoSuchMethodException ex) {
                System.out.println("constructor not found:" + ex.getMessage());
                continue;
            }

            BasicInternalFrame frame;
            try {
                frame = (BasicInternalFrame)frameConstructor.newInstance(new Object[] {desktopManager, internalFrameDesc.getInitParam()});
            } catch (Exception ex) {
                ex.printStackTrace();
                Throwable cause = ex.getCause();
                if (cause != null) {
                    ex.printStackTrace();
                }
                continue;
            }
            desktopManager.resizeFrame(
                    frame,
                    internalFrameDesc.getX(),
                    internalFrameDesc.getY(),
                    internalFrameDesc.getWidth(),
                    internalFrameDesc.getHeight()
            );

            boolean frameMaximized = internalFrameDesc.isMaximized();
            anyFrameMaximized = anyFrameMaximized || frameMaximized;

            try {
                if (frameMaximized || anyFrameMaximized) {
                    frame.setMaximum(true);
                } else if (internalFrameDesc.isIconified()) {
                    frame.setIcon(true);
                }
            } catch (PropertyVetoException ex) {
            }

            if (internalFrameDesc == config.getActiveFrameDesc()) {
                desktopManager.setActiveFrame(frame);
            }
        }

        desktopManager.showAll();
    }

    /**
        Get the constructor arguments classes for the constructor of the supplied frame class.
        @param frameClass the frame class.
        @return the constructor argument classes.
     */
    protected Class[] getFrameConstructorArguments(Class frameClass) {
        return BasicInternalFrame.CONSTRUCTOR_ARGUMENTS;
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
        contentPane.add(buildDesktop(), BorderLayout.CENTER);
        
    }

    private void setupEventHandlers() {

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent event) {
                doQuit();
            }
        });

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                desktopManager.checkResizeInMaximizedState();
                recordLastNormalFrameBounds();
            }
            public void componentMoved(ComponentEvent event) {
                recordLastNormalFrameBounds();
            }
        });

    }

    private void saveWindowSettings() {

        Preferences preferences = Preferences.userNodeForPackage(getClass());

        boolean maximized = (getExtendedState() & MAXIMIZED_BOTH) != 0;
        preferences.putBoolean(SETTINGS_WINDOW_MAXIMIZED, maximized);
        Rectangle frameBounds = maximized ? lastNormalFrameBounds : getBounds();

        if (frameBounds != null) {
            preferences.putInt(SETTINGS_WINDOW_WIDTH, frameBounds.width);
            preferences.putInt(SETTINGS_WINDOW_HEIGHT, frameBounds.height);
            preferences.putInt(SETTINGS_WINDOW_X, frameBounds.x);
            preferences.putInt(SETTINGS_WINDOW_Y, frameBounds.y);
        }
    }

    private void loadWindowSettings() {

        Preferences preferences = Preferences.userNodeForPackage(getClass());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenBounds = new Rectangle(screenSize);

        int windowX = preferences.getInt(SETTINGS_WINDOW_X, (int)(screenSize.getWidth() - DEFAULT_WINDOW_WIDTH)/2);
        int windowY = preferences.getInt(SETTINGS_WINDOW_Y, (int)(screenSize.getHeight() - DEFAULT_WINDOW_HEIGHT)/2);
        int windowWidth = preferences.getInt(SETTINGS_WINDOW_WIDTH, DEFAULT_WINDOW_WIDTH);
        int windowHeight = preferences.getInt(SETTINGS_WINDOW_HEIGHT, DEFAULT_WINDOW_HEIGHT);

        Rectangle frameBounds = new Rectangle(windowX, windowY, windowWidth, windowHeight);

        // sanitize frame bounds
        frameBounds.translate(-Math.min(0, frameBounds.x), -Math.min(0, frameBounds.y));
        frameBounds.translate(-Math.max(0, frameBounds.x + frameBounds.width - screenSize.width), -Math.max(0, frameBounds.y + frameBounds.height- screenSize.height));
        frameBounds = screenBounds.intersection(frameBounds);

        setBounds(frameBounds);

        if (preferences.getBoolean(SETTINGS_WINDOW_MAXIMIZED, false)) {
            setExtendedState(MAXIMIZED_BOTH);
        }

    }

    private void recordLastNormalFrameBounds() {
        if ((getExtendedState() & MAXIMIZED_BOTH) == 0) {
            Rectangle frameBounds = getBounds();
            if (frameBounds.getX() >= 0 && frameBounds.getY() >= 0) {
                lastNormalFrameBounds = frameBounds;
            }
        }

    }

    private JComponent buildDesktop() {

        desktopPane = new JDesktopPane();
        desktopPane.setBackground(Color.LIGHT_GRAY);
        desktopManager = createDesktopManager();
        desktopPane.setDesktopManager(desktopManager);
        scpDesktop = new JScrollPane(desktopPane);
        GUIHelper.setDefaultScrollbarUnits(scpDesktop);

        return scpDesktop;
    }
    
    private class WindowAction extends AbstractAction {

        private WindowAction(String name) {
            super(name);
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
