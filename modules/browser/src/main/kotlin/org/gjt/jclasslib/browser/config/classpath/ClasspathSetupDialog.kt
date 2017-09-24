/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.BrowserFrame
import org.gjt.jclasslib.util.DefaultAction
import org.gjt.jclasslib.util.GUIHelper
import org.gjt.jclasslib.util.MultiFileFilter
import java.awt.BorderLayout
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import java.util.*
import javax.swing.*

class ClasspathSetupDialog(private val frame: BrowserFrame) : JDialog(frame) {

    private val listModel: DefaultListModel<ClasspathEntry> = DefaultListModel()
    private val lstElements: JList<ClasspathEntry> = JList(listModel).apply {
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        cellRenderer = ClasspathCellRenderer()
        addListSelectionListener {
            checkEnabledStatus()
        }
    }
    private val jreHomeTextField = JTextField()

    private val addAction = DefaultAction("Add classpath entry", "Add a classpath entry (INS)", "add.png") {
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            frame.classesChooserPath = fileChooser.currentDirectory.absolutePath
            val files = fileChooser.selectedFiles
            for (file in files) {
                val entry = if (file.isDirectory) {
                    ClasspathDirectoryEntry(file.path)
                } else {
                    ClasspathArchiveEntry(file.path)

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

        oldEntries
                .filterNot { newEntries.contains(it) }
                .forEach { config.removeClasspathEntry(it) }
        newEntries
                .filterNot { oldEntries.contains(it) }
                .forEach { config.addClasspathEntry(it) }

        config.classpath.apply {
            clear()
            addAll(newEntries)
        }
        config.jreHome = jreHomeTextField.text.trim()
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

    private val jreFileChooser: JFileChooser by lazy {
        JFileChooser(frame.classesChooserPath).apply {
            dialogTitle = "Choose the JRE home directory"
            fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
        }
    }

    init {
        setupComponent()
    }

    override fun setVisible(visible: Boolean) {
        if (visible) {
            updateData()
        }
        super.setVisible(visible)
    }

    private fun updateData() {
        listModel.clear()
        for (classpathEntry in frame.config.classpath) {
            listModel.addElement(classpathEntry)
        }
        jreHomeTextField.text = frame.config.jreHome
    }

    private fun setupComponent() {
        (contentPane as JComponent).apply {
            layout = MigLayout("wrap", "[grow]")
            add(createListPanel(), "pushy, grow")
            add(JLabel("JRE home:"), "split")
            add(jreHomeTextField, "grow")
            add(JButton("Choose").apply {
                addActionListener {
                    fun maybeNestedJre(file: File) = File(file, "jre").let { if (it.exists()) it else file }

                    if (jreFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                        jreHomeTextField.text = maybeNestedJre(jreFileChooser.selectedFile).path
                    }

                }
            }, "wrap para")
            add(okAction.createTextButton().apply {
                this@ClasspathSetupDialog.getRootPane().defaultButton = this

            }, "split, tag ok")
            add(cancelAction.createTextButton(), "tag cancel")
        }

        setSize(600, 400)
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

    private fun isInModel(entry: ClasspathEntry): Boolean = listModel.elements().toList().any { it == entry }

    private fun selectIndex(newSelectedIndex: Int) {
        val cappedSelectedIndex = minOf(newSelectedIndex, listModel.size - 1)
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
