/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import org.gjt.jclasslib.mdi.BasicDesktopManager;

import javax.swing.event.InternalFrameEvent;

/**
    The desktop manager for the class file browser application.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.5 $ $Date: 2003-07-08 14:04:27 $
*/
public class BrowserDesktopManager extends BasicDesktopManager {

    public BrowserDesktopManager(BrowserMDIFrame parentFrame) {
        super(parentFrame);
    }

    public void internalFrameActivated(InternalFrameEvent event) {
        BrowserInternalFrame internalFrame = (BrowserInternalFrame)event.getInternalFrame();
        actionStatus(internalFrame);
        internalFrame.getBrowserComponent().checkSelection();
    }

    public void internalFrameDeactivated(InternalFrameEvent event) {
        actionStatus(null);
    }
    
    
    private void actionStatus(BrowserInternalFrame internalFrame) {
        
        BrowserMDIFrame browserParentFrame = (BrowserMDIFrame)parentFrame;
        
        if (internalFrame != null) {
            internalFrame.getBrowserComponent().getHistory().updateActions();
        } else {
            browserParentFrame.actionReload.setEnabled(false);
            browserParentFrame.actionBackward.setEnabled(false);
            browserParentFrame.actionForward.setEnabled(false);
        }
        browserParentFrame.actionReload.setEnabled(internalFrame != null);
    }
}
