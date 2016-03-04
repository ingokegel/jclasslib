/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.config.BrowserConfig
import org.gjt.jclasslib.browser.config.classpath.ClasspathArchiveEntry
import org.gjt.jclasslib.browser.config.classpath.ClasspathBrowser
import org.gjt.jclasslib.browser.config.classpath.ClasspathSetupDialog
import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.util.DefaultAction
import org.gjt.jclasslib.util.GUIHelper
import org.gjt.jclasslib.util.MultiFileFilter
import java.awt.*
import java.awt.datatransfer.DataFlavor
import java.awt.event.InputEvent
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.beans.XMLDecoder
import java.beans.XMLEncoder
import java.io.*
import java.util.prefs.Preferences
import javax.swing.*

class BrowserMDIFrame : JFrame() {

    private val frameContent: FrameContent = FrameContent(this)

    val openClassFileAction = DefaultAction("Open class file", "Open a class file", "open_small.png", "open_large.png") {
        val result = classesFileChooser.showOpenDialog(this)
        if (result == JFileChooser.APPROVE_OPTION) {
            repaintNow()
            withWaitCursor {
                classesChooserPath = classesFileChooser.currentDirectory.absolutePath
                val file = classesFileChooser.selectedFile
                val lowerCasePath = file.path.toLowerCase()
                if (lowerCasePath.endsWith(".class")) {
                    openClassFromFile(file)
                } else if (lowerCasePath.endsWith(".jar")) {
                    openClassFromJar(file)
                } else {
                    GUIHelper.showMessage(this, "Please select a class file or a JAR file", JOptionPane.WARNING_MESSAGE)
                }
            }
        }
    }

    val browseClasspathAction = DefaultAction("Browse classpath", "Browse the current classpath to open a class file", "tree_small.png", "tree_large.png") {
        classpathBrowser.isVisible = true
        val selectedClassName = classpathBrowser.selectedClassName
        if (!selectedClassName.isEmpty()) {
            val findResult = config.findClass(selectedClassName)
            if (findResult != null) {
                repaintNow()
                withWaitCursor {
                    frameContent.openClassFile(findResult.fileName)
                }
            } else {
                GUIHelper.showMessage(this, "Error loading " + selectedClassName, JOptionPane.ERROR_MESSAGE)
            }
        }
    }

    val setupClasspathAction = DefaultAction("Setup classpath", "Configure the classpath") {
        classpathSetupDialog.isVisible = true
    }

    val newWindowAction = DefaultAction("New window", "Open a new window") {
        BrowserMDIFrame().isVisible = true
    }.apply {
        accelerator(KeyEvent.VK_N)
    }

    val newWorkspaceAction = DefaultAction("New workspace", "Close all class files and open a new workspace") {
        frameContent.closeAllTabs()
        workspaceFile = null
        config = browserConfigWithRuntimeLib()
        updateTitle()
    }

