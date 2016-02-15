/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.mdi.MDIConfig
import org.gjt.jclasslib.util.GUIHelper
import org.gjt.jclasslib.util.ReentryGuard
import java.awt.Color
import java.awt.Dimension
import java.awt.Point
import java.awt.Rectangle
import java.awt.event.ActionEvent
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.beans.PropertyChangeEvent
import java.beans.VetoableChangeListener
import java.io.IOException
import java.util.*
import javax.swing.*
import javax.swing.event.InternalFrameEvent
import javax.swing.event.InternalFrameListener

class BrowserDesktopManager(val parentFrame: BrowserMDIFrame) : DefaultDesktopManager(), VetoableChangeListener, InternalFrameListener {

    private val openFrames = LinkedList<BrowserInternalFrame>()

    private var newInternalX = 0
    private var newInternalY = 0

    private val frameToMenuItem = hashMapOf<JInternalFrame, JCheckBoxMenuItem>()
    private var activeFrame: BrowserInternalFrame? = null
    private var rollover = 0
    private var separatorMenuIndex = -1

    private var maximizationReentryGuard = ReentryGuard()
    private var anyFrameMaximized: Boolean = false

    val desktopPane = JDesktopPane().apply {
        background = Color.LIGHT_GRAY
        this.desktopManager = this@BrowserDesktopManager
    }

    val selectedFrame: BrowserInternalFrame?
        get() = desktopPane.selectedFrame as BrowserInternalFrame?

    val nextInternalFrameBounds: Rectangle
        get() {
            if (newInternalY + NEW_INTERNAL_HEIGHT > desktopPane.height) {
                rollover++
                newInternalY = 0
                newInternalX = rollover * NEW_INTERNAL_X_OFFSET
            }

            val nextBounds = Rectangle(newInternalX, newInternalY, NEW_INTERNAL_WIDTH, NEW_INTERNAL_HEIGHT)
            newInternalX += NEW_INTERNAL_X_OFFSET
            newInternalY += NEW_INTERNAL_Y_OFFSET

            return nextBounds
        }

    override fun internalFrameActivated(event: InternalFrameEvent) {
        (event.internalFrame as BrowserInternalFrame?)?.apply {
            browserComponent.history.updateActions()
            parentFrame.reloadAction.isEnabled = true
            browserComponent.checkSelection()
        }
    }

    override fun internalFrameDeactivated(event: InternalFrameEvent) {
        parentFrame.apply {
            reloadAction.isEnabled = false
            backwardAction.isEnabled = false
            forwardAction.isEnabled = false
            reloadAction.isEnabled = false
        }
    }

    fun checkResizeInMaximizedState() {
        if (anyFrameMaximized || openFrames.isEmpty()) {
            resetSize()
        }
    }

    fun scrollToVisible(frame: JInternalFrame) {
        desktopPane.scrollRectToVisible(frame.bounds)
    }

    fun closeAllFrames() {
        while (openFrames.size > 0) {
            openFrames[0].doDefaultCloseAction()
        }
    }

    fun createMDIConfig() = MDIConfig().apply {
        internalFrameDescs = openFrames.map { openFrame ->
            val bounds = openFrame.normalBounds
            val internalFrameDesc = MDIConfig.InternalFrameDesc().apply {
                initParam = openFrame.createWindowState()
                x = bounds.x
                y = bounds.y
                width = bounds.width
                height = bounds.height
                isMaximized = openFrame.isMaximum()
                isIconified = openFrame.isIcon()
            }

            if (openFrame === selectedFrame) {
                activeFrameDesc = internalFrameDesc
            }
            internalFrameDesc
        }
    }

