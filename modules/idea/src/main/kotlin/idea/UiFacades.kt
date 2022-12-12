/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.idea

import com.intellij.CommonBundle
import com.intellij.execution.process.ConsoleHighlighter
import com.intellij.icons.AllIcons
import com.intellij.ide.ui.UISettings
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DoNotAskOption
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.messages.MessagesService
import com.intellij.openapi.util.Disposer
import com.intellij.ui.JBSplitter
import com.intellij.ui.JBTabsPaneImpl
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.treeStructure.Tree
import org.gjt.jclasslib.util.*
import java.awt.Color
import java.awt.Component
import javax.swing.*
import javax.swing.border.Border
import javax.swing.tree.DefaultTreeCellRenderer
import kotlin.math.roundToInt

fun initUiFacades() {
    splitterFactory = ::JBSplitterFacade
    tabPaneFactory = ::JBTabsFacade
    treeFactory = ::Tree
    treeCellRendererFactory = {
        val rendererFillBackground = UIManager.getBoolean("Tree.rendererFillBackground")
        try {
            UIManager.put("Tree.rendererFillBackground", false)
            DefaultTreeCellRenderer()
        } finally {
            UIManager.put("Tree.rendererFillBackground", rendererFillBackground)
        }
    }
    scrollPaneFactory = ::JBScrollPane
    borderlessScrollPaneFactory = ::BorderlessJBScrollPane

    alertFacade = object : AlertFacade {
        override fun showOptionDialog(parent: Component?, mainMessage: String, contentMessage: String?, options: Array<String>, alertType: AlertType, suppressionShown: Boolean): OptionAlertResult {
            var suppressionSelected = false
            val doNotAskOption = if (suppressionShown) {
                object : DoNotAskOption.Adapter() {
                    override fun rememberChoice(isSelected: Boolean, exitCode: Int) {
                        if (exitCode == Messages.OK && isSelected) {
                            suppressionSelected = true
                        }
                    }
                }

            } else {
                null
            }
            val selectedIndex = MessagesService.getInstance().showMessageDialog(getProject(parent), null, combineMessage(mainMessage, contentMessage), GUIHelper.MESSAGE_TITLE, options, 0, -1, alertType.getIcon(), doNotAskOption, false, null)
            return OptionAlertResult(selectedIndex, suppressionSelected)
        }

        override fun showMessage(parent: Component?, mainMessage: String, contentMessage: String?, alertType: AlertType, suppressionShown: Boolean): Boolean {
            return showOptionDialog(parent, mainMessage, contentMessage, arrayOf(Messages.getOkButton()), alertType, suppressionShown).suppressionSelected
        }

        override fun showYesNoDialog(parent: Component?, mainMessage: String, contentMessage: String?, suppressionShown: Boolean) =
            showOptionDialog(parent, mainMessage, contentMessage, arrayOf(CommonBundle.getYesButtonText(), CommonBundle.getNoButtonText()), AlertType.QUESTION, suppressionShown)

        override fun showOkCancelDialog(parent: Component?, mainMessage: String, contentMessage: String?, suppressionShown: Boolean) =
            showOptionDialog(parent, mainMessage, contentMessage, arrayOf(CommonBundle.getOkButtonText(), CommonBundle.getCancelButtonText()), AlertType.QUESTION, suppressionShown)

        private fun getProject(parent: Component?) : Project? = getToolWindow(parent)?.project

        private fun getToolWindow(parent: Component?) =
            if (parent is BytecodeToolWindowPanel) {
                parent
            } else {
                (SwingUtilities.getAncestorOfClass(BytecodeToolWindowPanel::class.java, parent) as BytecodeToolWindowPanel?)
            }

        private fun combineMessage(mainMessage: String, contentMessage: String?): String =
            if (contentMessage != null) {
                mainMessage + "\n\n" + contentMessage
            } else {
                mainMessage
            }

        private fun AlertType.getIcon(): Icon = when (this) {
            AlertType.INFORMATION -> Messages.getInformationIcon()
            AlertType.WARNING -> Messages.getWarningIcon()
            AlertType.ERROR -> Messages.getErrorIcon()
            AlertType.QUESTION -> Messages.getQuestionIcon()
        }
    }

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

    val uiSettings = UISettings.getInstance()
    tableRowHeight = ((if (uiSettings.overrideLafFonts) uiSettings.fontSize else 12) * 1.5).roundToInt()

    val editorColorsScheme = EditorColorsManager.getInstance().schemeForCurrentUITheme
    documentFontFamily = editorColorsScheme.editorFontName
    documentFontSize = editorColorsScheme.editorFontSize
}

class BorderlessJBScrollPane(component: Component): JBScrollPane(component) {
    init {
        border = null
    }
    override fun setBorder(border: Border?) {
    }
}

private fun MutableMap<ColorKey, Color>.addColorMapping(colorKey: ColorKey, attributesKey: TextAttributesKey) {
    val scheme = EditorColorsManager.getInstance().globalScheme
    scheme.getAttributes(TextAttributesKey.createTextAttributesKey(attributesKey.externalName))?.let { attributes ->
        put(colorKey, attributes.foregroundColor)
    }
}

private class JBSplitterFacade(splitDirection: SplitDirection, first: JComponent, second: JComponent) : JBSplitter(
    splitDirection == SplitDirection.VERTICAL
), SplitterFacade {
    init {
        firstComponent = first
        secondComponent = second
        splitterProportionKey = "jclasslib"
    }

    override val component: JComponent
        get() = this
}

private class JBTabsFacade : JBTabsPaneImpl(null, SwingConstants.TOP, Disposer.newDisposable()), TabbedPaneFacade {
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
