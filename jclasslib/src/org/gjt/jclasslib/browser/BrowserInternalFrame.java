/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.mdi.*;
import org.gjt.jclasslib.io.*;
import org.gjt.jclasslib.structures.*;

import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.*;

/**
    A child window of the class file browser application.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.4 $ $Date: 2002-02-27 16:47:42 $
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
            exception = ex;
        } catch (IOException ex) {
            exception = ex;
        }
    }
    
}