    fun applyMDIConfig(config: MDIConfig) {

        var anyFrameMaximized = false
        config.internalFrameDescs.forEach { internalFrameDesc ->
            val initParam = internalFrameDesc.initParam
            if (initParam != null && initParam.fileName != null) {
                val frame: BrowserInternalFrame
                try {
                    frame = BrowserInternalFrame(this, initParam.fileName!!, initParam.browserPath)
                    resizeFrame(frame, internalFrameDesc.x, internalFrameDesc.y, internalFrameDesc.width, internalFrameDesc.height)

                    val frameMaximized = internalFrameDesc.isMaximized
                    anyFrameMaximized = anyFrameMaximized || frameMaximized

                    if (frameMaximized || anyFrameMaximized) {
                        frame.setMaximum(true)
                    } else if (internalFrameDesc.isIconified) {
                        frame.setIcon(true)
                    }

                    if (internalFrameDesc === config.activeFrameDesc) {
                        activeFrame = frame
                    }
                } catch (e: IOException) {
                    GUIHelper.showMessage(parentFrame, "An error occurred while reading ${initParam.fileName}", JOptionPane.ERROR_MESSAGE)
                    e.printStackTrace()
                }
            }
        }

        showAll()
    }

    fun getOpenFrame(initParam: Any): BrowserInternalFrame? {
        return openFrames.find { it.createWindowState() == initParam }
    }

    fun showAll() {
        openFrames.forEach { openFrame -> openFrame.isVisible = true }
        activeFrame?.let {
            it.setSelected(true)
        }
        checkSize()
    }

    fun addInternalFrame(frame: BrowserInternalFrame) {
        frame.addComponentListener(object : ComponentAdapter() {
            override fun componentResized(event: ComponentEvent?) {
                checkSize()
            }
        })

        val menuWindow = parentFrame.windowMenu
        if (frameToMenuItem.size == 0) {
            separatorMenuIndex = menuWindow.menuComponentCount
            menuWindow.addSeparator()
        }
        JCheckBoxMenuItem(WindowActivateAction(frame)).apply {
            isSelected = false
            menuWindow.add(this)
            frameToMenuItem.put(frame, this)
        }

        desktopPane.add(frame)
        openFrames.add(frame)
        parentFrame.setWindowActionsEnabled(true)
        checkSize()
    }

    fun cycleToNextWindow() {
        cycleWindows(CycleDirection.NEXT)
    }

    fun cycleToPreviousWindow() {
        cycleWindows(CycleDirection.PREVIOUS)
    }

    fun tileWindows() {
        val framesCount = openFrames.size
        if (framesCount == 0) {
            return
        }

        resetSize()

        val (rows, cols) = getRowsAndCols(framesCount)
        val size = desktopPane.size

        val width = size.width / cols
        val height = size.height / rows
        val offset = Point()

        val it = openFrames.iterator()
        for (i in 0..rows - 1) {
            var j = 0
            while (j < cols && i * cols + j < framesCount) {
                val currentFrame = it.next()
                normalizeFrame(currentFrame)
                resizeFrame(currentFrame, offset.x, offset.y, width, height)
                offset.x += width
                j++
            }
            offset.x = 0
            offset.y += height
        }
    }

    override fun activateFrame(frame: JInternalFrame) {
        super.activateFrame(frame)
        frameToMenuItem.values.forEach {
            it.isSelected = false
        }
        frameToMenuItem[frame]?.isSelected = true
    }

    private fun getRowsAndCols(framesCount: Int): Pair<Int, Int> {
        val sqrt = Math.sqrt(framesCount.toDouble()).toInt()
        var rows = sqrt
        var cols = sqrt
        if (rows * cols < framesCount) {
            cols++
            if (rows * cols < framesCount) {
                rows++
            }
        }
        return Pair(rows, cols)
    }

    fun stackWindows() {

        newInternalX = 0
        newInternalY = 0
        rollover = 0

        for (openFrame in openFrames) {
            normalizeFrame(openFrame)
            val bounds = nextInternalFrameBounds
            resizeFrame(openFrame, bounds.x, bounds.y, bounds.width, bounds.height)
            openFrame.setSelected(true)
        }
        checkSize()
    }

    override fun vetoableChange(changeEvent: PropertyChangeEvent) {

        val eventName = changeEvent.propertyName

        if (JInternalFrame.IS_MAXIMUM_PROPERTY == eventName) {
            if (maximizationReentryGuard.inProgress) {
                return
            }

            val isMaximum = changeEvent.newValue as Boolean
            if (isMaximum) {
                resetSize()
            }
            anyFrameMaximized = isMaximum
            val source = changeEvent.source as JInternalFrame
            maximizeAllFrames(source, isMaximum)
        }
    }

