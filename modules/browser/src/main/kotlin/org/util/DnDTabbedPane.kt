/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.datatransfer.Transferable
import java.awt.dnd.*
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import java.util.*
import javax.swing.*

@Suppress("RedundantOverride")
open class DnDTabbedPane : JTabbedPane() {

    private val glassPane : GhostGlassPane by lazy { createOrGetGlassPane(rootPane) }

    val tabAcceptor: TabAcceptor = object : TabAcceptor {
        override fun isDropAcceptable(tabbedPane: DnDTabbedPane, index: Int): Boolean = true
    }

    init {
        addDropTarget(this)
        DragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, createDragGestureListener())
        tabLayoutPolicy = SCROLL_TAB_LAYOUT
    }

    fun addDropTarget(component: JComponent, dropNotifier: () -> Unit = {}) {
        DropTarget(component, DnDConstants.ACTION_COPY_OR_MOVE, TabDropTargetListener(dropNotifier), true)
    }

    private fun createDragGestureListener() = DragGestureListener { event ->
        val tabLocation = event.dragOrigin
        val dragTabIndex = indexAtLocation(tabLocation.x, tabLocation.y)
        if (dragTabIndex >= 0) {
            initGlassPane(event.component, event.dragOrigin, dragTabIndex)
            try {
                event.startDrag(DragSource.DefaultMoveDrop, TabTransferable(dragTabIndex), createDragSourceListener())
            } catch (e: InvalidDnDOperationException) {
                e.printStackTrace()
            }
        }
    }

    private fun createDragSourceListener() = object : DragSourceListener {
        override fun dragEnter(event: DragSourceDragEvent) {
            event.dragSourceContext.cursor = DragSource.DefaultMoveDrop
            drawDrag = true
        }

        override fun dragExit(event: DragSourceEvent) {
            event.dragSourceContext.cursor = DragSource.DefaultMoveNoDrop
            drawDrag = false
            repaintGlassPanes()
        }

        override fun dragOver(event: DragSourceDragEvent) {
            val data = getTabTransferData(event)
            if (data == null) {
                event.dragSourceContext.cursor = DragSource.DefaultMoveNoDrop
                return
            }
            event.dragSourceContext.cursor = DragSource.DefaultMoveDrop
        }

        override fun dragDropEnd(event: DragSourceDropEvent) {
            hideGlassPanes()
        }

        override fun dropActionChanged(event: DragSourceDragEvent) {
        }
    }

    private fun getTabTransferData(event: DropTargetDropEvent) =
            event.transferable.getTransferData(FLAVOR) as TabTransferData

    private fun getTabTransferData(transferable: Transferable) =
            transferable.getTransferData(FLAVOR) as TabTransferData

    private fun getTabTransferData(event: DragSourceDragEvent) =
            event.dragSourceContext.transferable.getTransferData(FLAVOR) as TabTransferData?

    private fun buildGhostLocation(location: Point) = Point(location).apply {
        when (getTabPlacement()) {
            TOP -> {
                y = 1
                x -= glassPane.ghostWidth / 2
            }

            BOTTOM -> {
                y = height - 1 - glassPane.ghostHeight
                x -= glassPane.ghostWidth / 2
            }

            LEFT -> {
                x = 1
                y -= glassPane.ghostHeight / 2
            }

            RIGHT -> {
                x = width - 1 - glassPane.ghostWidth
                y -= glassPane.ghostHeight / 2
            }
        }
    }

    private fun getTargetTabIndex(point: Point): Int {
        val isTopOrBottom = getTabPlacement() == TOP || getTabPlacement() == BOTTOM

        if (tabCount == 0) {
            return 0
        }

        for (i in 0 until tabCount) {
            val r = getBoundsAt(i) ?: continue
            if (isTopOrBottom) {
                r.setRect(r.x - r.width / 2, r.y, r.width, height)
            } else {
                r.setRect(r.x, r.y - r.height / 2, width, r.height)
            }

            if (r.contains(point)) {
                return i
            }
        }

        val r = getLastVisibleTabBound() ?: return -1
        if (isTopOrBottom) {
            val x = r.x + r.width / 2
            r.setRect(x, r.y, width - x, height)
        } else {
            val y = r.y + r.height / 2
            r.setRect(r.x, y, width, height - y)
        }

        return if (r.contains(point)) tabCount else -1
    }

    private fun getFirstVisibleTabBound(): Rectangle? = getNextVisibleTabBound(-1)

    private fun getNextVisibleTabBound(index: Int): Rectangle? {
        for (i in index + 1 until tabCount) {
            return getBoundsAt(i) ?: continue
        }
        return null
    }

    private fun getPreviousVisibleTabBound(index: Int): Rectangle? {
        for (i in index - 1 downTo 0) {
            return getBoundsAt(i) ?: continue
        }
        return null
    }

    private fun getLastVisibleTabBound(): Rectangle? = getPreviousVisibleTabBound(tabCount)

    // Nullable return value is important because tabs will be hidden for tab overflow
    // and the bounds of such tabs are null
    override fun getBoundsAt(index: Int): Rectangle? = super.getBoundsAt(index)

    private fun convertTab(data: TabTransferData, targetIndex: Int) {
        val source = data.tabbedPane
        val sourceIndex = data.tabIndex
        if (sourceIndex < 0) {
            return
        }

        val cmp = source.getComponentAt(sourceIndex)
        val str = source.getTitleAt(sourceIndex)
        if (this !== source) {
            source.remove(sourceIndex)

            if (targetIndex == tabCount) {
                addTab(str, cmp)
            } else {
                insertTab(str, null, cmp, null, if (targetIndex < 0) 0 else targetIndex)
            }

            selectedComponent = cmp
            return
        }

        if (targetIndex < 0 || sourceIndex == targetIndex) {
            return
        }

        selectedIndex = when {
            targetIndex == tabCount -> {
                source.remove(sourceIndex)
                addTab(str, cmp)
                tabCount - 1
            }
            sourceIndex > targetIndex -> {
                source.remove(sourceIndex)
                insertTab(str, null, cmp, null, targetIndex)
                targetIndex
            }
            else -> {
                source.remove(sourceIndex)
                insertTab(str, null, cmp, null, targetIndex - 1)
                targetIndex - 1
            }
        }
    }

    private fun initTargetLeftRightLine(next: Int, data: TabTransferData) : Boolean {
        return if (next < 0) {
            false
        } else if (data.tabbedPane === this && (data.tabIndex == next || next - data.tabIndex == 1)) {
            false
        } else if (tabCount == 0) {
            false
        } else if (next == 0) {
            val rect = getFirstVisibleTabBound() ?: return false
            glassPane.insertionMarker.setRect(rect.x - LINE_WIDTH / 2, rect.y, LINE_WIDTH, rect.height)
            true
        } else if (next == tabCount) {
            val rect = getLastVisibleTabBound() ?: return false
            glassPane.insertionMarker.setRect(rect.x + rect.width - LINE_WIDTH / 2, rect.y, LINE_WIDTH, rect.height)
            true
        } else {
            val rect = getPreviousVisibleTabBound(next) ?: return false
            glassPane.insertionMarker.setRect(rect.x + rect.width - LINE_WIDTH / 2, rect.y, LINE_WIDTH, rect.height)
            true
        }
    }

    private fun initTargetTopBottomLine(next: Int, data: TabTransferData) : Boolean {
        return if (next < 0) {
            false
        } else if (data.tabbedPane === this && (data.tabIndex == next || next - data.tabIndex == 1)) {
            false
        } else if (tabCount == 0) {
            false
        } else if (next == tabCount) {
            val rect = getLastVisibleTabBound() ?: return false
            glassPane.insertionMarker.setRect(rect.x, rect.y + rect.height - LINE_WIDTH / 2, rect.width, LINE_WIDTH)
            true
        } else if (next == 0) {
            val rect = getFirstVisibleTabBound() ?: return false
            glassPane.insertionMarker.setRect(rect.x, -LINE_WIDTH / 2, rect.width, LINE_WIDTH)
            true
        } else {
            val rect = getPreviousVisibleTabBound(next) ?: return false
            glassPane.insertionMarker.setRect(rect.x, rect.y + rect.height - LINE_WIDTH / 2, rect.width, LINE_WIDTH)
            true
        }
    }

    fun Rectangle2D.Double.setRect(x: Int, y: Int, width: Int, height: Int) {
        this.setRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
    }

    fun Rectangle.setRect(x: Int, y: Int, width: Int, height: Int) {
        this.setRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
    }

    private fun initGlassPane(c: Component, tabLocation: Point, tabIndex: Int) {
        val image = BufferedImage(c.width, c.height, BufferedImage.TYPE_INT_ARGB)
        c.paint(image.graphics)
        updateGlassPane(tabLocation)
        glassPane.apply {
            val rect = getBoundsAt(tabIndex) ?: return
            draggingGhost = image.getSubimage(rect.x, rect.y, rect.width, rect.height)
        }
    }

    private fun updateGlassPane(point: Point) {
        glassPane.apply {
            ghostLocation = buildGhostLocation(point)
            glassPane.offset = SwingUtilities.convertPoint(this@DnDTabbedPane, 0, 0, this)
            isVisible = true
            drawDrag = true
        }
    }

    protected open fun isDataFlavorSupported(transferable: Transferable) = false
    protected open fun handleDrop(event: DropTargetDropEvent) {
    }

    private class GhostGlassPane : JPanel() {
        var offset = Point(0, 0)
        var ghostLocation = Point(0, 0)
        val insertionMarker = Rectangle2D.Double()
        var drawInsertionMarker = false

        init {
            isOpaque = false
            isVisible = false
        }

        val ghostWidth: Int
            get() = draggingGhost?.getWidth(this) ?: 0

        val ghostHeight: Int
            get() = draggingGhost?.getHeight(this) ?: 0

        public override fun paintComponent(g: Graphics) {
            if (draggingGhost == null || !drawDrag) {
                return
            }
            (g as Graphics2D).apply {
                translate(offset.x, offset.y)
                if (drawInsertionMarker) {
                    paint = INSERTION_MARKER_COLOR
                    fill(insertionMarker)
                }
                composite = GHOST_COMPOSITE
                drawImage(draggingGhost, ghostLocation.x, ghostLocation.y, null)
                translate(-offset.x, -offset.y)
            }
        }
    }

    private inner class TabTransferable(tabIndex: Int) : Transferable {
        private val data: TabTransferData = TabTransferData(this@DnDTabbedPane, tabIndex)
        override fun getTransferData(flavor: DataFlavor) = data
        override fun getTransferDataFlavors() = Array(1) { FLAVOR }
        override fun isDataFlavorSupported(flavor: DataFlavor) = flavor.humanPresentableName == NAME
    }

    private inner class TabTransferData(val tabbedPane: DnDTabbedPane, val tabIndex: Int)

    private inner class TabDropTargetListener(private val dropNotifier: () -> Unit) : DropTargetListener {
        override fun dragEnter(event: DropTargetDragEvent) {
            if (isDropAcceptable(event.transferable)) {
                event.acceptDrag(DnDConstants.ACTION_COPY)
            } else {
                event.rejectDrag()
            }
        }

        override fun dragExit(event: DropTargetEvent) {
            glassPane.drawInsertionMarker = false
        }

        override fun dropActionChanged(event: DropTargetDragEvent) {
        }

        override fun dragOver(event: DropTargetDragEvent) {
            if (isTabTransfer(event.transferable)) {
                val data = getTabTransferData(event.transferable)
                glassPane.drawInsertionMarker = if (getTabPlacement() == TOP || getTabPlacement() == BOTTOM) {
                    initTargetLeftRightLine(getTargetTabIndex(event.location), data)
                } else {
                    initTargetTopBottomLine(getTargetTabIndex(event.location), data)
                }
                repaint()
                updateGlassPane(event.location)
                repaintGlassPanes()
            }
        }

        override fun drop(event: DropTargetDropEvent) {
            when {
                isTabTransfer(event.transferable) -> {
                    event.acceptDrop(event.dropAction)
                    convertTab(getTabTransferData(event), getTargetTabIndex(event.location))
                    event.dropComplete(true)
                    dropNotifier()
                }
                isDataFlavorSupported(event.transferable) -> handleDrop(event)
                else -> event.dropComplete(false)
            }
            glassPane.drawInsertionMarker = false
            repaintGlassPanes()
        }

        private fun isDropAcceptable(transferable: Transferable?): Boolean {
            if (transferable == null) return false
            return isTabTransfer(transferable) || isDataFlavorSupported(transferable)
        }

        private fun isTabTransfer(transferable: Transferable?): Boolean {
            if (transferable == null) return false
            return transferable.isDataFlavorSupported(FLAVOR) && isDataAcceptable(getTabTransferData(transferable))
        }

        private fun isDataAcceptable(data: TabTransferData?): Boolean {
            if (data != null) {
                if (this@DnDTabbedPane === data.tabbedPane && data.tabIndex >= 0) {
                    return true
                }
                if (this@DnDTabbedPane !== data.tabbedPane) {
                    return tabAcceptor.isDropAcceptable(data.tabbedPane, data.tabIndex)
                }
            }

            return false
        }
    }

    interface TabAcceptor {
        fun isDropAcceptable(tabbedPane: DnDTabbedPane, index: Int): Boolean
    }

    companion object {
        private const val LINE_WIDTH = 3
        private const val NAME = "TabTransferData"
        private val FLAVOR = DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME)
        private val INSERTION_MARKER_COLOR = Color(0, 100, 255)
        private val GHOST_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)

        private val glassPanes = WeakHashMap<JRootPane, GhostGlassPane>()
        var draggingGhost: BufferedImage? = null
        var drawDrag = false

        fun repaintGlassPanes() {
            glassPanes.values.forEach(GhostGlassPane::repaint)
        }

        fun hideGlassPanes() {
            glassPanes.values.forEach {
                it.isVisible = false
            }
        }

        private fun createOrGetGlassPane(rootPane: JRootPane) = glassPanes.getOrPut(rootPane) {
            GhostGlassPane().apply {
                rootPane.glassPane = this
            }
        }

    }
}

