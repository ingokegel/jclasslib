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
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTabbedPane
import javax.swing.SwingUtilities

open class DnDTabbedPane : JTabbedPane() {

    val tabAcceptor: TabAcceptor = object : TabAcceptor {
        override fun isDropAcceptable(tabbedPane: DnDTabbedPane, index: Int): Boolean {
            return true
        }
    }

    private var isDrawRect = false
    private val line = Rectangle2D.Double()

    init {
        addDropTarget(this)
        DragSource().createDefaultDragGestureRecognizer(this, DnDConstants.ACTION_COPY_OR_MOVE, createDragGestureListener())
    }

    fun addDropTarget(component: JComponent, dropNotifier: () -> Unit = {}) {
        DropTarget(component, DnDConstants.ACTION_COPY_OR_MOVE, TabDropTargetListener(dropNotifier), true)
    }

    private fun createDragGestureListener() = DragGestureListener { event ->
        val tabPt = event.dragOrigin
        val dragTabIndex = indexAtLocation(tabPt.x, tabPt.y)
        if (dragTabIndex < 0) {
            return@DragGestureListener
        }

        initGlassPane(event.component, event.dragOrigin, dragTabIndex)
        try {
            event.startDrag(DragSource.DefaultMoveDrop, TabTransferable(dragTabIndex), createDragSourceListener())
        } catch (e: InvalidDnDOperationException) {
            e.printStackTrace()
        }
    }

    private fun createDragSourceListener() = object : DragSourceListener {
        override fun dragEnter(event: DragSourceDragEvent) {
            event.dragSourceContext.cursor = DragSource.DefaultMoveDrop
        }

        override fun dragExit(event: DragSourceEvent) {
            event.dragSourceContext.cursor = DragSource.DefaultMoveNoDrop
            line.origin()
            isDrawRect = false
            GLASS_PANE.apply {
                setPoint(Point(-1000, -1000))
                repaint()
            }
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
            isDrawRect = false
            line.origin()
            GLASS_PANE.apply {
                isVisible = false
                setImage(null)
            }
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

    private fun buildGhostLocation(location: Point): Point {
        val copy = Point(location).apply {
            when (getTabPlacement()) {
                JTabbedPane.TOP -> {
                    y = 1
                    x -= GLASS_PANE.ghostWidth / 2
                }

                JTabbedPane.BOTTOM -> {
                    y = height - 1 - GLASS_PANE.ghostHeight
                    x -= GLASS_PANE.ghostWidth / 2
                }

                JTabbedPane.LEFT -> {
                    x = 1
                    y -= GLASS_PANE.ghostHeight / 2
                }

                JTabbedPane.RIGHT -> {
                    x = width - 1 - GLASS_PANE.ghostWidth
                    y -= GLASS_PANE.ghostHeight / 2
                }
            }
        }
        return SwingUtilities.convertPoint(this@DnDTabbedPane, copy, GLASS_PANE)
    }

    private fun getTargetTabIndex(point: Point): Int {
        val isTopOrBottom = getTabPlacement() == JTabbedPane.TOP || getTabPlacement() == JTabbedPane.BOTTOM

        if (tabCount == 0) {
            return 0
        }

        for (i in 0..tabCount - 1) {
            val r = getBoundsAt(i) ?: continue
            if (isTopOrBottom) {
                r.setRect(r.x - r.width / 2, r.y, r.width, r.height)
            } else {
                r.setRect(r.x, r.y - r.height / 2, r.width, r.height)
            }

            if (r.contains(point)) {
                return i
            }
        }

        val r = getLastVisibleTabBound() ?: return -1
        if (isTopOrBottom) {
            val x = r.x + r.width / 2
            r.setRect(x, r.y, width - x, r.height)
        } else {
            val y = r.y + r.height / 2
            r.setRect(r.x, y, r.width, height - y)
        }

        return if (r.contains(point)) tabCount else -1
    }

    private fun getFirstVisibleTabBound(): Rectangle? {
        return getNextVisibleTabBound(-1)
    }

    private fun getNextVisibleTabBound(index : Int): Rectangle? {
        for (i in index + 1..tabCount - 1) {
            return getBoundsAt(i) ?: continue
        }
        return null
    }

    private fun getPreviousVisibleTabBound(index : Int): Rectangle? {
        for (i in index - 1 downTo 0) {
            return getBoundsAt(i) ?: continue
        }
        return null
    }

    private fun getLastVisibleTabBound(): Rectangle? {
        return getPreviousVisibleTabBound(tabCount)
    }

    // Nullable return value is important because tabs will be hidden for tab overflow
    // and the bounds of such tabs are null
    override fun getBoundsAt(index: Int): Rectangle? {
        return super.getBoundsAt(index)
    }

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

        if (targetIndex == tabCount) {
            source.remove(sourceIndex)
            addTab(str, cmp)
            selectedIndex = tabCount - 1
        } else if (sourceIndex > targetIndex) {
            source.remove(sourceIndex)
            insertTab(str, null, cmp, null, targetIndex)
            selectedIndex = targetIndex
        } else {
            source.remove(sourceIndex)
            insertTab(str, null, cmp, null, targetIndex - 1)
            selectedIndex = targetIndex - 1
        }
    }

    private fun initTargetLeftRightLine(next: Int, data: TabTransferData) {
        if (next < 0) {
            line.origin()
            isDrawRect = false
            return
        }

        if (data.tabbedPane === this && (data.tabIndex == next || next - data.tabIndex == 1)) {
            line.origin()
            isDrawRect = false
        } else if (tabCount == 0) {
            line.origin()
            isDrawRect = false
            return
        } else if (next == 0) {
            val rect = getFirstVisibleTabBound() ?: return
            line.setRect(rect.x - LINE_WIDTH / 2, rect.y, LINE_WIDTH, rect.height)
            isDrawRect = true
        } else if (next == tabCount) {
            val rect = getLastVisibleTabBound() ?: return
            line.setRect(rect.x + rect.width - LINE_WIDTH / 2, rect.y, LINE_WIDTH, rect.height)
            isDrawRect = true
        } else {
            val rect = getPreviousVisibleTabBound(next) ?: return
            line.setRect(rect.x + rect.width - LINE_WIDTH / 2, rect.y, LINE_WIDTH, rect.height)
            isDrawRect = true
        }
    }

    private fun initTargetTopBottomLine(next: Int, data: TabTransferData) {
        if (next < 0) {
            line.origin()
            isDrawRect = false
            return
        }

        if (data.tabbedPane === this && (data.tabIndex == next || next - data.tabIndex == 1)) {
            line.origin()
            isDrawRect = false
        } else if (tabCount == 0) {
            line.origin()
            isDrawRect = false
            return
        } else if (next == tabCount) {
            val rect = getLastVisibleTabBound() ?: return
            line.setRect(rect.x, rect.y + rect.height - LINE_WIDTH / 2, rect.width, LINE_WIDTH)
            isDrawRect = true
        } else if (next == 0) {
            val rect = getFirstVisibleTabBound() ?: return
            line.setRect(rect.x, -LINE_WIDTH / 2, rect.width, LINE_WIDTH)
            isDrawRect = true
        } else {
            val rect = getPreviousVisibleTabBound(next) ?: return
            line.setRect(rect.x, rect.y + rect.height - LINE_WIDTH / 2, rect.width, LINE_WIDTH)
            isDrawRect = true
        }
    }

    fun Rectangle2D.Double.origin() {
        this.setRect(0, 0, 0, 0)
    }

    fun Rectangle2D.Double.setRect(x: Int, y: Int, width: Int, height: Int) {
        this.setRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
    }

    fun Rectangle.setRect(x: Int, y: Int, width: Int, height: Int) {
        this.setRect(x.toDouble(), y.toDouble(), width.toDouble(), height.toDouble())
    }

    private fun initGlassPane(c: Component, tabPt: Point, tabIndex: Int) {
        rootPane.glassPane = GLASS_PANE
        val image = BufferedImage(c.width, c.height, BufferedImage.TYPE_INT_ARGB)
        c.paint(image.graphics)
        GLASS_PANE.apply {
            val rect = getBoundsAt(tabIndex) ?: return
            setImage(image.getSubimage(rect.x, rect.y, rect.width, rect.height))
            setPoint(buildGhostLocation(tabPt))
            isVisible = true
        }
    }

    protected open fun isDataFlavorSupported(transferable: Transferable) = false
    protected open fun handleDrop(event: DropTargetDropEvent) {
    }

    public override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        if (isDrawRect) {
            (g as Graphics2D).apply {
                paint = LINE_COLOR
                fill(line)
            }
        }
    }

