/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.idea

import com.intellij.execution.process.ConsoleHighlighter
import com.intellij.icons.AllIcons
import com.intellij.ide.ui.UISettings
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.ui.JBSplitter
import com.intellij.ui.JBTabsPaneImpl
import org.gjt.jclasslib.util.*
import java.awt.Color
import javax.swing.JComponent
import javax.swing.SwingConstants
import kotlin.math.roundToInt

fun initUiFacades() {
    splitterFactory = ::JBSplitterFacade
    tabPaneFactory = ::JBTabsFacade

    treeIcons = mapOf(
            TreeIcon.CLOSED to AllIcons.Nodes.Folder,
            TreeIcon.OPEN to AllIcons.Nodes.Folder,
            TreeIcon.LEAF to AllIcons.FileTypes.Any_type
    )

    colors = mutableMapOf<ColorKey, Color>().apply {
        addColorMapping(ColorKey.VALUE, ConsoleHighlighter.RED)
        addColorMapping(ColorKey.LINK, ConsoleHighlighter.GREEN)
        addColorMapping(ColorKey.ACTIVE_LINK, ConsoleHighlighter.GREEN_BRIGHT)
    }

    val uiSettings = UISettings.instance
    tableRowHeight = ((if (uiSettings.overrideLafFonts) uiSettings.fontSize else 12) * 1.5).roundToInt()

    val editorColorsScheme = EditorColorsManager.getInstance().schemeForCurrentUITheme
    documentFontFamily = editorColorsScheme.editorFontName
    documentFontSize = editorColorsScheme.editorFontSize
}

private fun MutableMap<ColorKey, Color>.addColorMapping(colorKey: ColorKey, attributesKey: TextAttributesKey) {
    val scheme = EditorColorsManager.getInstance().globalScheme
    scheme.getAttributes(TextAttributesKey.createTextAttributesKey(attributesKey.externalName))?.let { attributes ->
        put(colorKey, attributes.foregroundColor)
    }
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

private class JBTabsFacade : JBTabsPaneImpl(null, SwingConstants.TOP, Disposable { }), TabbedPaneFacade {
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
