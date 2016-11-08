/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.idea

import com.intellij.icons.AllIcons
import com.intellij.openapi.Disposable
import com.intellij.ui.JBSplitter
import com.intellij.ui.JBTabsPaneImpl
import org.gjt.jclasslib.util.*
import javax.swing.JComponent
import javax.swing.SwingConstants

fun initUiFacades() {
    splitterFactory = ::JBSplitterFacade
    tabPaneFactory = ::JBTabsFacade

    treeIcons[TreeIcon.CLOSED] = AllIcons.Nodes.TreeClosed
    treeIcons[TreeIcon.OPEN] = AllIcons.Nodes.TreeOpen
    treeIcons[TreeIcon.LEAF] = AllIcons.FileTypes.Any_type
}

private class JBSplitterFacade(splitDirection: SplitDirection, first: JComponent, second: JComponent) : JBSplitter(splitDirection == SplitDirection.VERTICAL), SplitterFacade {
    init {
        firstComponent = first
        secondComponent = second
        splitterProportionKey = "jclasslib"
    }

    override val component: JComponent
        get() = this
}

private class JBTabsFacade : JBTabsPaneImpl(null, SwingConstants.TOP, Disposable {  }), TabbedPaneFacade {
    override fun addTabAtEnd(name: String, component: JComponent) {
        insertTab(name, null, component, null, -1)
    }

    override var selectedTabIndex: Int
        get() = selectedIndex
        set(value) {
            selectedIndex = value
        }

    override val selectedTab: JComponent
        get() = selectedComponent as JComponent

    override val outerComponent: JComponent
        get() = component
}
