/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import javax.swing.JComponent
import javax.swing.JTabbedPane

var tabPaneFactory: () -> TabbedPaneFacade = ::DefaultTabbedPaneFacade

interface TabbedPaneFacade {
    fun addTabAtEnd(name: String, component: JComponent)
    var selectedTabIndex: Int
    val selectedTab: JComponent
    val outerComponent: JComponent
}

private class DefaultTabbedPaneFacade : JTabbedPane(), TabbedPaneFacade {
    override fun addTabAtEnd(name: String, component: JComponent) {
        addTab(name, component)
    }

    override var selectedTabIndex: Int
        get() = selectedIndex
        set(value) {
            selectedIndex = value
        }

    override val selectedTab: JComponent
        get() = selectedComponent as JComponent

    override val outerComponent: JComponent
        get() = this
}