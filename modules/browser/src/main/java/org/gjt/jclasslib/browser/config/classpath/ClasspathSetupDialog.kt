/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import org.gjt.jclasslib.browser.BrowserMDIFrame
import org.gjt.jclasslib.util.DefaultAction
import org.gjt.jclasslib.util.GUIHelper
import org.gjt.jclasslib.util.MultiFileFilter
import java.awt.BorderLayout
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.util.*
import javax.swing.*

class ClasspathSetupDialog(private val frame: BrowserMDIFrame) : JDialog(frame) {

    private val listModel: DefaultListModel<ClasspathEntry> = DefaultListModel()
    private val lstElements: JList<ClasspathEntry> = JList(listModel).apply {
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        cellRenderer = ClasspathCellRenderer()
        addListSelectionListener {
            checkEnabledStatus()
        }
    }

    private val addAction = DefaultAction("Add classpath entry", "Add a classpath entry (INS)", "add.png") {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            frame.classesChooserPath = fileChooser.currentDirectory.absolutePath
            val files = fileChooser.selectedFiles
            for (file in files) {
                val entry: ClasspathEntry
                if (file.isDirectory) {
                    entry = ClasspathDirectoryEntry()
                    entry.fileName = file.path
                } else {
                    entry = ClasspathArchiveEntry()
                    entry.fileName = file.path

                }
                if (!isInModel(entry)) {
                    listModel.addElement(entry)
                    selectIndex(listModel.size - 1)
                }
            }
        }
    }.apply {
        accelerator(KeyEvent.VK_INSERT, 0)
        applyAcceleratorTo(lstElements)
    }

    private val removeAction = DefaultAction("Remove classpath entry", "Remove a classpath entry (DEL)", "remove.png") {
        val selectedIndex = lstElements.selectedIndex
        if (selectedIndex > -1) {
            listModel.remove(selectedIndex)
            selectIndex(selectedIndex)
        }
    }.apply {
        accelerator(KeyEvent.VK_DELETE, 0)
        applyAcceleratorTo(lstElements)
    }

    private val upAction = DefaultAction("Move up", "Move a classpath entry up (ALT-UP)", "up.png") {
        val selectedIndex = lstElements.selectedIndex
        if (selectedIndex > 0) {
            val entry = listModel.remove(selectedIndex)
            val newSelectedIndex = selectedIndex - 1
            listModel.insertElementAt(entry, newSelectedIndex)
            selectIndex(newSelectedIndex)
        }

    }.apply {
        accelerator(KeyEvent.VK_UP, InputEvent.ALT_DOWN_MASK)
        applyAcceleratorTo(lstElements)
    }

    private val downAction = DefaultAction("down.png", "Move a classpath entry down (ALT-DOWN)", "down.png") {
        val selectedIndex = lstElements.selectedIndex
        if (selectedIndex < listModel.size - 1) {
            val entry = listModel.remove(selectedIndex)
            val newSelectedIndex = selectedIndex + 1
            listModel.insertElementAt(entry, newSelectedIndex)
            selectIndex(newSelectedIndex)
        }
    }.apply {
        accelerator(KeyEvent.VK_DOWN, InputEvent.ALT_DOWN_MASK)
        applyAcceleratorTo(lstElements)
    }

    private val okAction = DefaultAction("OK") {
        val newEntries = ArrayList<ClasspathEntry>()
        newEntries.addAll(listModel.elements().asSequence())
        val config = frame.config
        val oldEntries = ArrayList(config.classpath)

        for (oldEntry in oldEntries) {
            if (!newEntries.contains(oldEntry)) {
                config.removeClasspathEntry(oldEntry)
            }
        }
        for (newEntry in newEntries) {
            if (!oldEntries.contains(newEntry)) {
                config.addClasspathEntry(newEntry)
            }
        }

        config.classpath = newEntries
        isVisible = false
    }

    private val cancelAction = DefaultAction("Cancel") {
        isVisible = false
    }.apply {
        accelerator(KeyEvent.VK_ESCAPE, 0)
        applyAcceleratorTo(contentPane as JComponent)
    }

    private val fileChooser: JFileChooser by lazy {
        JFileChooser(frame.classesChooserPath).apply {
            dialogTitle = "Choose directory or jar file"
            fileFilter = MultiFileFilter("jar", "jar files and directories")
            fileSelectionMode = JFileChooser.FILES_AND_DIRECTORIES
            isMultiSelectionEnabled = true
        }
    }

    init {
        setupComponent()
    }

    override fun setVisible(visible: Boolean) {
        if (visible) {
            updateList()
        }
        super.setVisible(visible)
    }

    private fun updateList() {
        listModel.clear()
        for (classpathEntry in frame.config.classpath) {
            listModel.addElement(classpathEntry)
        }
    }

    private fun setupComponent() {
        (contentPane as JComponent).apply {
            border = GUIHelper.WINDOW_BORDER
            layout = GridBagLayout()
            add(JLabel("Classpath:"), GridBagConstraints().apply {
                gridy = 0
                weightx = 1.0
                anchor = GridBagConstraints.NORTHWEST
            })
            add(createListPanel(), GridBagConstraints().apply {
                gridy = 1
                insets = Insets(5, 0, 5, 0)
                weightx = 1.0
                weighty = 1.0
                fill = GridBagConstraints.BOTH
            })
            add(createButtonBox(), GridBagConstraints().apply {
                gridy = 2
                weightx = 1.0
                fill = GridBagConstraints.HORIZONTAL
            })
        }

        setSize(500, 300)
        isModal = true
        title = "Setup classpath"
        GUIHelper.centerOnParentWindow(this, owner)
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(event: WindowEvent?) {
                cancelAction()
            }
        })

        checkEnabledStatus()
    }

    private fun createListPanel() = JPanel().apply {
        layout = BorderLayout()
        add(JScrollPane(lstElements).apply {
            border = BorderFactory.createEtchedBorder()
        }, BorderLayout.CENTER)
        add(createModificationButtonBox(), BorderLayout.EAST)
    }

    private fun createModificationButtonBox() = Box.createVerticalBox().apply {
        add(addAction.createImageButton())
        add(removeAction.createImageButton())
        add(Box.createVerticalGlue())
        add(upAction.createImageButton())
        add(downAction.createImageButton())
    }

    private fun createButtonBox() = Box.createHorizontalBox().apply {
        add(Box.createHorizontalGlue())
        add(okAction.createTextButton().apply {
            this@ClasspathSetupDialog.getRootPane().defaultButton = this

        })
        add(cancelAction.createTextButton())
    }

    private fun isInModel(entry: ClasspathEntry): Boolean = listModel.elements().toList().any { it == entry }

    private fun selectIndex(newSelectedIndex: Int) {
        val cappedSelectedIndex = Math.min(newSelectedIndex, listModel.size - 1)
        if (cappedSelectedIndex > -1) {
            lstElements.selectedIndex = cappedSelectedIndex
            lstElements.ensureIndexIsVisible(cappedSelectedIndex)
            checkEnabledStatus()
        }
    }

    private fun checkEnabledStatus() {
        val selectedIndex = lstElements.selectedIndex
        removeAction.isEnabled = selectedIndex > -1
        upAction.isEnabled = selectedIndex > 0
        downAction.isEnabled = selectedIndex > -1 && selectedIndex < listModel.size - 1
    }
}
