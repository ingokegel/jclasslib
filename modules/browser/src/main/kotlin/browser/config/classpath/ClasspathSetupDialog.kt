/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import com.install4j.runtime.filechooser.DirectoryChooser
import com.install4j.runtime.filechooser.FileChooser
import com.install4j.runtime.filechooser.MultiFileFilter
import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserFrame
import org.gjt.jclasslib.util.DefaultAction
import org.gjt.jclasslib.util.applyPath
import org.gjt.jclasslib.util.StandardDialog
import org.gjt.jclasslib.util.scrollPaneFactory
import java.awt.BorderLayout
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.io.File
import javax.swing.*

class ClasspathSetupDialog(private val frame: BrowserFrame) : StandardDialog(frame, getString("window.setup.classpath")) {

    private val listModel: DefaultListModel<ClasspathEntry> = DefaultListModel()
    private val lstElements: JList<ClasspathEntry> = JList(listModel).apply {
        selectionMode = ListSelectionModel.SINGLE_SELECTION
        cellRenderer = ClasspathCellRenderer()
        addListSelectionListener {
            checkEnabledStatus()
        }
    }
    private val jreHomeTextField = JTextField()

    private val popupMenu = JPopupMenu().apply {
        add(DefaultAction(getString("action.classpath.add.jar")) {
            if (fileChooser.select()) {
                val files = fileChooser.selectedFiles
                for (file in files) {
                    addClasspathEntry(ClasspathArchiveEntry(file.path))
                    frame.classesChooserPath = file.parent
                }
            }
        })
        add(DefaultAction(getString("action.classpath.add.directory")) {
            if (directoryChooser.select()) {
                val file = directoryChooser.selectedDirectory
                addClasspathEntry(ClasspathDirectoryEntry(file.path))
                frame.classesChooserPath = file.parent
            }
        })
    }

    private fun addClasspathEntry(entry: ClasspathEntry) {
        if (!isInModel(entry)) {
            listModel.addElement(entry)
            selectIndex(listModel.size - 1)
        }
    }

    private val addButton: JButton = DefaultAction(getString("action.classpath.add.entry"), getString("action.classpath.add.entry.description"), "add.svg") { action ->
        action.lastButton?.let {
            val bounds = it.bounds
            popupMenu.show(it.parent, bounds.x, bounds.y + bounds.height)
        }
    }.apply {
        accelerator(KeyEvent.VK_INSERT, 0)
        applyAcceleratorTo(lstElements)
    }.createImageButton()

    private val removeAction = DefaultAction(getString("action.classpath.remove.entry"), getString("action.classpath.remove.entry.description"), "remove.svg") {
        val selectedIndex = lstElements.selectedIndex
        if (selectedIndex > -1) {
            listModel.remove(selectedIndex)
            selectIndex(selectedIndex)
        }
    }.apply {
        accelerator(KeyEvent.VK_DELETE, 0)
        applyAcceleratorTo(lstElements)
    }

    private val upAction = DefaultAction(getString("action.move.up"), getString("action.move.up.description"), "up.svg") {
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

    private val downAction = DefaultAction(getString("action.move.down"), getString("action.move.down.description"), "down.svg") {
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

    private val fileChooser: FileChooser by lazy {
        FileChooser.create()
                .parent(this)
                .title(getString("chooser.jar.files.title"))
                .applyPath(frame.classesChooserPath)
                .addFileFilter(MultiFileFilter("jar", getString("chooser.jar.files.and.directories.filter.name")))
                .multiple(true)
    }

    private val directoryChooser: DirectoryChooser by lazy {
        DirectoryChooser.create()
                .parent(this)
                .title(getString("chooser.directory.title"))
                .applyPath(frame.classesChooserPath)
    }

    private val jreFileChooser: DirectoryChooser by lazy {
        DirectoryChooser.create()
                .parent(this)
                .title(getString("chooser.jre.home.title"))
                .applyPath(frame.classesChooserPath)
    }

    init {
        setupComponent()
    }

    override fun doOk() {
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
        super.doOk()
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

    override fun addContent(component: JComponent) {
        with(component) {
            layout = MigLayout("wrap", "[grow]")
            add(createListPanel(), "pushy, grow")
            add(JLabel(getString("classpath.jre.home")), "split")
            add(jreHomeTextField, "grow")
            add(JButton(getString("action.choose")).apply {
                addActionListener {
                    fun maybeNestedJre(file: File) = File(file, "jre").let { if (it.exists()) it else file }

                    if (jreFileChooser.select()) {
                        jreHomeTextField.text = maybeNestedJre(jreFileChooser.selectedFile).path
                    }

                }
            }, "wrap para")
        }

        setSize(600, 400)
        checkEnabledStatus()
    }

    private fun createListPanel() = JPanel().apply {
        layout = BorderLayout()
        add(scrollPaneFactory(lstElements).apply {
            border = BorderFactory.createEtchedBorder()
        }, BorderLayout.CENTER)
        add(createModificationButtonBox(), BorderLayout.EAST)
    }

    private fun createModificationButtonBox() = Box.createVerticalBox().apply {
        add(addButton)
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
