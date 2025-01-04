/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import java.awt.event.FocusAdapter
import java.awt.event.FocusEvent
import javax.swing.JTextArea
import javax.swing.UIManager

class MultiLineLabel : JTextArea(), TextDisplay {
    init {
        isEditable = false
        border = null
        foreground = UIManager.getColor("Label.foreground")
        font = UIManager.getFont("Label.font")
        isOpaque = false

        addFocusListener(object : FocusAdapter() {
            override fun focusGained(e: FocusEvent) {
                selectAll()
            }
        })
    }

    var autoTooltip = false
        set(autoTooltip) {
            field = autoTooltip
            if (autoTooltip) {
                toolTipText = text
            }
        }

    override fun setText(text: String) {
        super.setText(text)
        if (autoTooltip) {
            toolTipText = text
        }
    }

}
