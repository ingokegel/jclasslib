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
import org.gjt.jclasslib.util.ProgressDialog
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import java.awt.event.*
import javax.swing.*
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

class ClasspathBrowser(private val frame: BrowserMDIFrame, private val header: String, private val updateClassPathFromFrame: Boolean) : JDialog(frame) {

    private val classPathChangeListener = object : ClasspathChangeListener {
        override fun classpathChanged(event: ClasspathChangeEvent) {
            needsMerge = true
            if (event.isRemoval) {
                resetOnNextMerge = true
            }
        }
    }

    var classpathComponent: ClasspathComponent?  = null
        set(classpathComponent) {
            field?.removeClasspathChangeListener(classPathChangeListener)
            field = classpathComponent
            classpathComponent?.addClasspathChangeListener(classPathChangeListener)
            resetOnNextMerge = true
            needsMerge = true
            clear()
        }

    private val tree : JTree = JTree(ClassTreeNode()).apply {
        isRootVisible = false
        showsRootHandles = true
        putClientProperty("JTree.lineStyle", "Angled")

        addTreeSelectionListener {
            val selectionPath = selectionPath
            okAction.isEnabled = if (selectionPath != null) {
                !(selectionPath.lastPathComponent as ClassTreeNode).isPackageNode
            } else false

        }
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(event: MouseEvent) {
                if (event.clickCount == 2 && isValidDoubleClickPath(event)) {
                    okAction()
                }
            }
        })
    }

    private val setupAction = DefaultAction("Setup classpath") {
        frame.setupClasspathAction.actionPerformed(ActionEvent(this, 0, null))
        conditionalUpdate()
    }

    private val syncAction = DefaultAction("Synchronize") {
        sync(true)
    }

    private val okAction = DefaultAction("OK") {
        isVisible = false
    }.apply {
        isEnabled = false
    }

    private val cancelAction = DefaultAction("Cancel") {
        isVisible = false
    }.apply {
        accelerator(KeyEvent.VK_ESCAPE, 0)
        applyAcceleratorTo(contentPane as JComponent)
    }

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog(this, "Scanning classpath ...")
    }

    private var resetOnNextMerge: Boolean = false
    private var needsMerge: Boolean = false

    /**
     * Get the name of the selected class.
     * @return the name
     */
    val selectedClassName: String
        get() {
            val buffer = StringBuilder()
            val selectionPath = tree.selectionPath
            if (selectionPath != null) {
                for (i in 1..selectionPath.pathCount - 1) {
                    if (buffer.length > 0) {
                        buffer.append('/')
                    }
                    buffer.append(selectionPath.getPathComponent(i).toString())
                }
            }
            return buffer.toString()
        }

    init {
        setupComponent()
    }

    override fun setVisible(visible: Boolean) {
        if (visible) {
            if (updateClassPathFromFrame) {
                classpathComponent = frame.config
            }
        }
        super.setVisible(visible)
    }

    fun clear() {
        //The tree will not be synchronized automatically on the next setVisible.
        tree.model = DefaultTreeModel(ClassTreeNode())
    }

    private fun setupComponent() {
        (contentPane as JComponent).apply {
            border = GUIHelper.WINDOW_BORDER
            layout = GridBagLayout()

            add(JLabel(header), GridBagConstraints().apply {
                gridy = 0
                weightx = 1.0
                anchor = GridBagConstraints.NORTHWEST
            })
            add(JScrollPane(tree), GridBagConstraints().apply {
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

        setSize(450, 450)
        isModal = true
        title = "Choose a class"
        GUIHelper.centerOnParentWindow(this, owner)
        defaultCloseOperation = WindowConstants.DO_NOTHING_ON_CLOSE
    }

    private fun createButtonBox() = Box.createHorizontalBox().apply {
        if (updateClassPathFromFrame) {
            add(setupAction.createTextButton())
        }
        add(syncAction.createTextButton())
        add(Box.createHorizontalGlue())
        add(okAction.createTextButton().apply {
            this@ClasspathBrowser.getRootPane().defaultButton = this
        })
        add(cancelAction.createTextButton())

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(event: WindowEvent?) {
                cancelAction()
            }

            override fun windowActivated(e: WindowEvent?) {
                conditionalUpdate()
            }
        })
    }

    private fun conditionalUpdate() {
        if (needsMerge) {
            sync(resetOnNextMerge)
        }
    }

    private fun isValidDoubleClickPath(event: MouseEvent): Boolean {
        val locationPath = tree.getPathForLocation(event.x, event.y)
        val selectionPath = tree.selectionPath
        if (selectionPath == null || locationPath == null || selectionPath != locationPath) {
            return false
        }
        return !(selectionPath.lastPathComponent as ClassTreeNode).isPackageNode
    }

    private fun sync(reset: Boolean) {
        val model = if (reset) DefaultTreeModel(ClassTreeNode()) else tree.model as DefaultTreeModel
        progressDialog.task = {
            classpathComponent?.mergeClassesIntoTree(model, reset)
        }
        progressDialog.isVisible = true
        if (reset) {
            tree.model = model
        }
        tree.expandPath(TreePath(model.root))
        resetOnNextMerge = false
        needsMerge = false
    }
}
