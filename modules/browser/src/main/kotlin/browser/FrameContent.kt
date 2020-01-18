/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import kotlinx.dom.build.addElement
import kotlinx.dom.childElements
import kotlinx.dom.firstChildElement
import org.gjt.jclasslib.browser.config.BrowserPath
import org.gjt.jclasslib.util.GUIHelper
import org.w3c.dom.Element
import util.LightOrDarkColor
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Color
import java.awt.Dimension
import java.io.File
import javax.swing.*

class FrameContent(val frame: BrowserFrame) : JPanel() {

    val wrappers = Position.values().map { TabbedPaneWrapper(it) }

    private var splitMode: SplitMode = SplitMode.NONE

    var focusedTabbedPane: BrowserTabbedPane = wrappers[Position.NW].tabbedPane

    val selectedTab: BrowserTab?
        get() = focusedTabbedPane.selectedTab

    init {
        layout = BorderLayout()
        split(SplitMode.NONE)
    }

    fun saveClassesToDirectory(directory: File) {
        val count = wrappers.flatMap { it.tabbedPane.tabs() }
                .map { it.saveClassToDirectory(directory) }.count { it }

        GUIHelper.showMessage(frame, "$count classes were saved to $directory", JOptionPane.INFORMATION_MESSAGE)
    }

    fun focus(focusedTabbedPane: BrowserTabbedPane) {
        this.focusedTabbedPane = focusedTabbedPane
    }

    fun closeAllTabs() {
        wrappers.forEach { it.tabbedPane.removeAll() }
        split(SplitMode.NONE)
    }

    fun split(splitMode: SplitMode) {
        removeAll()
        transferTabs(splitMode)
        add(getComponent(splitMode), BorderLayout.CENTER)
        wrappers[Position.NW].focus()
        this.splitMode = splitMode
        frame.splitActions.forEach { it.value.isEnabled = it.key != splitMode }
        revalidate()
    }

    private fun getComponent(splitMode: SplitMode): JComponent = when (splitMode) {
        SplitMode.NONE -> {
            wrappers[Position.NW]
        }
        SplitMode.HORIZONTAL -> {
            splitPane(JSplitPane.HORIZONTAL_SPLIT, Position.NW, Position.NE)
        }
        SplitMode.VERTICAL -> {
            splitPane(JSplitPane.VERTICAL_SPLIT, Position.NW, Position.SW)
        }
        SplitMode.BOTH -> {
            splitPane(JSplitPane.VERTICAL_SPLIT,
                    splitPane(JSplitPane.HORIZONTAL_SPLIT, Position.NW, Position.NE),
                    splitPane(JSplitPane.HORIZONTAL_SPLIT, Position.SW, Position.SE)
            )
        }
    }

    private fun splitPane(splitConstant: Int, first: Position, second: Position) =
            splitPane(splitConstant, wrappers[first], wrappers[second])

    private fun splitPane(splitConstant: Int, first: JSplitPane, second: JSplitPane): JSplitPane {
        coupleDividers(first, second)
        coupleDividers(second, first)
        return splitPane(splitConstant, first as JComponent, second as JComponent)
    }