    val openWorkspaceAction = DefaultAction("Open workspace", "Open workspace from disk", "open_ws_small.png", "open_ws_large.png") {
        val result = workspaceFileChooser.showOpenDialog(this)
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = workspaceFileChooser.selectedFile
            openWorkspace(selectedFile)
            workspaceChooserPath = workspaceFileChooser.currentDirectory.absolutePath
        }
    }

    val saveWorkspaceAction = DefaultAction("Save workspace", "Save current workspace to disk", "save_ws_small.png", "save_ws_large.png") {
        val workspaceFile = this.workspaceFile
        if (workspaceFile != null) {
            saveWorkspaceToFile(workspaceFile)
        } else {
            saveWorkspace()
        }
    }

    val saveWorkspaceAsAction = DefaultAction("Save workspace as", "Save current workspace to a different file") {
        saveWorkspace()
    }.apply {
        disabled()
    }

    val quitAction = DefaultAction("Quit") {
        saveWindowSettings()
        exit()
    }

    val closeAction = DefaultAction("Close window") {
        saveWindowSettings()
        isVisible = false
        dispose()
        if (getBrowserFrames().size == 0) {
            exit()
        }
    }.apply {
        accelerator(KeyEvent.VK_W)
    }

    val backwardAction = DefaultAction("Backward", "Move backward in the navigation history", "browser_backward_small.png", "browser_backward_large.png") {
        frameContent.selectedTab?.browserComponent?.history?.historyBackward()
    }.apply {
        disabled()
        accelerator(KeyEvent.VK_LEFT, InputEvent.ALT_DOWN_MASK)
    }

    val forwardAction = DefaultAction("Forward", "Move backward in the navigation history", "browser_forward_small.png", "browser_forward_large.png") {
        frameContent.selectedTab?.browserComponent?.history?.historyForward()
    }.apply {
        disabled()
        accelerator(KeyEvent.VK_RIGHT, InputEvent.ALT_DOWN_MASK)
    }

    var reloadAction = DefaultAction("Reload", "Reload class file", "reload_small.png", "reload_large.png") {
        try {
            frameContent.selectedTab?.reload()
        } catch (e: IOException) {
            GUIHelper.showMessage(this, e.message, JOptionPane.ERROR_MESSAGE)
        }

    }.apply {
        disabled()
        accelerator(KeyEvent.VK_R)
    }

    val showHomepageAction = DefaultAction("jclasslib on the web", "Visit jclasslib on the web", "web_small.png", "web_large.png") {
        GUIHelper.showURL("http://www.ej-technologies.com/products/jclasslib/overview.html")
    }

    val showEjtAction = DefaultAction("ej-technologies on the web", "Visit ej-technologies on the web", "web_small.png") {
        GUIHelper.showURL("http://www.ej-technologies.com")
    }

    val showHelpAction = DefaultAction("Show help", "Show the jclasslib documentation", "help.png") {
        GUIHelper.showURL(File("doc/help.html").canonicalFile.toURI().toURL().toExternalForm())
    }.apply {
        accelerator(KeyEvent.VK_F1, 0)
    }

    val aboutAction = DefaultAction("About the jclasslib bytecode viewer", "Show the jclasslib documentation") {
        BrowserAboutDialog(this).isVisible = true
    }

    val previousWindowAction = DefaultAction("Previous window", "Cycle to the previous open window") {
        getPreviousBrowserFrame(this).toFront()
    }.apply {
        accelerator(KeyEvent.VK_F2)
    }

    val nextWindowAction = DefaultAction("Next window", "Cycle to the next open window") {
        getNextBrowserFrame(this).toFront()
    }.apply {
        accelerator(KeyEvent.VK_F3)
    }

    val splitActions = SplitMode.values().associate { splitMode ->
        splitMode to DefaultAction(splitMode.actionName, splitMode.actionDescription) {
            frameContent.split(splitMode)
        }.apply {
            accelerator(splitMode.accelator, DefaultAction.MENU_MODIFIER or KeyEvent.SHIFT_MASK)
        }
    }

    val windowMenu = JMenu("Window").apply {
        add(previousWindowAction)
        add(nextWindowAction)
        addSeparator()
        splitActions.values.forEach {
            add(it)
        }
    }

    private val normalFrameBounds: Rectangle
        get() = if (isMaximized) getDefaultFrameBounds() else bounds

    private val isMaximized: Boolean
        get() = extendedState and Frame.MAXIMIZED_BOTH != 0

    private var workspaceFile: File? = null
    private var workspaceChooserPath = ""
    var classesChooserPath = ""
    var config: BrowserConfig = browserConfigWithRuntimeLib()
        private set


    private val workspaceFileChooser: JFileChooser by lazy {
        JFileChooser(workspaceChooserPath).apply {
            dialogTitle = "Choose workspace file"
            fileFilter = MultiFileFilter(WORKSPACE_FILE_SUFFIX, "jclasslib workspace files")
        }
    }

    private val classesFileChooser: JFileChooser by lazy {
        JFileChooser(workspaceChooserPath).apply {
            dialogTitle = "Choose class file or jar file"
            addChoosableFileFilter(MultiFileFilter("class", "class files"))
            addChoosableFileFilter(MultiFileFilter("jar", "jar files"))
            fileFilter = MultiFileFilter(listOf("class", "jar"), "class files and jar files")
        }
    }

    private val recentMenu: RecentMenu = RecentMenu(this)
    private val classpathSetupDialog: ClasspathSetupDialog by lazy { ClasspathSetupDialog(this) }
    private val classpathBrowser: ClasspathBrowser by lazy { ClasspathBrowser(this, "Configured classpath:", true) }
    private val jarBrowser: ClasspathBrowser by lazy { ClasspathBrowser(this, "Classes in selected JAR file:", false) }

    private fun browserConfigWithRuntimeLib() = BrowserConfig().apply { addRuntimeLib() }


    fun openWorkspace(file: File) {
        repaintNow()
        withWaitCursor {
            frameContent.closeAllTabs()
            val decoder = XMLDecoder(FileInputStream(file))
            config = decoder.readObject() as BrowserConfig
            frameContent.applyMDIConfig(config.mdiConfig)
            decoder.close()
            recentMenu.addRecentWorkspace(file)
        }
        workspaceFile = file
        updateTitle()
        saveWorkspaceAsAction.isEnabled = true
    }

    private fun openClassFromFile(file: File): BrowserTab {
        val tab = frameContent.openClassFile(file.path)
        val classFile = tab.classFile
        try {
            val pathComponents = classFile.thisClassName.split("/".toRegex())
            val currentDirectory = findClassPathRootDirectory(file, pathComponents)
            if (currentDirectory != null) {
                config.addClasspathDirectory(currentDirectory.path)
            }
        } catch (e: InvalidByteCodeException) {
        }
        return tab
    }

    private fun findClassPathRootDirectory(file: File, pathComponents: List<String>): File? {
        var currentDirectory = file.parentFile
        for (i in pathComponents.size - 2 downTo 0) {
            if (currentDirectory.name != pathComponents[i]) {
                return null
            }
            currentDirectory = currentDirectory.parentFile
        }
        return currentDirectory
    }

    fun openExternalFile(path: String) {
        EventQueue.invokeLater {
            val file = File(path)
            if (file.exists()) {
                if (path.toLowerCase().endsWith("." + WORKSPACE_FILE_SUFFIX)) {
                    openWorkspace(file)
                } else if (path.toLowerCase().endsWith(".class")) {
                    try {
                        openClassFromFile(file)
                    } catch (e: IOException) {
                        GUIHelper.showMessage(this@BrowserMDIFrame, e.message, JOptionPane.ERROR_MESSAGE)
                    }
                }
            }
        }
    }


    private fun setupMenu() {
        jMenuBar = JMenuBar().apply {

            add(JMenu("File").apply {
                add(openClassFileAction)
                addSeparator()
                add(newWindowAction)
                add(newWorkspaceAction)
                add(openWorkspaceAction)
                add(recentMenu)
                addSeparator()
                add(saveWorkspaceAction)
                add(saveWorkspaceAsAction)
                addSeparator()
                add(showHomepageAction)
                add(showEjtAction)
                addSeparator()
                add(closeAction)
                add(quitAction)
            })

            add(JMenu("Classpath").apply {
                add(browseClasspathAction)
                add(setupClasspathAction)
            })

            add(JMenu("Browse").apply {
                add(backwardAction)
                add(forwardAction)

                addSeparator()
                add(reloadAction)
            })

            add(windowMenu)

            add(JMenu("Help").apply {
                add(showHelpAction)
                add(aboutAction)
            })
        }

    }

    private fun setupWindowBounds() {
        val activeBrowserFrame = getActiveBrowserFrame()
        if (activeBrowserFrame != null) {
            bounds = activeBrowserFrame.normalFrameBounds.apply {
                x += NEW_FRAME_OFFSET
                y += NEW_FRAME_OFFSET
            }
        } else {
            loadWindowSettings()
        }
    }

    private fun setupFrame() {
        defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE

        val contentPane = contentPane as JComponent
        contentPane.layout = BorderLayout(5, 5)
        contentPane.add(frameContent, BorderLayout.CENTER)
        contentPane.add(buildToolbar(), BorderLayout.NORTH)
        iconImages = listOf(ICON_APPLICATION_16, ICON_APPLICATION_32).map { it.image }

        contentPane.transferHandler = object : TransferHandler() {
            override fun canImport(support: TransferHandler.TransferSupport): Boolean {
                val supported = support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
                if (supported) {
                    support.dropAction = TransferHandler.COPY
                }
                return supported
            }

            override fun importData(support: TransferHandler.TransferSupport): Boolean {
                val transferable = support.transferable
                val flavors = transferable.transferDataFlavors
                for (flavor in flavors) {
                    try {
                        if (flavor.isFlavorJavaFileListType) {
                            @Suppress("UNCHECKED_CAST")
                            (transferable.getTransferData(flavor) as List<File>).forEach {
                                openExternalFile(it.path)
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
                return false
            }
        }

    }

    private fun updateTitle() {
        val workspaceFile = this.workspaceFile
        if (workspaceFile == null) {
            title = APPLICATION_TITLE
            saveWorkspaceAsAction.isEnabled = false
        } else {
            title = APPLICATION_TITLE + " [" + workspaceFile.name + "]"
        }
    }

    private fun buildToolbar(): JToolBar = JToolBar().apply {
        add(openClassFileAction)
        add(browseClasspathAction)
        addSeparator()
        add(openWorkspaceAction)
        add(saveWorkspaceAction)
        addSeparator()
        add(backwardAction)
        add(forwardAction)
        addSeparator()
        add(reloadAction)
        addSeparator()
        add(showHomepageAction)

        isFloatable = false
    }

    private fun repaintNow() {
        val contentPane = contentPane as JComponent
        contentPane.paintImmediately(0, 0, contentPane.width, contentPane.height)
        val menuBar = jMenuBar
        menuBar.paintImmediately(0, 0, menuBar.width, menuBar.height)
    }

    private fun loadSettings() {
        Preferences.userNodeForPackage(javaClass).apply {
            workspaceChooserPath = get(SETTINGS_WORKSPACE_CHOOSER_PATH, workspaceChooserPath)
            classesChooserPath = get(SETTINGS_CLASSES_CHOOSER_PATH, classesChooserPath)
            recentMenu.read(this)
        }
    }

    private fun saveWorkspace() {
        val fileChooser = workspaceFileChooser
        val result = fileChooser.showSaveDialog(this)
        if (result == JFileChooser.APPROVE_OPTION) {
            val selectedFile = getWorkspaceFile(fileChooser.selectedFile)
            if (!selectedFile.exists() || GUIHelper.showOptionDialog(this,
                    "The file " + selectedFile.path + "\nexists. Do you want to overwrite this file?",
                    GUIHelper.YES_NO_OPTIONS,
                    JOptionPane.QUESTION_MESSAGE) == 0) {
                saveWorkspaceToFile(selectedFile)
                workspaceFile = selectedFile
                updateTitle()
                workspaceChooserPath = fileChooser.currentDirectory.absolutePath
            }
        }
    }

    private fun getWorkspaceFile(selectedFile: File): File {
        if (!selectedFile.name.toLowerCase().endsWith("." + WORKSPACE_FILE_SUFFIX)) {
            return File(selectedFile.path + "." + WORKSPACE_FILE_SUFFIX)
        } else {
            return selectedFile
        }
    }

    private fun saveWorkspaceToFile(file: File) {
        config.mdiConfig = frameContent.createMDIConfig()
        try {
            val fos = FileOutputStream(file)
            val encoder = XMLEncoder(fos)
            encoder.writeObject(config)
            encoder.close()
            recentMenu.addRecentWorkspace(file)
        } catch (e: FileNotFoundException) {
            GUIHelper.showMessage(this, "An error occurred while saving to " + file.path, JOptionPane.ERROR_MESSAGE)
        }

        GUIHelper.showMessage(this, "Workspace saved to " + file.path, JOptionPane.INFORMATION_MESSAGE)
        saveWorkspaceAsAction.isEnabled = true
    }

    private fun openClassFromJar(file: File): BrowserTab? {
        val entry = ClasspathArchiveEntry().apply { fileName = file.path }
        jarBrowser.apply {
            clear()
            classpathComponent = entry
            isVisible = true
        }
        val selectedClassName = jarBrowser.selectedClassName
        val fileName = file.path + "!" + selectedClassName + ".class"
        val tab = frameContent.openClassFile(fileName)
        config.addClasspathArchive(file.path)
        return tab
    }

    private fun withWaitCursor(function: () -> Unit) {
        cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
        try {
            function.invoke()
        } catch (e: FileNotFoundException) {
            GUIHelper.showMessage(this, "File not found: " + e.message, JOptionPane.ERROR_MESSAGE)
        } catch (e: IOException) {
            GUIHelper.showMessage(this, e.message, JOptionPane.ERROR_MESSAGE)
        } finally {
            cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)
        }
    }

    private fun setupEventHandlers() {
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(event: WindowEvent) {
                closeAction()
            }
        })
    }

    private fun saveWindowSettings() {
        val preferences = Preferences.userNodeForPackage(javaClass)
        preferences.putBoolean(SETTINGS_WINDOW_MAXIMIZED, isMaximized)
        preferences.apply {
            if (!isMaximized) {
                val frameBounds = bounds
                putInt(SETTINGS_WINDOW_WIDTH, frameBounds.width)
                putInt(SETTINGS_WINDOW_HEIGHT, frameBounds.height)
                putInt(SETTINGS_WINDOW_X, frameBounds.x)
                putInt(SETTINGS_WINDOW_Y, frameBounds.y)
            }

            put(SETTINGS_WORKSPACE_CHOOSER_PATH, workspaceChooserPath)
            put(SETTINGS_CLASSES_CHOOSER_PATH, classesChooserPath)
            recentMenu.save(this)
        }
    }

    private fun loadWindowSettings() {

        val preferences = Preferences.userNodeForPackage(javaClass)
        val defaultFrameBounds = getDefaultFrameBounds()

        val windowX = preferences.getInt(SETTINGS_WINDOW_X, defaultFrameBounds.x)
        val windowY = preferences.getInt(SETTINGS_WINDOW_Y, defaultFrameBounds.y)
        val windowWidth = preferences.getInt(SETTINGS_WINDOW_WIDTH, defaultFrameBounds.width)
        val windowHeight = preferences.getInt(SETTINGS_WINDOW_HEIGHT, defaultFrameBounds.height)

        val frameBounds = Rectangle(windowX, windowY, windowWidth, windowHeight)
        // sanitize frame bounds
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        val screenBounds = Rectangle(screenSize)
        frameBounds.translate(-Math.min(0, frameBounds.x), -Math.min(0, frameBounds.y))
        frameBounds.translate(-Math.max(0, frameBounds.x + frameBounds.width - screenSize.width), -Math.max(0, frameBounds.y + frameBounds.height - screenSize.height))

        bounds = screenBounds.intersection(frameBounds)

        if (preferences.getBoolean(SETTINGS_WINDOW_MAXIMIZED, false)) {
            extendedState = Frame.MAXIMIZED_BOTH
        }

    }

    private fun getDefaultFrameBounds(): Rectangle {
        val screenSize = Toolkit.getDefaultToolkit().screenSize
        return Rectangle(
                (screenSize.getWidth() - DEFAULT_WINDOW_WIDTH).toInt() / 2,
                (screenSize.getHeight() - DEFAULT_WINDOW_HEIGHT).toInt() / 2,
                DEFAULT_WINDOW_WIDTH,
                DEFAULT_WINDOW_HEIGHT
        )
    }

    init {
        loadSettings()
        setupMenu()
        setupEventHandlers()
        setupWindowBounds()
        setupFrame()
        updateTitle()
    }

    companion object {

        private val SETTINGS_WORKSPACE_CHOOSER_PATH = "workspaceChooserPath"
        private val SETTINGS_CLASSES_CHOOSER_PATH = "classesChooserPath"
        private val DEFAULT_WINDOW_WIDTH = 800
        private val DEFAULT_WINDOW_HEIGHT = 600
        private val NEW_FRAME_OFFSET = 22

        private val SETTINGS_WINDOW_WIDTH = "windowWidth"
        private val SETTINGS_WINDOW_HEIGHT = "windowHeight"
        private val SETTINGS_WINDOW_X = "windowX"
        private val SETTINGS_WINDOW_Y = "windowY"
        private val SETTINGS_WINDOW_MAXIMIZED = "windowMaximized"

        private val icons = hashMapOf<String, ImageIcon>()
        val ICON_APPLICATION_16 = getIcon("jclasslib_16.png")
        val ICON_APPLICATION_32 = getIcon("jclasslib_32.png")


        fun getIcon(fileName: String): ImageIcon =
                icons.getOrPut(fileName) {
                    ImageIcon(BrowserMDIFrame::class.java.getResource("images/" + fileName))
                }

    }
}
