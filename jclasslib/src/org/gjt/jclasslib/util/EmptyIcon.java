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
    Transparent icon with no content.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 07:46:43 $
*/
public class EmptyIcon implements Icon {

    private int width;
    private int height;

    /**
     * Constructor.
     * @param width the width of the icon.
     * @param height the height of the icon.
     */
    public EmptyIcon(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void paintIcon(Component c, Graphics g, int x, int y) {
    }

    public int getIconWidth() {
        return width;
    }

    public int getIconHeight() {
        return height;
    }
}
