/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.browser.config.classpath.FindResult;
import org.gjt.jclasslib.browser.config.window.BrowserPath;
import org.gjt.jclasslib.browser.config.window.WindowState;
import org.gjt.jclasslib.io.ClassFileReader;
import org.gjt.jclasslib.mdi.BasicDesktopManager;
import org.gjt.jclasslib.mdi.BasicInternalFrame;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.util.GUIHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * A child window of the class file browser application.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
 * @version $Revision: 1.10 $ $Date: 2006-03-02 11:42:36 $
 */
public class BrowserInternalFrame extends BasicInternalFrame
        implements BrowserServices {


    /**
     * Constructor for creating a derived <tt>BasicInternalFrame</tt> with
     * an initialization parameter.
     */
    public static final Class[] CONSTRUCTOR_ARGUMENTS =
            new Class[]{BasicDesktopManager.class, WindowState.class};

    private String fileName;
    private ClassFile classFile;

    // Visual Components

    private BrowserComponent browserComponent;

    /**
     * Constructor.
     *
     * @param desktopManager the associated desktop manager
     * @param windowState    the window state object. The frame will load the class file from
     *                       information present within this object.
     */
    public BrowserInternalFrame(BasicDesktopManager desktopManager, WindowState windowState) {
        super(desktopManager, windowState.getFileName());
        this.fileName = windowState.getFileName();

        setFrameIcon(BrowserMDIFrame.ICON_APPLICATION);
        readClassFile();
        setupInternalFrame(windowState.getBrowserPath());
    }

    public Object getInitParam() {
        WindowState windowState = new WindowState(fileName, browserComponent.getBrowserPath());
        return windowState;
    }

    // Browser services

    public ClassFile getClassFile() {
        return classFile;
    }

    public void activate() {

        // force sync of toolbar state with this frame
        desktopManager.getDesktopPane().setSelectedFrame(this);
    }

    public BrowserComponent getBrowserComponent() {
        return browserComponent;
    }

    public Action getActionBackward() {
        return getParentFrame().getActionBackward();
    }

    public Action getActionForward() {
        return getParentFrame().getActionForward();
    }

    public void openClassFile(String className, BrowserPath browserPath) {

        FindResult findResult = getParentFrame().getConfig().findClass(className);
        while (findResult == null) {
            int result = GUIHelper.showOptionDialog(getParentFrame(),
                    "The class " + className + " could not be found.\n" +
                    "You can check your classpath configuration and try again.",
                    new String[]{"Setup classpath", "Cancel"},
                    JOptionPane.WARNING_MESSAGE);
            if (result == 0) {
                getParentFrame().getActionSetupClasspath().actionPerformed(new ActionEvent(this, 0, null));
                findResult = getParentFrame().getConfig().findClass(className);
            } else {
                return;
            }
        }

        BrowserInternalFrame frame = (BrowserInternalFrame)desktopManager.getOpenFrame(new WindowState(findResult.getFileName()));
        if (frame != null) {
            try {
                frame.setSelected(true);
                frame.browserComponent.setBrowserPath(browserPath);
                desktopManager.scrollToVisible(frame);
            } catch (PropertyVetoException e) {
            }
        } else {
            WindowState windowState = new WindowState(findResult.getFileName(), browserPath);
            frame = new BrowserInternalFrame(desktopManager, windowState);
            if (isMaximum()) {
                try {
                    frame.setMaximum(true);
                } catch (PropertyVetoException ex) {
                }
            } else {
                desktopManager.scrollToVisible(frame);
            }
        }
    }

    public boolean canOpenClassFiles() {
        return true;
    }

    public void showURL(String urlSpec) {
        GUIHelper.showURL(urlSpec);
    }

    /**
     * Reload class file.
     */
    public void reload() {
        readClassFile();
        browserComponent.rebuild();
    }

    /**
     * Get the file name for the displayed class file.
     *
     * @return the file name
     */
    public String getFileName() {
        return fileName;
    }

    private void setupInternalFrame(BrowserPath browserPath) {

        setTitle(fileName);

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        browserComponent = new BrowserComponent(this);
        contentPane.add(browserComponent, BorderLayout.CENTER);

        setupInternalFrame();
        browserComponent.setBrowserPath(browserPath);

    }

    private BrowserMDIFrame getParentFrame() {
        return (BrowserMDIFrame)desktopManager.getParentFrame();
    }

    private void readClassFile() {
        try {
            int index = fileName.indexOf('!');
            if (index > -1) {
                String jarFileName = fileName.substring(0, index);
                String classFileName = fileName.substring(index + 1);
                JarFile jarFile = new JarFile(jarFileName);
                JarEntry jarEntry = jarFile.getJarEntry(classFileName);
                if (jarEntry != null) {
                    classFile = ClassFileReader.readFromInputStream(jarFile.getInputStream(jarEntry));
                }
            } else {
                classFile = ClassFileReader.readFromFile(new File(fileName));
            }
        } catch (InvalidByteCodeException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