    override fun internalFrameDeiconified(event: InternalFrameEvent) {
    }

    override fun internalFrameOpened(event: InternalFrameEvent) {
    }

    override fun internalFrameIconified(event: InternalFrameEvent) {
    }

    override fun internalFrameClosing(event: InternalFrameEvent) {
        removeInternalFrame(event.internalFrame as BrowserInternalFrame)
    }

    override fun internalFrameClosed(event: InternalFrameEvent) {
        desktopPane.remove(event.internalFrame)
        checkSize()
    }

    override fun endResizingFrame(f: JComponent) {
        super.endResizingFrame(f)
        checkSize()
    }

    override fun endDraggingFrame(f: JComponent) {
        super.endDraggingFrame(f)
        checkSize()
    }

    private fun checkSize() {
        val size = Dimension()
        val frames = desktopPane.allFrames
        frames.forEach { frame ->
            size.width = Math.max(size.width, frame.x + frame.width)
            size.height = Math.max(size.height, frame.y + frame.height)
        }
        desktopPane.preferredSize = if (size.width > 0 && size.height > 0) size else null
        desktopPane.revalidate()
    }

    private fun removeInternalFrame(frame: BrowserInternalFrame) {
        val menuItem = frameToMenuItem.remove(frame)
        if (menuItem != null) {
            val menuWindow = parentFrame.windowMenu
            menuWindow.remove(menuItem)
            openFrames.remove(frame)
            if (frameToMenuItem.size == 0 && separatorMenuIndex > -1) {
                menuWindow.remove(separatorMenuIndex)
                separatorMenuIndex = -1
                parentFrame.setWindowActionsEnabled(false)
            }
        }
    }

    private fun resetSize() {
        desktopPane.apply {
            preferredSize = null
            size = Dimension(0, 0)
        }
        parentFrame.contentPane.apply {
            invalidate()
            validate()
        }
    }

    private fun normalizeFrame(frame: JInternalFrame) {
        if (frame.isIcon) {
            frame.isIcon = false
        }
        if (frame.isMaximum) {
            frame.isMaximum = false
        }
    }

    private fun cycleWindows(cycleDirection: CycleDirection) {
        getNextFrame(cycleDirection).apply {
            if (isIcon()) {
                setIcon(false)
            }
            setSelected(true)
            scrollToVisible(this)
        }
    }

    private fun getNextFrame(cycleDirection: CycleDirection): BrowserInternalFrame {
        val it = openFrames.listIterator()
        while (it.hasNext() && it.next() !== desktopPane.selectedFrame) {
        }
        when (cycleDirection) {
            CycleDirection.NEXT -> {
                if (it.hasNext()) {
                    return it.next()
                } else {
                    return openFrames.first
                }
            }
            CycleDirection.PREVIOUS -> {
                if (it.hasPrevious() && it.let { it.previous(); it.hasPrevious() }) {
                    return it.previous()
                } else {
                    return openFrames.last
                }
            }
        }
    }

    private fun maximizeAllFrames(source: JInternalFrame, isMaximum: Boolean) {
        maximizationReentryGuard.execute {
            desktopPane.allFrames.filterNot { it === source }.forEach { it.isMaximum = isMaximum }
        }
    }

    private inner class WindowActivateAction(private val frame: JInternalFrame) : AbstractAction(frame.title) {
        override fun actionPerformed(event: ActionEvent) {
            if (frame.isIcon) {
                frame.isIcon = false
            }
            if (frame.isSelected) {
                (event.source as JCheckBoxMenuItem).isSelected = true
            } else {
                frame.isSelected = true
            }
            scrollToVisible(frame)
        }
    }

    private enum class CycleDirection {NEXT, PREVIOUS }

    companion object {
        private val NEW_INTERNAL_X_OFFSET = 22
        private val NEW_INTERNAL_Y_OFFSET = 22
        private val NEW_INTERNAL_WIDTH = 600
        private val NEW_INTERNAL_HEIGHT = 400
    }
}
