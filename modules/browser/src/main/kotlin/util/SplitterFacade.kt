/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import javax.swing.JComponent
import javax.swing.JSplitPane

var splitterFactory: (splitDirection: SplitDirection, first: JComponent, second: JComponent) -> SplitterFacade = ::DefaultSplitter

interface SplitterFacade {
    val component: JComponent
}

enum class SplitDirection {HORIZONTAL, VERTICAL }

private class DefaultSplitter(splitDirection: SplitDirection, first: JComponent, second: JComponent) : JSplitPane(), SplitterFacade {
    init {
        isContinuousLayout = true
        orientation = when (splitDirection) {
            SplitDirection.HORIZONTAL -> HORIZONTAL_SPLIT
            SplitDirection.VERTICAL -> VERTICAL_SPLIT
        }
        setLeftComponent(first)
        setRightComponent(second)
    }

    override val component: JComponent
        get() = this

}