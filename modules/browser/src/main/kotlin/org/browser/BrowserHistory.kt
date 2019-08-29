/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import javax.swing.tree.TreePath
import javax.swing.undo.AbstractUndoableEdit
import javax.swing.undo.UndoManager
import javax.swing.undo.UndoableEdit

class BrowserHistory(private val services: BrowserServices) {

    private val undoManager = UndoManager()

    fun clear() {
        undoManager.discardAllEdits()
        updateActions()
    }

    fun historyBackward() {
        undoManager.undo()
        updateActions()
    }

    fun historyForward() {
        undoManager.redo()
        updateActions()
    }

    fun updateActions() {
        services.backwardAction.isEnabled = undoManager.canUndo()
        services.forwardAction.isEnabled = undoManager.canRedo()
    }

    fun addHistoryEntry(path: TreePath, resetter: Resetter? = null) {
        if (!applyingState) {
            undoManager.addEdit(BrowserHistoryEntry(path, resetter))
            updateActions()
        }
    }

    private var applyingState = false

    private inner class BrowserHistoryEntry(val treePath: TreePath, val resetter: Resetter?) : AbstractUndoableEdit() {

        private var before: BrowserHistoryEntry? = null

        override fun canUndo(): Boolean = before != null
        override fun toString() = treePath.toString() + " / " + (resetter?.toString() ?: "null")

        override fun addEdit(newEdit: UndoableEdit): Boolean {
            // this is the last entry

            if (newEdit !is BrowserHistoryEntry) {
                return true
            }
            if (isEditMoreSpecificThan(newEdit)) {
                return true
            }
            newEdit.before = this
            return false
        }

        override fun replaceEdit(lastEdit: UndoableEdit): Boolean {
            // this is the new entry

            if (lastEdit !is BrowserHistoryEntry) {
                return false
            }
            if (isEditMoreSpecificThan(lastEdit)) {
                before = lastEdit.before
                return true
            }
            return false
        }

        private fun isEditMoreSpecificThan(otherEdit: BrowserHistoryEntry) = otherEdit.treePath == treePath && otherEdit.resetter == null

        override fun undo() {
            super.undo()
            before?.applyState()
        }

        override fun redo() {
            super.redo()
            applyState()
        }

        private fun applyState() {
            applyingState = true
            try {
                services.browserComponent.treePane.tree.apply {
                    selectionPath = treePath
                    scrollPathToVisible(treePath)
                }
                resetter?.reset()
                updateActions()
            } finally {
                applyingState = false
            }
        }
    }

    interface Resetter {
        fun reset()
    }

}
