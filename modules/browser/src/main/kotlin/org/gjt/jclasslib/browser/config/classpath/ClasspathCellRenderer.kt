/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import java.awt.Component
import javax.swing.DefaultListCellRenderer
import javax.swing.JList
import javax.swing.UIManager

class ClasspathCellRenderer : DefaultListCellRenderer() {
    override fun getListCellRendererComponent(list: JList<*>, value: Any?, index: Int, isSelected: Boolean, cellHasFocus: Boolean): Component? {
        val entry = value as ClasspathEntry?
        super.getListCellRendererComponent(list, entry?.file?.path, index, isSelected, cellHasFocus)

        icon = UIManager.getIcon(if (entry is ClasspathDirectoryEntry) {
            "FileView.directoryIcon"
        } else {
            "FileView.fileIcon"
        })
        return this
    }
}