    private class GhostGlassPane : JPanel() {
        private val composite: AlphaComposite
        private val point = Point(0, 0)
        private var draggingGhost: BufferedImage? = null

        init {
            isOpaque = false
            composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f)
        }

        fun setImage(draggingGhost: BufferedImage?) {
            this.draggingGhost = draggingGhost
        }

        fun setPoint(point: Point) {
            this.point.apply {
                x = point.x
                y = point.y
            }
        }

        val ghostWidth: Int
            get() = draggingGhost?.getWidth(this) ?: 0

        val ghostHeight: Int
            get() = draggingGhost?.getHeight(this) ?: 0

        public override fun paintComponent(g: Graphics) {
            if (draggingGhost == null) {
                return
            }
            val g2 = g as Graphics2D
            g2.composite = composite
            g2.drawImage(draggingGhost, point.getX().toInt(), point.getY().toInt(), null)
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
            isDrawRect = false
        }

        override fun dropActionChanged(event: DropTargetDragEvent) {
        }

        override fun dragOver(event: DropTargetDragEvent) {
            if (isTabTransfer(event.transferable)) {
                val data = getTabTransferData(event.transferable)
                if (getTabPlacement() == JTabbedPane.TOP || getTabPlacement() == JTabbedPane.BOTTOM) {
                    initTargetLeftRightLine(getTargetTabIndex(event.location), data)
                } else {
                    initTargetTopBottomLine(getTargetTabIndex(event.location), data)
                }
                repaint()
                GLASS_PANE.setPoint(buildGhostLocation(event.location))
                GLASS_PANE.repaint()
            }
        }

        override fun drop(event: DropTargetDropEvent) {
            if (isTabTransfer(event.transferable)) {
                event.acceptDrop(event.dropAction)
                convertTab(getTabTransferData(event), getTargetTabIndex(event.location))
                event.dropComplete(true)
                dropNotifier()
            } else if (isDataFlavorSupported(event.transferable)) {
                handleDrop(event)
            } else {
                event.dropComplete(false)
            }
            isDrawRect = false
            repaint()
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
        private val LINE_WIDTH = 3
        private val NAME = "TabTransferData"
        private val FLAVOR = DataFlavor(DataFlavor.javaJVMLocalObjectMimeType, NAME)
        private val GLASS_PANE = GhostGlassPane()
        private val LINE_COLOR = Color(0, 100, 255)
    }
}

