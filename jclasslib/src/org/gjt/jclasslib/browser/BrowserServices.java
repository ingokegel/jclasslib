/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.structures.*;
import org.gjt.jclasslib.util.*;

import javax.swing.Action;

/**
    Container services for a <tt>BrowserComponent</tt>.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2002-02-27 16:47:42 $
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
        Add a <tt>MaximizedListener</tt> listening for maximizations
        of the parent frame if applicable.
        @param listener the listener
     */
    void addMaximizedListener(MaximizedListener listener);
    
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
}

