/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.util.ICON_EMPTY
import java.awt.EventQueue
import java.awt.event.ActionListener
import java.io.File
import java.io.IOException
import java.util.*
import java.util.prefs.BackingStoreException
import java.util.prefs.Preferences
import javax.swing.JMenu
import javax.swing.JMenuItem
import javax.swing.event.MenuEvent
import javax.swing.event.MenuListener

class RecentMenu(private val frame: BrowserFrame) : JMenu() {

    private val recentWorkspaces = LinkedList<String>()

    init {
        text = getString("menu.recent.workspaces")
        icon = ICON_EMPTY
    }

    override fun menuSelectionChanged(isIncluded: Boolean) {
        super.menuSelectionChanged(isIncluded)
        updateContents()
    }

    fun addRecentWorkspace(file: File) {
        try {
            val fileName = file.canonicalFile.absolutePath
            recentWorkspaces.apply {
                remove(fileName)
                addFirst(fileName)
                if (size > RECENT_PROJECTS_MAX_SIZE) {
                    removeLast()
                }
            }
        } catch (_: IOException) {
        }
    }

    fun read(preferences: Preferences) {
        recentWorkspaces.clear()

        val numberToFile = TreeMap<Int, String>()
        val recentNode = preferences.node(SETTINGS_RECENT_WORKSPACES)
        try {
            for (key in recentNode.keys()) {
                val fileName = recentNode.get(key, null)
                if (fileName != null) {
                    numberToFile[Integer.parseInt(key)] = fileName
                }
            }
            recentWorkspaces.addAll(numberToFile.values)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun save(preferences: Preferences) {

        val recentNode = preferences.node(SETTINGS_RECENT_WORKSPACES)
        try {
            recentNode.clear()
        } catch (_: BackingStoreException) {
        }

        for ((count, fileName) in recentWorkspaces.withIndex()) {
            recentNode.put(count.toString(), fileName)
        }
    }

    fun addTo(parentMenu: JMenu) {
        parentMenu.addMenuListener(object: MenuListener {
            override fun menuSelected(e: MenuEvent) {
                updateContents()
            }

            override fun menuCanceled(e: MenuEvent) {
            }

            override fun menuDeselected(e: MenuEvent) {
            }
        })
        parentMenu.add(this)
    }

    private fun updateContents() {
        removeAll()
        if (recentWorkspaces.isNotEmpty()) {
            val workspaceOpenListener = ActionListener { event ->
                isPopupMenuVisible = false
                EventQueue.invokeLater { frame.openWorkspace(File((event.source as JMenuItem).text)) }
            }
            recentWorkspaces.forEach { fileName ->
                add(JMenuItem(fileName).apply {
                    addActionListener(workspaceOpenListener)
                })
            }
            addSeparator()
            add(JMenuItem(getString("action.clear.list")).apply {
                addActionListener {
                    recentWorkspaces.clear()
                }
            })
        } else {
            add(JMenuItem(getString("menu.empty.placeholder")).apply {
                isEnabled = false
            })
        }
    }

    companion object {
        private const val RECENT_PROJECTS_MAX_SIZE = 10
        private const val SETTINGS_RECENT_WORKSPACES = "recentWorkspaces"
    }
}
