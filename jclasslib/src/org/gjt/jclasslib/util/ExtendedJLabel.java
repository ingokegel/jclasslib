/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.util;

import javax.swing.*;
import java.awt.*;

/**
    A <tt>JLabel</tt> that can be underlined, implements <tt>Scrollable</tt>,
    may have a tooltip text equal to its text and exposes a few convenience methods
    for <tt>setText()</tt>.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.3 $ $Date: 2003-08-18 07:47:20 $
*/
public class ExtendedJLabel extends JLabel implements Scrollable {

    private boolean underlined = false;
    private boolean autoTooltip = false;

    /**
        Constructor.
     */
    public ExtendedJLabel() {
    }

    /**
        Constructor.
        @param text the label text.
     */
    public ExtendedJLabel(String text) {
        super(text);
    }
    
    public Dimension getPreferredScrollableViewportSize() {
        return getSize();
    }

    public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
        return getWidth() / 10;
    }
    
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }
    
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
    
    public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
        return 10;
    }
    
    /**
        Check whether this label is underlined.
        @return underlined or not
     */
    public boolean isUnderlined() {
        return underlined;
    }
    
    /**
        Set whether this label is underlined.
        @param underlined underlined or not
     */
    public void setUnderlined(boolean underlined) {
        this.underlined = underlined;
        repaint();
    }
    
    /**
        Check whether the tooltip text is automatically equal
        to the text of this label or not.
        @return equal or not
     */
    public boolean getAutoTooltip() {
        return autoTooltip;
    }

    /**
        Set whether the tooltip text is automatically equal
        to the text of this label or not.
        @param autoTooltip equal or not
     */
    public void setAutoTooltip(boolean autoTooltip) {
        this.autoTooltip = autoTooltip;
        setToolTipText(getText());
    }
    
    public void setText(String text) {
        super.setText(text);
        if (autoTooltip) {
            setToolTipText(text);
        }
    }
    
    /**
        Convenience method for calling <tt>setText()</tt> with a <tt>short</tt>.
        @param number the <tt>short</tt>
     */
    public void setText(short number) {
        setText(String.valueOf(number));
    }

    /**
        Convenience method for calling <tt>setText()</tt> with a <tt>int</tt>.
        @param number the <tt>int</tt>
     */
    public void setText(int number) {
        setText(String.valueOf(number));
    }

    /**
        Convenience method for calling <tt>setText()</tt> with a <tt>double</tt>.
        @param number the <tt>double</tt>
     */
    public void setText(double number) {
        setText(String.valueOf(number));
    }

    /**
        Convenience method for calling <tt>setText()</tt> with a <tt>float</tt>.
        @param number the <tt>float</tt>
     */
    public void setText(float number) {
        setText(String.valueOf(number));
    }

    /**
        Convenience method for calling <tt>setText()</tt> with a <tt>long</tt>.
        @param number the <tt>long</tt>
     */
    public void setText(long number) {
        setText(String.valueOf(number));
    }

    public void paint(Graphics g) {
        super.paint(g);
        
        if (underlined) {
            Insets i = getInsets();
            FontMetrics fm = g.getFontMetrics();

            Rectangle textRect = new Rectangle();
            Rectangle viewRect = new Rectangle(i.left, i.top, getWidth() - (i.right + i.left), getHeight() - (i.bottom + i.top) );

            SwingUtilities.layoutCompoundLabel(
                                this,
                                fm,
                                getText(),
                                getIcon(),
                                getVerticalAlignment(),
                                getHorizontalAlignment(),
                                getVerticalTextPosition(),
                                getHorizontalTextPosition(),
                                viewRect,
                                new Rectangle(),
                                textRect,
                                getText() == null ? 0 : ((Integer)UIManager.get("Button.textIconGap")).intValue()
                          );


            int offset = 2;
            if (UIManager.getLookAndFeel().isNativeLookAndFeel() && System.getProperty("os.name").startsWith("Windows")) {
                offset = 1;
            }
            g.fillRect(textRect.x + ((Integer)UIManager.get("Button.textShiftOffset")).intValue() ,
                       textRect.y + fm.getAscent() + ((Integer)UIManager.get("Button.textShiftOffset")).intValue() + offset,
                       textRect.width,
                       1);
        }
    }
}

