/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.mdi;

import org.gjt.jclasslib.util.*;

import javax.swing.*;
import java.beans.*;
import java.util.*;

/**
    Child frame for MDI application.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2002-02-18 12:44:32 $
*/
public class BasicInternalFrame extends JInternalFrame {

    /**
        Constructor for creating a <tt>BasicInternalFrame</tt> for
        state loading
     */
    public static final Class[] DEFAULT_CONSTRUCTOR =
        new Class[] {BasicDesktopManager.class, String.class};
    
    /** The parent <tt>DesktopManager</tt> */
    protected final BasicDesktopManager desktopManager;

    /** 
        The list of <tt>MaximizedListeners</tt> listening for maximizations
        of this child frame
     */
    protected LinkedList listeners = new LinkedList();
    
    public BasicInternalFrame(BasicDesktopManager desktopManager, String title) {
        super(title, true, true, true, true);
        this.desktopManager = desktopManager;
    }
    
    /** 
        Get the initialization parameter used for storing the state of the frame.
        This parameter will be supplied to the cinstructor when the state is restored.
        @return the parameter
     */
    public String getInitParam() {
        return "";
    }
    
    /** 
        Add a <tt>MaximizedListener</tt> listening for maximizations
        of this child frame.
        @param listener the listener
     */
    public void addMaximizedListener(MaximizedListener listener) {
        listeners.add(listener);
    }

    /** 
        Remove a <tt>MaximizedListener</tt> listening for maximizations
        of this child frame.
        @param listener the listener
     */
    public void removeMaximizedListener(MaximizedListener listener) {
        listeners.remove(listener);
    }
    
    public void setMaximum(boolean maximum) throws PropertyVetoException {
        super.setMaximum(maximum);
        
        if (maximum) {
            Iterator it = listeners.iterator();
            MaximizedEvent event = new MaximizedEvent(this);
            while (it.hasNext()) {
                MaximizedListener listener = (MaximizedListener)it.next();
                listener.frameMaximized(event);
            }
        }
    }
    
    /**
        Setup the internal frame. To be overridden by derived classes.
        Call <tt>super()</tt> after the initialization in the
        the dreived class is completed.
     */
    protected void setupInternalFrame() {

        setBounds(desktopManager.getNextInternalFrameBounds());

        addVetoableChangeListener(desktopManager);
        addInternalFrameListener(desktopManager);
        desktopManager.addInternalFrame(this);

        if (desktopManager.getParentFrame().isVisible()) {
            setVisible(true);
        }
    }
    
}
