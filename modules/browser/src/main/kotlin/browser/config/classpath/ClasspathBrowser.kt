/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserFrame
import org.gjt.jclasslib.util.DefaultAction
import org.gjt.jclasslib.util.ProgressDialog
import org.gjt.jclasslib.util.StandardDialog
import org.gjt.jclasslib.util.scrollPaneFactory
import org.jetbrains.annotations.Nls
import java.awt.event.ActionEvent
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JComponent
import javax.swing.JTabbedPane
import javax.swing.JTree
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

class ClasspathBrowser(private val frame: BrowserFrame, @Nls title: String, private val updateClassPathFromFrame: Boolean) : StandardDialog(frame, title) {

    var classpathComponent: ClasspathComponent? = null
        set(classpathComponent) {
            if (field != classpathComponent) {
                resetOnNextMerge = field.let { it == null || classpathComponent?.contains(it) == false }
                needsMerge = true
                field = classpathComponent
                if (resetOnNextMerge) {
                    clear()
                }
            }
        }

    private val classPathTree: JTree = createTree(getString("classpath.tab.class.path"))
    private val modulePathTree: JTree = createTree(getString("classpath.tab.module.path"))
    private val trees = listOf(classPathTree, modulePathTree)

    private val tabbedPane = JTabbedPane().apply {
        for (tree in trees) {
            addTab(tree.name, scrollPaneFactory(tree))
        }
    }

    private fun createTree(@Nls treeName: String): JTree {
        return JTree(ClassTreeNode()).apply {
            name = treeName
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
                    if (event.clickCount == 2 && isValidDoubleClickPath(event, this@apply)) {
                        okAction()
                    }
                }
            })
        }
    }

    private val setupAction = DefaultAction(getString("action.setup.class.path")) {
        frame.setupClasspathAction.actionPerformed(ActionEvent(this, 0, null))
        classpathComponent = frame.config.toImmutableContainer()
        conditionalUpdate()
    }

    private val syncAction = DefaultAction(getString("action.synchronize")) {
        sync(true)
    }

    private val progressDialog: ProgressDialog by lazy {
        ProgressDialog(this, getString("message.scanning.classes"))
    }

    private var resetOnNextMerge: Boolean = false
    private var needsMerge: Boolean = false

    /**
     * Get the name of the selected class.
     * @return the name
     */
    val selectedClassNames: Collection<String>
        get() {
            val selectionPaths = getSelectedTree().selectionPaths?.toList() ?: emptyList()
            return selectionPaths.map { selectionPath ->
                val buffer = StringBuilder()
                for (i in 1 until selectionPath.pathCount) {
                    if (buffer.isNotEmpty()) {
                        buffer.append('/')
                    }
                    buffer.append(selectionPath.getPathComponent(i).toString())
                }
                buffer.toString()
            }
        }

    init {
        setupComponent()
        okAction.isEnabled = false
    }

    fun isModulePathSelection(): Boolean = getSelectedTree() == modulePathTree

    override fun setVisible(visible: Boolean) {
        if (visible) {
            if (updateClassPathFromFrame) {
                classpathComponent = frame.classpathComponent
            }
            setupAction.isEnabled = frame.vmConnection != null
        }
        super.setVisible(visible)
    }

    fun clear() {
        //The trees will not be synchronized automatically on the next setVisible.
        for (tree in trees) {
            tree.model = DefaultTreeModel(ClassTreeNode())
        }
    }

    override fun addContent(component: JComponent) {
        with(component) {
            layout = MigLayout("wrap", "[grow]")

            add(tabbedPane, "grow, pushy")

            if (updateClassPathFromFrame) {
                add(setupAction.createTextButton(), "tag help2")
            }
            add(syncAction.createTextButton(), "split, tag help2")
        }
        setSize(450, 450)
    }

    override fun conditionalUpdate() {
        if (needsMerge) {
            sync(resetOnNextMerge)
        }
    }

    private fun isValidDoubleClickPath(event: MouseEvent, tree: JTree): Boolean {
        val locationPath = tree.getPathForLocation(event.x, event.y)
        val selectionPath = tree.selectionPath
        if (selectionPath == null || locationPath == null || selectionPath != locationPath) {
            return false
        }
        return !(selectionPath.lastPathComponent as ClassTreeNode).isPackageNode
    }

    private fun sync(reset: Boolean) {
        val classPathModel = getModel(reset, classPathTree)
        val modulePathModel = getModel(reset, modulePathTree)
        resetOnNextMerge = false
        needsMerge = false

        progressDialog.task = {
            classpathComponent?.mergeClassesIntoTree(classPathModel, modulePathModel, reset)
        }
        progressDialog.isVisible = true
        if (reset) {
            classPathTree.applyModel(classPathModel)
            modulePathTree.applyModel(modulePathModel)
        }
    }

    private fun JTree.applyModel(model: DefaultTreeModel) {
        this.model = model
        expandPath(TreePath(model.root))
    }

    private fun getModel(reset: Boolean, tree: JTree) = if (reset) DefaultTreeModel(ClassTreeNode()) else tree.model as DefaultTreeModel

    private fun getSelectedTree(): JTree = trees[tabbedPane.selectedIndex]
}
