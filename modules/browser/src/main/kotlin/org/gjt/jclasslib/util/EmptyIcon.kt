/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.util

import java.awt.Component
import java.awt.Graphics
import javax.swing.Icon

class EmptyIcon(private val width: Int, private val height: Int) : Icon {

    override fun paintIcon(c: Component?, g: Graphics, x: Int, y: Int) {
    }

    override fun getIconWidth(): Int = width
    override fun getIconHeight(): Int = height
}
