/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.util;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
    Wrapper around a label which displays a horizontal scrollbar if the
    text of the label does not fit into the current width of the label.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2001-05-31 13:23:00 $
*/
public class ScrollableJLabel extends JScrollPane 
                              implements ComponentListener,
                                         MaximizedListener {

    private static final int scrollBarHeight;
    
    static {
        scrollBarHeight = (int)
            (new JScrollBar(JScrollBar.HORIZONTAL)).getPreferredSize().getHeight();
    }
                                             
    private ExtendedJLabel label;
    private Dimension scrollPaneMinimumSize;
    private boolean isScrolling = false;
    
    public ScrollableJLabel(ExtendedJLabel label) {
        super(label);
        this.label  = label;
        
        setBorder(null);
        setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_NEVER);
        addComponentListener(this);
    }
    
    public void componentResized(ComponentEvent event) {
        resize(false);
    }

    public void componentMoved(ComponentEvent event) {
    }

    public void componentHidden(ComponentEvent event) {
    }

    public void componentShown(ComponentEvent event) {
    }

    public void frameMaximized(MaximizedEvent event) {
        resize(false);
    }

    public void resize(boolean force) {
        if (getSize().width < label.getWidth()) {
            if (!force && isScrolling) {
                return;
            }
            if (scrollPaneMinimumSize == null) {
                int height = label.getHeight() + scrollBarHeight;
                scrollPaneMinimumSize = new Dimension(0, height);
            }
            setMinimumSize(scrollPaneMinimumSize);
            setPreferredSize(scrollPaneMinimumSize);
            doInvalidate();
            
            isScrolling = true;

        } else if (force || isScrolling) {
            setMinimumSize(null);
            setPreferredSize(null);
            doInvalidate();
            
            isScrolling = false;
        }
    }
    
    private void doInvalidate() {
        invalidate();
        getParent().validate();
    }
    
}

