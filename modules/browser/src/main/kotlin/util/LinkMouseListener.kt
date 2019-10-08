/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import java.awt.Cursor
import java.awt.Point
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.event.MouseMotionListener
import javax.swing.JComponent

abstract class LinkMouseListener(val component: JComponent) : MouseAdapter(), MouseMotionListener {

    init {
        component.apply {
            addMouseListener(this@LinkMouseListener)
            addMouseMotionListener(this@LinkMouseListener)
        }
    }

    protected abstract fun isLink(point: Point): Boolean
    protected abstract fun link(point: Point)

    override fun mouseClicked(event: MouseEvent) {
        val point = event.point
        if (isLink(point)) {
            link(point)
        }
    }

    override fun mouseMoved(event: MouseEvent) {
        val link = isLink(event.point)
        if (component.cursor.type == Cursor.getDefaultCursor().type && link) {
            component.cursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        } else if (!link) {
            component.cursor = Cursor.getDefaultCursor()
        }
    }

    override fun mouseDragged(event: MouseEvent) {
    }

}

