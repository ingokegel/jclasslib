/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.config.window.BrowserPath
import org.gjt.jclasslib.mdi.MDIConfig
import java.awt.BorderLayout
import java.awt.CardLayout
import java.awt.Color
import java.awt.Dimension
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
        add(createComponent(splitMode), BorderLayout.CENTER)
        wrappers[Position.NW].focus()
        this.splitMode = splitMode
        frame.splitActions.forEach { it.value.isEnabled = it.key != splitMode }
        revalidate()
    }

    private fun createComponent(splitMode: SplitMode): JComponent = when (splitMode) {
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

    fun findTab(fileName: String): BrowserTab? {
        return wrappers.flatMap { it.tabbedPane.tabs() }.firstOrNull { it.fileName == fileName }
    }

    val totalTabCount: Int
        get() = wrappers.sumBy { it.tabbedPane.tabs().count() }

    fun openClassFile(fileName: String, browserPath: BrowserPath? = null): BrowserTab =
            focusedTabbedPane.addTab(fileName, browserPath)

    fun applyMDIConfig(config: MDIConfig) {
        config.internalFrameDescs.forEach { internalFrameDesc ->
            val initParam = internalFrameDesc.initParam
            if (initParam != null && initParam.fileName != null) {
                openClassFile(initParam.fileName!!)
            }
        }
    }

    fun createMDIConfig() = MDIConfig().apply {
        internalFrameDescs = wrappers.flatMap { it.tabbedPane.tabs() }.map { tab ->
            val internalFrameDesc = MDIConfig.InternalFrameDesc().apply {
                initParam = tab.createWindowState()
            }
            if (tab === selectedTab) {
                activeFrameDesc = internalFrameDesc
            }
            internalFrameDesc
        }
    }

    operator fun List<TabbedPaneWrapper>.get(position: Position) = this[position.ordinal]

    enum class Position(val noneOpenMessage: String? = null) {
        NW("Open a class file"), NE(), SE(), SW();
    }

    inner class TabbedPaneWrapper(private val position: Position) : JPanel() {
        val tabbedPane = BrowserTabbedPane(this@FrameContent).apply {
            addChangeListener {
                showCard(if (tabCount == 0 ) CARD_EMPTY else CARD_TABBED_PANE)
                wrappers.forEach { it.updateMessageLabel() }
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
    }

    companion object {
        val CARD_EMPTY = "empty"
        val CARD_TABBED_PANE = "tabbedPane"
        val PREFERRED_SIZE = Dimension(100, 100)
        val EMPTY_BACKGROUND = Color(210, 210, 210)
        val TABBED_PANE_EMPTY_MESSAGE = "Drag class files to this area"
    }
}


