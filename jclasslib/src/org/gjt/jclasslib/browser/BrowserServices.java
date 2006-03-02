/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.browser.config.window.BrowserPath;
import org.gjt.jclasslib.structures.ClassFile;

import javax.swing.*;

/**
    Container services for a <tt>BrowserComponent</tt>.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.6 $ $Date: 2006-03-02 11:42:37 $
*/
public interface BrowserServices {
    /**
        Get the <tt>ClassFile</tt> object for the show class file.
        @return the <tt>ClassFile</tt> object
     */
    ClassFile getClassFile();

    /**
        Activate this view.
     */
    void activate();
    
    /**
        Get the <tt>BrowserComponent</tt> which is the top parent of
        the visual hierarchy of the class file browser.
        @return the <tt>BrowserComponent</tt>
     */
    BrowserComponent getBrowserComponent();
    
    /**
        Get the <tt>Action</tt> which allows to move
        backward in the navigation history.
        @return the <tt>Action</tt>
     */
    Action getActionBackward();

    /**
        Get the <tt>Action</tt> which allows to move
        forward in the navigation history.
        @return the <tt>Action</tt>
     */
    Action getActionForward();

    /**
        Open a class.
        @param className the name of the class.
        @param browserPath an optional <tt>BrowserPath</tt> object. May be <tt>null</tt>.
     */
    void openClassFile(String className, BrowserPath browserPath);

    /**
        Indicates whether <tt>openClassFile</tt> will be able to show class files or not.
        @return the value
     */

    boolean canOpenClassFiles();

    /**
        Show a URL in the browser.
        @param urlSpec the URL as a browser
     */
    void showURL(String urlSpec);
}

