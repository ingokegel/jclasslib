/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser;

import java.awt.Dimension
import java.awt.Rectangle
import java.awt.event.ActionEvent
import java.awt.event.ComponentEvent
import java.beans.PropertyChangeEvent
import java.util.*
import javax.swing.*
import javax.swing.event.InternalFrameEvent

/**
    The desktop manager for the class file browser application.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class BrowserDesktopManager extends DefaultDesktopManager implements VetoableChangeListener, InternalFrameListener {

    private static int NEW_INTERNAL_X_OFFSET = 22;
    private static int NEW_INTERNAL_Y_OFFSET = 22;
    private static int NEW_INTERNAL_WIDTH = 600;
    private static int NEW_INTERNAL_HEIGHT = 400;

    /** Parent frame of this <tt>DesktopManager</tt>. */
    protected BrowserMDIFrame parentFrame;

    private int newInternalX = 0;
    private int newInternalY = 0;

    private HashMap<JInternalFrame, JCheckBoxMenuItem> frameToMenuItem = new HashMap<JInternalFrame, JCheckBoxMenuItem>();
    private BrowserInternalFrame activeFrame;
    private LinkedList<BrowserInternalFrame> openFrames = new LinkedList<BrowserInternalFrame>();
    private int rollover = 0;
    private int separatorMenuIndex = -1;

    private boolean maximizationInProgress;
    private boolean anyFrameMaximized;
    /**
        Constructor.
        @param parentFrame the parent frame
     */
    public BrowserDesktopManager(BrowserMDIFrame parentFrame) {
        this.parentFrame = parentFrame;
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

        BrowserMDIFrame browserParentFrame = parentFrame;

        if (internalFrame != null) {
            internalFrame.getBrowserComponent().getHistory().updateActions();
        } else {
            browserParentFrame.getActionReload().setEnabled(false);
            browserParentFrame.getActionBackward().setEnabled(false);
            browserParentFrame.getActionForward().setEnabled(false);
        }
        browserParentFrame.getActionReload().setEnabled(internalFrame != null);
    }

    /**
     Get the parent frame.
     @return the frame
     */
    public BrowserMDIFrame getParentFrame() {
        return parentFrame;
    }

    /**
     Get the associated <tt>JDesktopPane</tt>.
     @return the <tt>JDesktopPane</tt>
     */
    public JDesktopPane getDesktopPane() {
        return parentFrame.getDesktopPane();
    }

    /**
     Get the list of open child frames.
     @return the list
     */
    public List<BrowserInternalFrame> getOpenFrames() {
        return openFrames;
    }

    /**
     Get a rectangle for a new child frame.
     @return the rectangle
     */
    public Rectangle getNextInternalFrameBounds() {

        if (newInternalY + NEW_INTERNAL_HEIGHT > getDesktopPane().getHeight()) {
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
    public void setActiveFrame(BrowserInternalFrame activeFrame) {
        this.activeFrame = activeFrame;
    }

    /**
     Look for an open frame with an equivalent init parameter.
     @param initParam the init parameter to look for.
     @return the open frame or <tt>null</tt>.
     */
    public BrowserInternalFrame getOpenFrame(Object initParam) {

        for (BrowserInternalFrame frame : openFrames) {
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
        for (BrowserInternalFrame openFrame : openFrames) {
            openFrame.setVisible(true);
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
    public void addInternalFrame(BrowserInternalFrame frame) {

        frame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent event) {
                checkSize();
            }

        });

        JMenu menuWindow = parentFrame.getMenuWindow();
        if (frameToMenuItem.size() == 0) {
            separatorMenuIndex = menuWindow.getMenuComponentCount();
            menuWindow.addSeparator();
        }
        Action action = new WindowActivateAction(frame);
        JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem(action);
        menuItem.setSelected(false);
        menuWindow.add(menuItem);

        getDesktopPane().add(frame);
        frameToMenuItem.put(frame, menuItem);
        openFrames.add(frame);
        parentFrame.setWindowActionsEnabled(true);
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

        Dimension size = getDesktopPane().getSize();

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
        for (BrowserInternalFrame openFrame : openFrames) {
            normalizeFrame(openFrame);
            currentBounds = getNextInternalFrameBounds();
            resizeFrame(openFrame, currentBounds.x, currentBounds.y, currentBounds.width, currentBounds.height);
            try {
                openFrame.setSelected(true);
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

            boolean isMaximum = (Boolean)changeEvent.getNewValue();
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
        for (JCheckBoxMenuItem menuItem : frameToMenuItem.values()) {
            menuItem.setSelected(false);

        }
        (frameToMenuItem.get(frame)).setSelected(true);
    }

    public void internalFrameDeiconified(InternalFrameEvent event) {
    }

    public void internalFrameOpened(InternalFrameEvent event) {
    }

    public void internalFrameIconified(InternalFrameEvent event) {
    }

    public void internalFrameClosing(InternalFrameEvent event) {
        BrowserInternalFrame frame = (BrowserInternalFrame)event.getInternalFrame();
        removeInternalFrame(frame);
    }

    public void internalFrameClosed(InternalFrameEvent event) {
        parentFrame.getDesktopPane().remove(event.getInternalFrame());
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
        JDesktopPane desktopPane = getDesktopPane();
        JInternalFrame[] frames = desktopPane.getAllFrames();
        for (JInternalFrame frame : frames) {
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
     Scroll the desktop pane such that the given frame becomes fully visible.
     @param frame the frame.
     */
    public void scrollToVisible(JInternalFrame frame) {
        getDesktopPane().scrollRectToVisible(frame.getBounds());
    }

    private void removeInternalFrame(BrowserInternalFrame frame) {

        JMenuItem menuItem = frameToMenuItem.remove(frame);
        if (menuItem != null) {
            JMenu menuWindow = parentFrame.getMenuWindow();
            menuWindow.remove(menuItem);
            openFrames.remove(frame);
            if (frameToMenuItem.size() == 0 && separatorMenuIndex > -1) {
                menuWindow.remove(separatorMenuIndex);
                separatorMenuIndex = -1;
                parentFrame.setWindowActionsEnabled(false);
            }
        }
    }

    private void resetSize() {
        parentFrame.invalidateDesktopPane();
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

        JInternalFrame currentFrame = getDesktopPane().getSelectedFrame();
        JInternalFrame nextFrame;

        ListIterator<BrowserInternalFrame> it = openFrames.listIterator();
        //noinspection StatementWithEmptyBody
        while (it.hasNext() && it.next() != currentFrame) {
        }
        if (forward) {
            if (it.hasNext()) {
                nextFrame = it.next();
            } else {
                nextFrame = openFrames.getFirst();
            }
        } else {
            if (it.hasPrevious() && it.previous() != null && it.hasPrevious()) {
                nextFrame = it.previous();
            } else {
                nextFrame = openFrames.getLast();
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

    private void maximizeAllFrames(JInternalFrame source, boolean isMaximum) {

        synchronized (this) {
            if (maximizationInProgress) {
                return;
            }
            maximizationInProgress = true;
        }

        try {
            JInternalFrame[] frames = getDesktopPane().getAllFrames();
            for (JInternalFrame frame : frames) {
                if (frame == source) {
                    continue;
                }
                try {
                    frame.setMaximum(isMaximum);
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
