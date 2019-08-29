/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import net.miginfocom.swing.MigLayout
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JSeparator
import javax.swing.UIManager

class TitledSeparator(title: String) : JPanel() {
    init {
        layout = MigLayout("insets 0 0 unrel 0", "[][grow, fill]", "[align ${if (GUIHelper.isMacOs()) "bottom" else "center" }]")
        add(JLabel(title).apply {
            font = UIManager.getFont("TitledBorder.font")
            foreground = UIManager.getColor("TitledBorder.titleColor")
        })
        add(JSeparator(JSeparator.HORIZONTAL))
    }
}
