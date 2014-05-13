/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.mdi;

import javax.swing.*;

/**
    Child frame for MDI application.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.5 $ $Date: 2003-08-18 07:59:50 $
*/
public abstract class BasicInternalFrame extends JInternalFrame {

    /**
        Constructor for creating a derived <tt>BasicInternalFrame</tt> with
        an initialization parameter.
     */
    public static final Class[] CONSTRUCTOR_ARGUMENTS =
        new Class[] {BasicDesktopManager.class, String.class};
    
    /** The parent <tt>DesktopManager</tt>. */
    protected final BasicDesktopManager desktopManager;

    /**
        Constructor.
        @param desktopManager the associated desktop manager.
        @param title the frame title.
     */
    protected BasicInternalFrame(BasicDesktopManager desktopManager, String title) {
        super(title, true, true, true, true);
        this.desktopManager = desktopManager;
    }
    
    /**
        Get the initialization parameter used for storing the state of the frame.
        This parameter will be supplied to the constructor when the state is restored.
        @return the parameter
     */
    public Object getInitParam() {
        return null;
    }

    /**
        Setup the internal frame. Has to be called by derived classes.
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
