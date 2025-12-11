/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath

import com.formdev.flatlaf.FlatClientProperties
import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserFrame
import org.gjt.jclasslib.util.*
import org.jetbrains.annotations.Nls
import java.awt.Cursor
import java.awt.event.*
import java.util.regex.Matcher
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.tree.DefaultTreeModel
import javax.swing.tree.TreePath

private val ICON_RETURN = BrowserFrame.getSvgIcon("return.svg")

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

    private val classPathTree: ClasspathTree = ClasspathTree(getString("classpath.tab.class.path"))
    private val modulePathTree: ClasspathTree = ClasspathTree(getString("classpath.tab.module.path"))
    private val trees = listOf(classPathTree, modulePathTree)

    private val tabbedPane = JTabbedPane().apply {
        for (tree in trees) {
            addTab(tree.name, scrollPaneFactory(tree))
        }
    }

    private val matchTypeDropDown = JComboBox(MatchType.entries.toTypedArray()).apply {
        selectedItem = MatchType.CONTAINS
        addActionListener {
            updateFilter()
        }
    }

    private val applyButton = JButton(ICON_RETURN).apply {
        putClientProperty(FlatClientProperties.BUTTON_TYPE, FlatClientProperties.BUTTON_TYPE_TOOLBAR_BUTTON)
        setCursor(Cursor.getDefaultCursor())
        addActionListener {
            updateFilter()
        }
    }

    private val filterTextField = JTextField().apply {
        putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, getString("enter.filter.expression"))
        putClientProperty(FlatClientProperties.TEXT_FIELD_TRAILING_COMPONENT, applyButton)
        putClientProperty(FlatClientProperties.TEXT_FIELD_SHOW_CLEAR_BUTTON, true)
        putClientProperty(FlatClientProperties.TEXT_FIELD_CLEAR_CALLBACK, Runnable {
            text = ""
            updateFilter()
        })
        addActionListener {
            updateFilter()
        }
        addKeyListener(object : KeyAdapter() {
            override fun keyPressed(e: KeyEvent) {
                when (e.keyCode) {
                    KeyEvent.VK_UP -> selectLeaf(true)
                    KeyEvent.VK_DOWN -> selectLeaf(false)
                }
            }
        })
        document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) {
                modified()
            }

            override fun removeUpdate(e: DocumentEvent) {
                modified()
            }

            override fun changedUpdate(e: DocumentEvent) {
                modified()
            }

            private fun modified() {
                applyButton.isVisible = true
            }
        })
    }

    private fun selectLeaf(last: Boolean) {
        getSelectedTree().apply {
            requestFocus()
            val rootNode = model.root as ClassTreeNode
            (if (last) rootNode.lastLeaf else rootNode.firstLeaf)?.let {
                selectionPath = TreePath(it.path)
                scrollPathToVisible(selectionPath)
            }
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
            setupAction.isEnabled = frame.vmConnection == null
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
            add(JLabel(getString("filter.class.name")), "split")
            add(matchTypeDropDown)
            add(filterTextField, "growx, wrap")

            if (updateClassPathFromFrame) {
                add(setupAction.createTextButton(), "tag help2")
            }
            add(syncAction.createTextButton(), "split, tag help2")
        }
        setSize(550, 550)
    }

    override fun conditionalUpdate() {
        if (needsMerge) {
            sync(resetOnNextMerge)
        }
    }

    override fun windowOpened() {
        super.windowOpened()
        filterTextField.requestFocusInWindow()
    }

    private fun sync(reset: Boolean) {
        val classPathModel = getUnfilteredModel(reset, classPathTree)
        val modulePathModel = getUnfilteredModel(reset, modulePathTree)
        resetOnNextMerge = false
        needsMerge = false

        progressDialog.task = {
            classpathComponent?.mergeClassesIntoTree(classPathModel, modulePathModel, reset)
        }
        progressDialog.isVisible = true
        if (reset) {
            classPathTree.unfilteredModel = classPathModel
            modulePathTree.unfilteredModel = modulePathModel
        }
        updateFilter()
    }

    private fun updateFilter() {
        val filterText = filterTextField.text.trim()
        for (tree in trees) {
            if (filterText.isEmpty()) {
                tree.showUnfiltered()
            } else {
                tree.showFiltered(filterText, matchTypeDropDown.selectedItem as MatchType)
            }
        }
        applyButton.isVisible = false
    }

    private fun getUnfilteredModel(reset: Boolean, tree: ClasspathTree) =
        if (reset) DefaultTreeModel(ClassTreeNode()) else tree.unfilteredModel

    private fun getSelectedTree(): ClasspathTree = trees[tabbedPane.selectedIndex]

    private inner class ClasspathTree(@Nls treeName: String) : JTree(ClassTreeNode()) {
        var unfilteredModel: DefaultTreeModel = model as DefaultTreeModel

        init {
            name = treeName
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

        override fun isRootVisible() = false
        override fun getShowsRootHandles() = true

        private fun isValidDoubleClickPath(event: MouseEvent): Boolean {
            val locationPath = getPathForLocation(event.x, event.y)
            val selectionPath = selectionPath
            if (selectionPath == null || locationPath == null || selectionPath != locationPath) {
                return false
            }
            return !(selectionPath.lastPathComponent as ClassTreeNode).isPackageNode
        }

        fun showUnfiltered() {
            if (model != unfilteredModel) {
                model = unfilteredModel
            }
            expandRoot()
        }

        fun showFiltered(filterText: String, matchType: MatchType) {
            val matcher = matchType.createMatcher(filterText)
            val originalRoot = unfilteredModel.root as ClassTreeNode
            val filteredRoot = filterNodeRecursive(originalRoot, filterText, matchType, matcher)
            model = DefaultTreeModel(filteredRoot)
            expandAll()
        }

        private fun filterNodeRecursive(
            originalNode: ClassTreeNode,
            filterText: String,
            matchType: MatchType,
            matcher: Matcher?
        ): ClassTreeNode? {
            val nodeText = originalNode.userObject as String?
            val matches = nodeText != null && !originalNode.isPackageNode && matchType.matches(nodeText, filterText, matcher)
            val matchingChildren = mutableListOf<ClassTreeNode>()
            val children = originalNode.children()
            while (children.hasMoreElements()) {
                val child = children.nextElement() as ClassTreeNode
                val filteredChild = filterNodeRecursive(child, filterText, matchType, matcher)
                if (filteredChild != null) {
                    matchingChildren.add(filteredChild)
                }
            }

            return if (matches || matchingChildren.isNotEmpty()) {
                val newNode = if (nodeText == null) ClassTreeNode() else ClassTreeNode(nodeText, originalNode.isPackageNode)
                matchingChildren.forEach {
                    newNode.add(it)
                }
                newNode
            } else {
                null
            }
        }
    }
}
