/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.mdi;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Rectangle;
import java.beans.*;
import java.util.*;

/**
    <tt>DesktopManager</tt> for MDI application.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.5 $ $Date: 2002-02-27 16:47:43 $
*/
public class BasicDesktopManager extends DefaultDesktopManager
                                 implements VetoableChangeListener,
                                            InternalFrameListener {

    /** Relative x offset of a new child window */
    protected static int NEW_INTERNAL_X_OFFSET = 22;
    /** Relative y offset of a new child window */
    protected static int NEW_INTERNAL_Y_OFFSET = 22;

    /** Default width of a new child window */
    protected static int NEW_INTERNAL_WIDTH = 600;
    /** Default height of a new child window */
    protected static int NEW_INTERNAL_HEIGHT = 400;
    
    /** Parent frame of this <tt>DesktopManager</tt> */
    protected BasicMDIFrame parentFrame;
    /**  Associated <tt>JDesktopPane</tt> of this <tt>DesktopManager</tt> */
    protected JDesktopPane desktopPane;

    /** Current x offset for new child windows */
    protected int newInternalX = 0;
    /** Current y offset for new child windows */
    protected int newInternalY = 0;
    /** Rollover counter for y offsets */
    protected int rollover = 0;
    
    /**
        Records whether a maximization in progress or not. When one frame
        is maximized, all other frames are also maximized and must be 
        aware of this process
     */
    protected boolean maximizationInProgress;
    /** Map connecting frames to window menu items */
    protected HashMap frameToMenuItem = new HashMap();
    /** List of open child frames */
    protected LinkedList openFrames = new LinkedList();
    /** Menu index fo separator in the window menu*/
    protected int separatorMenuIndex = -1;
    /** the index of the frame to be shown on top after a call to <tt>showAll()</tt> */
    protected int activeFrameIndex = -1;
    
    public BasicDesktopManager(BasicMDIFrame parentFrame) {
        this.parentFrame = parentFrame;
        desktopPane = parentFrame.desktopPane;
    }

    /**
        Get the parent frame.
        @return the frame
     */
    public BasicMDIFrame getParentFrame() {
        return parentFrame;
    }
    
    /**
        Get the associated <tt>JDesktopPane</tt>.
        @return the <tt>JDesktopPane</tt>
     */
    public JDesktopPane getDesktopPane() {
        return desktopPane;
    }
    
    /**
        Get the list of open child frames.
        @return the list
     */
    public java.util.List getOpenFrames() {
        return openFrames;
    }
    
    /**
        Get a rectangle for a new child frame.
        @return the rectangle
     */
    public Rectangle getNextInternalFrameBounds() {
        
        if (newInternalY + NEW_INTERNAL_HEIGHT > desktopPane.getHeight()) {
            rollover++;
            newInternalY = 0;
            newInternalX = rollover * NEW_INTERNAL_X_OFFSET;
        }
        
        Rectangle nextBounds = new Rectangle(newInternalX,
                             newInternalY,
                             NEW_INTERNAL_WIDTH,
                             NEW_INTERNAL_HEIGHT);
        
        newInternalX += NEW_INTERNAL_X_OFFSET;
        newInternalY += NEW_INTERNAL_Y_OFFSET;

        return nextBounds;
    }
    
    /**
        Set the index of the frame to be shown on top after a call to <tt>showAll()</tt>.
        @param activeFrameIndex the index
     */
    public void setActiveFrameIndex(int activeFrameIndex) {
        this.activeFrameIndex = activeFrameIndex;
    }
    
    /**
        Show all internal frames.
     */
    public void showAll() {
        Iterator it = openFrames.iterator();
        while (it.hasNext()) {
            ((BasicInternalFrame)it.next()).setVisible(true);
        }
        if (activeFrameIndex > -1) {
            JInternalFrame activeFrame = (JInternalFrame)openFrames.get(activeFrameIndex);
            try {
                activeFrame.setSelected(true);
            } catch (PropertyVetoException ex) {
            }
        }
    }
    
    /**
        Add a child frame to this <tt>DesktopManager</tt>.
        @param frame the frame
     */
    public void addInternalFrame(JInternalFrame frame) {
        
        if (frameToMenuItem.size() == 0) {
            separatorMenuIndex = parentFrame.menuWindow.getMenuComponentCount();
            parentFrame.menuWindow.addSeparator();
        }
        Action action = new WindowActivateAction(frame);
        JMenuItem menuItem = parentFrame.menuWindow.add(action);
        
        desktopPane.add(frame);
        frameToMenuItem.put(frame, menuItem);
        openFrames.add(frame);
        setWindowActionsEnabled(true);
    }

    /**
        Remove a child frame from this <tt>DesktopManager</tt>.
        @param frame the frame
     */
    public void removeInternalFrame(JInternalFrame frame) {

        JMenuItem menuItem = (JMenuItem)frameToMenuItem.remove(frame);
        if (menuItem != null) {
            parentFrame.menuWindow.remove(menuItem);
            openFrames.remove(frame);
            if (frameToMenuItem.size() == 0 && separatorMenuIndex > -1) {
                parentFrame.menuWindow.remove(separatorMenuIndex);
                separatorMenuIndex = -1;
                setWindowActionsEnabled(false);
            }
        }
    }
    
    
    /**
        Cycle to the next child window.
     */
    public void cycleToNextWindow() {
        cycleWindows(true);
    }
    
    /**
        Cycle to the previous child window.
     */
    public void cycleToPreviousWindow() {
        cycleWindows(false);
    }
    
    /**
        Tile all child windows.
     */
    public void tileWindows() {
        
        int framesCount = openFrames.size();
        if (framesCount == 0) {
            return;
        }
        
        int sqrt = (int)Math.sqrt(framesCount);
        int rows = sqrt;
        int cols = sqrt;
        if (rows * cols < framesCount) {
            cols++;
            if (rows * cols < framesCount) {
                rows++;
            }
        }
        
        Dimension size = desktopPane.getSize();
        
        int width = size.width/cols;
        int height = size.height/rows;
        int offsetX = 0;
        int offsetY = 0;
        
        JInternalFrame currentFrame;
        Iterator it = openFrames.iterator();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols && (i * cols + j < framesCount); j++) {
                currentFrame = (JInternalFrame)it.next();
                normalizeFrame(currentFrame);
                resizeFrame(currentFrame, offsetX, offsetY, width, height);
                offsetX += width;
            }
            offsetX = 0;
            offsetY += height;
        }
    }

    /**
        Stack all child windows.
     */
    public void stackWindows() {
        
        newInternalX = newInternalY = rollover = 0;

        Rectangle currentBounds;
        JInternalFrame currentFrame;
        Iterator it = openFrames.iterator();
        while (it.hasNext()) {
            currentFrame = (JInternalFrame)it.next();
            normalizeFrame(currentFrame);
            currentBounds = getNextInternalFrameBounds();
            resizeFrame(currentFrame, 
                        currentBounds.x,
                        currentBounds.y,
                        currentBounds.width,
                        currentBounds.height);
            try {
                currentFrame.setSelected(true);
            } catch (PropertyVetoException ex) {
            }
        }

    }
    
    private void normalizeFrame(JInternalFrame frame) {

        try {
            if (frame.isIcon()) {
                frame.setIcon(false);
            }
            if (frame.isMaximum()) {
                frame.setMaximum(false);
            }
        } catch (PropertyVetoException ex) {
        }
    }
    
    private void cycleWindows(boolean forward) {

        JInternalFrame currentFrame = desktopPane.getSelectedFrame();
        JInternalFrame nextFrame = null;
        
        ListIterator it = openFrames.listIterator();
        while (it.hasNext() && it.next() != currentFrame) {
        }
        if (forward) {
            if (it.hasNext()) {
                nextFrame = (JInternalFrame)it.next();
            } else {
                nextFrame = (JInternalFrame)openFrames.getFirst();
            }
        } else {
            if (it.hasPrevious() && it.previous() != null && it.hasPrevious()) {
                nextFrame = (JInternalFrame)it.previous();
            } else {
                nextFrame = (JInternalFrame)openFrames.getLast();
            }
        }
        
        try {
            if (nextFrame.isIcon()) {
                nextFrame.setIcon(false);
            }
            nextFrame.setSelected(true);
        } catch (PropertyVetoException ex) {
        }
    }
    
    private void setWindowActionsEnabled(boolean enabled) {
        
        parentFrame.actionNextWindow.setEnabled(enabled);
        parentFrame.actionPreviousWindow.setEnabled(enabled);
        parentFrame.actionTileWindows.setEnabled(enabled);
        parentFrame.actionStackWindows.setEnabled(enabled);
    }
    
    private void maximizeAllFrames(JInternalFrame source, boolean isMaximum) {

        synchronized (this) {
            if (maximizationInProgress) {
                return;
            }
            maximizationInProgress = true;
        }

        try {
            JInternalFrame[] frames = desktopPane.getAllFrames();
            for (int i = 0; i < frames.length; i++) {
                if (frames[i] == source) {
                    continue;
                }
                try {
                    frames[i].setMaximum(isMaximum);
                } catch (PropertyVetoException ex) {
                }
            }
        } finally {
            maximizationInProgress = false;
        }
    }

    public void vetoableChange(PropertyChangeEvent changeEvent)
        throws PropertyVetoException {
        
        String eventName = changeEvent.getPropertyName();

        if (JInternalFrame.IS_MAXIMUM_PROPERTY.equals(eventName)) {
            if (maximizationInProgress) {
                return;
            }
            
            boolean isMaximum = ((Boolean)changeEvent.getNewValue()).booleanValue();
            JInternalFrame source = (JInternalFrame)changeEvent.getSource();
            maximizeAllFrames(source, isMaximum);
        }
    }    
    
    public void internalFrameDeiconified(InternalFrameEvent event) {
    }
    
    public void internalFrameOpened(InternalFrameEvent event) {
    }
    
    public void internalFrameIconified(InternalFrameEvent event) {
    }
    
    public void internalFrameClosing(InternalFrameEvent event) {
        JInternalFrame frame = event.getInternalFrame();
        removeInternalFrame(frame);
    }
    
    public void internalFrameActivated(InternalFrameEvent event) {
    }
    
    public void internalFrameDeactivated(InternalFrameEvent event) {
    }
    
    public void internalFrameClosed(InternalFrameEvent event) {
        parentFrame.desktopPane.remove(event.getInternalFrame());
    }
    
    private class WindowActivateAction extends AbstractAction {

        private JInternalFrame frame;
        
        public WindowActivateAction(JInternalFrame frame) {
            super(frame.getTitle());
            this.frame = frame;
        }

        public void actionPerformed(ActionEvent ev) {
            try {
                if (frame.isIcon()) {
                    frame.setIcon(false);
                }
                frame.setSelected(true);
            } catch (PropertyVetoException ex) {
            }
        }
        
    }
}
