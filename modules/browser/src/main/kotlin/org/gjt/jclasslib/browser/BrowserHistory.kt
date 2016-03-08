/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import java.util.*
import javax.swing.tree.TreePath


class BrowserHistory(private val services: BrowserServices) {

    private val history = LinkedList<BrowserHistoryEntry>()
    private var historyPointer = -1

    fun clear() {
        history.clear()
        historyPointer = -1
    }

    fun historyBackward() {
        if (historyPointer == 0) {
            return
        }
        historyPointer--
        syncWithHistory()
    }

    fun historyForward() {
        if (historyPointer == history.size - 1) {
            return
        }
        historyPointer++
        syncWithHistory()
    }

    fun updateActions() {
        services.backwardAction.isEnabled = historyPointer > 0
        services.forwardAction.isEnabled = historyPointer < history.size - 1
    }

    fun updateHistory(newPath: TreePath, offset: Int? = null) {
        val newEntry = BrowserHistoryEntry(newPath, offset)
        if (!checkForOffset(newEntry) && (historyPointer < 0 || newEntry != history[historyPointer])) {
            eliminateForwardEntries()

            if (historyPointer > MAX_HISTORY_ENTRIES) {
                history.removeFirst()
                historyPointer--
            }

            history.add(newEntry)
            historyPointer++

        }
        updateActions()
    }

    private fun checkForOffset(newEntry: BrowserHistoryEntry): Boolean {
        if (historyPointer >= 0) {
            val currentEntry = history[historyPointer]
            if (currentEntry.treePath == newEntry.treePath) {
                if (newEntry.offset == null) {
                    // Ignore history event, since it is more unspecific than the current one
                } else if (currentEntry.offset == null) {
                    // merge with current entry to achieve more specific history entry
                    eliminateForwardEntries()
                    currentEntry.offset = newEntry.offset
                    // Do not add another history event
                    return true
                }
            }
        }
        return false
    }

    private fun eliminateForwardEntries() {
        if (historyPointer < history.size - 1) {
            val it = history.listIterator(historyPointer + 1)
            while (it.hasNext()) {
                it.next()
                it.remove()
            }
        }
    }

    private fun syncWithHistory() {
        val entry = history[historyPointer]
        services.browserComponent.treePane.tree.apply {
            selectionPath = entry.treePath
            scrollPathToVisible(entry.treePath)
        }

        val offset = entry.offset
        if (offset != null) {
            services.browserComponent.detailPane.attributeDetailPane.codeAttributeDetailPane.apply {
                selectByteCodeDetailPane()
                byteCodeDetailPane.scrollToOffset(offset)
            }
        }
        updateActions()
    }

    private inner class BrowserHistoryEntry(val treePath: TreePath, var offset: Int?) {

        override fun toString(): String {
            return treePath.toString() + " / offset " + (offset?.toString() ?: "null")
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other?.javaClass != javaClass) return false

            other as BrowserHistoryEntry

            if (treePath != other.treePath) return false
            if (offset != other.offset) return false

            return true
        }

        override fun hashCode(): Int {
            return 31 * treePath.hashCode() + (offset ?: 0)
        }
    }

    companion object {
        private val MAX_HISTORY_ENTRIES = 50
    }
}