    private fun coupleDividers(first: JSplitPane, second: JSplitPane) {
        first.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY) {
            second.dividerLocation = first.dividerLocation
        }
    }

    private fun splitPane(splitConstant: Int, first: JComponent, second: JComponent) =
            JSplitPane(splitConstant, first, second).apply {
                isContinuousLayout = true
                resizeWeight = 0.5
                isOpaque = true
            }

    private fun transferTabs(splitMode: SplitMode) {
        when (this.splitMode to splitMode) {
            SplitMode.VERTICAL to SplitMode.NONE,
            SplitMode.HORIZONTAL to SplitMode.NONE,
            SplitMode.BOTH to SplitMode.NONE -> {
                transferTabs(Position.NE, Position.NW)
                transferTabs(Position.SE, Position.NW)
                transferTabs(Position.SW, Position.NW)
            }
            SplitMode.VERTICAL to SplitMode.HORIZONTAL -> {
                transferTabs(Position.SW, Position.NE)
            }
            SplitMode.HORIZONTAL to SplitMode.VERTICAL -> {
                transferTabs(Position.NE, Position.SW)
            }
            SplitMode.BOTH to SplitMode.HORIZONTAL -> {
                transferTabs(Position.SW, Position.NW)
                transferTabs(Position.SE, Position.NE)
            }
            SplitMode.BOTH to SplitMode.VERTICAL -> {
                transferTabs(Position.NE, Position.NW)
                transferTabs(Position.SE, Position.SW)
            }
        }
    }

    private fun transferTabs(sourcePosition: Position, targetPosition: Position) {
        wrappers[targetPosition].transferTabsFrom(wrappers[sourcePosition])
    }

    fun findTab(fileName: String): BrowserTab? =
            wrappers.flatMap { it.tabbedPane.tabs() }.firstOrNull { it.fileName == fileName }

    val totalTabCount: Int
        get() = wrappers.sumBy { it.tabbedPane.tabs().count() }

    fun openClassFile(fileName: String, moduleName: String = "", browserPath: BrowserPath? = null): BrowserTab =
            focusedTabbedPane.addTab(fileName, moduleName, browserPath)

    fun saveWorkspace(element: Element) {
        element.addElement(NODE_NAME_TABS) {
            setAttribute(ATTRIBUTE_SPLIT_MODE, splitMode.name)
            wrappers.filter { it.isShowing }.forEach { wrapper ->
                wrapper.saveWorkspace(this)
            }
        }
    }


    fun readWorkspace(element: Element) {
        element.firstChildElement(NODE_NAME_TABS)?.let { tabsElement ->
            split(SplitMode.getByName(tabsElement.getAttribute(ATTRIBUTE_SPLIT_MODE)))
            tabsElement.childElements(NODE_NAME_GROUP).forEach { groupElement ->
                val position = Position.getByName(groupElement.getAttribute(ATTRIBUTE_POSITION))
                wrappers[position].readWorkspace(groupElement)
            }
        }
    }

    operator fun List<TabbedPaneWrapper>.get(position: Position) = this[position.ordinal]

    enum class Position(val noneOpenMessage: String? = null) {
        NW("Open a class file"), NE, SE, SW;

        companion object {
            fun getByName(name: String?) = values().firstOrNull { it.name == name } ?: NW
        }
    }

    inner class TabbedPaneWrapper(val position: Position) : JPanel() {
        val tabbedPane = BrowserTabbedPane(this@FrameContent).apply {
            addChangeListener {
                showCard(if (tabCount == 0 ) CARD_EMPTY else CARD_TABBED_PANE)
                wrappers.forEach { it.updateMessageLabel() }
                frame.reloadAction.isEnabled = totalTabCount > 0
            }
        }

        private val cardLayout = CardLayout()
        private val messageLabel = JLabel(position.noneOpenMessage ?: TABBED_PANE_EMPTY_MESSAGE)

        init {
            layout = cardLayout
            add(createEmptyPanel(), CARD_EMPTY)
            add(tabbedPane, CARD_TABBED_PANE)
            showCard(CARD_EMPTY)
        }

        private fun updateMessageLabel() {
            messageLabel.text = if (totalTabCount > 0) TABBED_PANE_EMPTY_MESSAGE else position.noneOpenMessage ?: TABBED_PANE_EMPTY_MESSAGE
        }

        private fun showCard(cardName: String) {
            cardLayout.show(this, cardName)
        }

        private fun createEmptyPanel() = Box.createHorizontalBox().apply {
            add(Box.createHorizontalGlue())
            add(messageLabel)
            add(Box.createHorizontalGlue())
            background = EMPTY_BACKGROUND
            isOpaque = true

            tabbedPane.addDropTarget(this) {
                showCard(CARD_TABBED_PANE)
            }
        }

        fun transferTabsFrom(other: TabbedPaneWrapper) {
            tabbedPane.transferTabsFrom(other.tabbedPane)
        }

        override fun getPreferredSize() = PREFERRED_SIZE

        fun focus() {
            tabbedPane.focus()
        }

        fun saveWorkspace(element: Element) {
            element.addElement(NODE_NAME_GROUP) {
                setAttribute(ATTRIBUTE_POSITION, position.name)
                tabbedPane.tabs().forEach { tab ->
                    tab.saveWorkspace(this)
                }
            }
        }

        fun readWorkspace(element: Element) {
            element.childElements(BrowserTab.NODE_NAME).forEach { tabElement ->
                BrowserTab.create(tabElement, frame).apply {
                    tabbedPane.addTab(this)
                    setBrowserPath(BrowserPath.create(tabElement))
                }
            }
        }
    }

    companion object {
        const val CARD_EMPTY = "empty"
        const val CARD_TABBED_PANE = "tabbedPane"
        val PREFERRED_SIZE = Dimension(100, 100)
        val EMPTY_BACKGROUND = LightOrDarkColor(Color(210, 210, 210), Color(80, 80, 80))
        const val TABBED_PANE_EMPTY_MESSAGE = "Drag class files to this area"

        private const val NODE_NAME_TABS = "tabs"
        private const val ATTRIBUTE_SPLIT_MODE = "splitMode"
        private const val NODE_NAME_GROUP = "group"
        private const val ATTRIBUTE_POSITION = "position"
    }
}


