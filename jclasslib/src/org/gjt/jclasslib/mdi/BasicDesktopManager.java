/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.mdi;

import javax.swing.*;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.util.*;

/**
    <tt>DesktopManager</tt> for MDI application.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.7 $ $Date: 2003-08-18 07:59:50 $
*/
public class BasicDesktopManager extends DefaultDesktopManager
                                 implements VetoableChangeListener,
                                            InternalFrameListener
 {
    private static int NEW_INTERNAL_X_OFFSET = 22;
    private static int NEW_INTERNAL_Y_OFFSET = 22;
    private static int NEW_INTERNAL_WIDTH = 600;
    private static int NEW_INTERNAL_HEIGHT = 400;

    /** Parent frame of this <tt>DesktopManager</tt>. */
    protected BasicMDIFrame parentFrame;

    private int newInternalX = 0;
    private int newInternalY = 0;

    private JDesktopPane desktopPane;
    private HashMap frameToMenuItem = new HashMap();
    private BasicInternalFrame activeFrame;
    private LinkedList openFrames = new LinkedList();
    private int rollover = 0;
    private int separatorMenuIndex = -1;

    private boolean maximizationInProgress;
    private boolean anyFrameMaximized;

    /**
     * Constructor.
     * @param parentFrame the parent frame.
     */
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
        @param activeFrame the index
     */
    public void setActiveFrame(BasicInternalFrame activeFrame) {
        this.activeFrame = activeFrame;
    }

    /**
        Look for an open frame with an equivalent init parameter.
        @param initParam the init parameter to look for.
        @return the open frame or <tt>null</tt>.
     */
    public BasicInternalFrame getOpenFrame(Object initParam) {

        Iterator it = openFrames.iterator();
        while (it.hasNext()) {
            BasicInternalFrame frame = (BasicInternalFrame)it.next();
            if (frame.getInitParam().equals(initParam)) {
                return frame;
            }
        }
        return null;
    }

    /**
        Show all internal frames.
     */
    public void showAll() {
        Iterator it = openFrames.iterator();
        while (it.hasNext()) {
            ((BasicInternalFrame)it.next()).setVisible(true);
        }
        if (activeFrame != null) {
            try {
                activeFrame.setSelected(true);
            } catch (PropertyVetoException ex) {
            }
        }
        checkSize();
    }

    /**
        Add a child frame to this <tt>DesktopManager</tt>.
        @param frame the frame
     */
    public void addInternalFrame(JInternalFrame frame) {

        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                 checkSize();
            }

        });

        if (frameToMenuItem.size() == 0) {
            separatorMenuIndex = parentFrame.menuWindow.getMenuComponentCount();
            parentFrame.menuWindow.addSeparator();
        }
        Action action = new WindowActivateAction(frame);
        JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(action);
        menuItem.setSelected(false);
        parentFrame.menuWindow.add(menuItem);

        desktopPane.add(frame);
        frameToMenuItem.put(frame, menuItem);
        openFrames.add(frame);
        setWindowActionsEnabled(true);
        checkSize();
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

        resetSize();

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
        checkSize();
    }

    public void vetoableChange(PropertyChangeEvent changeEvent)
        throws PropertyVetoException {

        String eventName = changeEvent.getPropertyName();

        if (JInternalFrame.IS_MAXIMUM_PROPERTY.equals(eventName)) {
            if (maximizationInProgress) {
                return;
            }

            boolean isMaximum = ((Boolean)changeEvent.getNewValue()).booleanValue();
            if (isMaximum) {
                resetSize();
            }
            anyFrameMaximized = isMaximum;
            JInternalFrame source = (JInternalFrame)changeEvent.getSource();
            maximizeAllFrames(source, isMaximum);
        }
    }

    public void activateFrame(JInternalFrame frame) {
        super.activateFrame(frame);
        Iterator it = frameToMenuItem.values().iterator();
        while (it.hasNext()) {
            JCheckBoxMenuItem menuItem = (JCheckBoxMenuItem)it.next();
            menuItem.setSelected(false);

        }
        ((JCheckBoxMenuItem)frameToMenuItem.get(frame)).setSelected(true);
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
        checkSize();
    }

    public void endResizingFrame(JComponent f) {
        super.endResizingFrame(f);
        checkSize();
    }

    public void endDraggingFrame(JComponent f) {
        super.endDraggingFrame(f);
        checkSize();
    }

    /**
        Check if the desktop pane should be resized.
     */
    public void checkSize() {

        Dimension size = new Dimension();
        JInternalFrame[] frames = desktopPane.getAllFrames();
        for (int i = 0; i < frames.length; i++) {
            JInternalFrame frame = frames[i];
            size.width = Math.max(size.width, frame.getX() + frame.getWidth());
            size.height = Math.max(size.height, frame.getY() + frame.getHeight());
        }
        if (size.width > 0 && size.height > 0) {
            desktopPane.setPreferredSize(size);
        } else {
            desktopPane.setPreferredSize(null);
        }
        desktopPane.revalidate();
    }

    /**
        Check whether the desktop pane must be resized if in the maximized state.
     */
    public void checkResizeInMaximizedState() {
        if (anyFrameMaximized) {
            resetSize();
        }
    }

    /**
        Scroll the destop pane such that the given frame becoes fully visible.
        @param frame the frame.
     */
    public void scrollToVisible(JInternalFrame frame) {
        desktopPane.scrollRectToVisible(frame.getBounds());
    }

    private void removeInternalFrame(JInternalFrame frame) {

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

    private void resetSize() {

        desktopPane.setPreferredSize(null);
        desktopPane.invalidate();
        desktopPane.getParent().validate();
        parentFrame.scpDesktop.invalidate();
        parentFrame.scpDesktop.validate();
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
        JInternalFrame nextFrame;

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
            scrollToVisible(nextFrame);
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

    private class WindowActivateAction extends AbstractAction {

        private JInternalFrame frame;

        private WindowActivateAction(JInternalFrame frame) {
            super(frame.getTitle());
            this.frame = frame;
        }

        public void actionPerformed(ActionEvent event) {
            try {
                if (frame.isIcon()) {
                    frame.setIcon(false);
                }
                if (frame.isSelected()) {
                    ((JCheckBoxMenuItem)event.getSource()).setSelected(true);
                } else {
                    frame.setSelected(true);
                }
                scrollToVisible(frame);
            } catch (PropertyVetoException ex) {
            }
        }

    }
}
