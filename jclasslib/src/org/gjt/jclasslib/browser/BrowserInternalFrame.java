/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.io.ClassFileReader;
import org.gjt.jclasslib.mdi.BasicDesktopManager;
import org.gjt.jclasslib.mdi.BasicInternalFrame;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
    A child window of the class file browser application.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.6 $ $Date: 2003-07-08 14:04:27 $
*/
public class BrowserInternalFrame extends BasicInternalFrame
                                  implements BrowserServices {


    private File file;
    private ClassFile classFile;

    private boolean valid;
    private Exception exception;

    // Visual Components

    private BrowserComponent browserComponent;

    public BrowserInternalFrame(BasicDesktopManager desktopManager, String fileName) {
        this(desktopManager, new File(fileName));
    }

    public BrowserInternalFrame(BasicDesktopManager desktopManager, File file) {
        super(desktopManager, file.getAbsolutePath());
        this.file = file;

        readClassFile();
        setupInternalFrame();
    }

    public String getInitParam() {
        return file.getAbsolutePath();
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
        return getParentFrame().actionBackward;
    }

    public Action getActionForward() {
        return getParentFrame().actionForward;
    }

    /**
        Reload class file.
     */
    public void reload() {
        readClassFile();
        browserComponent.rebuild();
    }

    /**
        Get the <tt>File</tt> object for the show class file.
        @return the <tt>File</tt> object
     */
    public File getFile() {
        return file;
    }

    protected void setupInternalFrame() {

        setTitle(file.getAbsolutePath());

        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        browserComponent = new BrowserComponent(this);
        contentPane.add(browserComponent, BorderLayout.CENTER);

        super.setupInternalFrame();

    }

    private BrowserMDIFrame getParentFrame() {
        return (BrowserMDIFrame)desktopManager.getParentFrame();
    }

    private void readClassFile() {
        valid = false;
        try {
            classFile = ClassFileReader.readFromFile(file);
            valid = true;
        } catch (InvalidByteCodeException ex) {
			ex.printStackTrace();
            exception = ex;
        } catch (IOException ex) {
			ex.printStackTrace();
            exception = ex;
        }
    }

}
