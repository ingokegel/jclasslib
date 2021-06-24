/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

@file:JvmName("BrowserApplication")

package org.gjt.jclasslib.browser

import com.exe4j.runtime.util.LazyFileOutputStream
import com.formdev.flatlaf.FlatDarkLaf
import com.formdev.flatlaf.FlatLightLaf
import com.install4j.api.Util
import com.install4j.api.launcher.StartupNotification
import com.install4j.runtime.installer.platform.macos.MacosUiHelper
import org.gjt.jclasslib.util.darkMode
import java.awt.EventQueue
import java.awt.Frame
import java.awt.Toolkit
import java.awt.Window
import java.io.BufferedOutputStream
import java.io.File
import java.io.PrintStream
import java.util.*
import java.util.prefs.Preferences
import javax.swing.SwingUtilities
import javax.swing.UIManager
import kotlin.system.exitProcess

const val LAF_DEFAULT_SYSTEM_PROPERTY = "jclasslib.laf.default"
const val WORKSPACE_FILE_SUFFIX = "jcw"
const val WEBSITE_URL = "https://www.ej-technologies.com/products/jclasslib/overview.html"
const val SETTINGS_DARK_MODE_OPTION = "darkModeOption"
const val SETTINGS_LOCALE = "locale"

var darkModeOption = DarkModeOption.AUTO

fun main(args: Array<String>) {

    getPreferencesNode().get(SETTINGS_LOCALE, "").takeIf { it.isNotBlank() }?.let { localeCode ->
        val localeParts = localeCode.split("_")
        Locale.setDefault(
            when (localeParts.size) {
                1 -> Locale(localeCode)
                2 -> Locale(localeParts[0], localeParts[1])
                3 -> Locale(localeParts[0], localeParts[1], localeParts[2])
                else -> throw IllegalStateException("$localeCode has more than 3 components")
            }
        )
    }

    if (!java.lang.Boolean.getBoolean(LAF_DEFAULT_SYSTEM_PROPERTY)) {
        darkModeOption = DarkModeOption.valueOf(getPreferencesNode().get(SETTINGS_DARK_MODE_OPTION, DarkModeOption.AUTO.name))
        darkMode = darkModeOption.isDarkMode()
        if (Util.isMacOS()) {
            MacosUiHelper.addThemeChangedListener { syncDarkMode() }
        } else if (Util.isAtLeastWindows10()) {
            // Only for JetBrains JRE, since 11.0.8+10-b1098.1
            //https://youtrack.jetbrains.com/issue/JBR-2667
            Toolkit.getDefaultToolkit().addPropertyChangeListener("win.lightTheme.on") { syncDarkMode() }
        }
        updateFlatLaf()
    }

    if (isLoadedFromJar()) {
        val stdErrFile = File(System.getProperty("java.io.tmpdir"), "jclasslib_error.log")
        System.setErr(PrintStream(BufferedOutputStream(LazyFileOutputStream(stdErrFile.path)), true))
    }

    registerStartupListener()
    if (Util.isMacOS()) {
        MacEventHandler.init()
    }

    EventQueue.invokeLater {
        BrowserFrame().apply {
            isVisible = true
            if (args.isNotEmpty()) {
                openExternalFile(args[0])
            }
        }
    }
}

fun updateFlatLaf() {
    val defaultOptionPaneIcons = listOf("error", "information", "question", "warning")
            .map { "OptionPane.${it}Icon" }
            .associateWith { UIManager.getIcon(it) }
    if (darkMode) {
        FlatDarkLaf.install()
    } else {
        FlatLightLaf.install()
    }
    if (Util.isMacOS()) {
        for ((key, icon) in defaultOptionPaneIcons) {
            UIManager.put(key, icon)
        }
    }
}

fun darkModeChanged() {
    darkMode = darkModeOption.isDarkMode()
    if (Util.isMacOS()) {
        MacosUiHelper.setDarkMode(darkMode)
    }
    getPreferencesNode().put(SETTINGS_DARK_MODE_OPTION, darkModeOption.name)
    updateFlatLaf()
    for (window in Window.getWindows()) {
        SwingUtilities.updateComponentTreeUI(window)

        // With the Jetbrains JRE, the window border is not repainted completely and
        // resizing forces a repaint
        window.setSize(window.width + 1, window.height + 1)
        EventQueue.invokeLater {
            window.setSize(window.width - 1, window.height - 1)
        }
    }
}

fun getPreferencesNode(): Preferences = Preferences.userNodeForPackage(BrowserFrame::class.java)

fun getBrowserFrames(): List<BrowserFrame> = Frame.getFrames()
        .filterIsInstance<BrowserFrame>()
        .filter { it.isVisible }

fun getNextBrowserFrame(browserFrame: BrowserFrame) : BrowserFrame = getBrowserFrames().run {
    get((indexOf(browserFrame) + 1) % size)
}

fun getPreviousBrowserFrame(browserFrame: BrowserFrame) : BrowserFrame = getBrowserFrames().run {
    get((indexOf(browserFrame) - 1 + size) % size)
}

fun getActiveBrowserFrame(): BrowserFrame? = getBrowserFrames().firstOrNull { it.isActive }

fun exit() {
    exitProcess(0)
}

private fun syncDarkMode() {
    EventQueue.invokeLater {
        darkModeChanged()
    }
}

private fun registerStartupListener() {
    StartupNotification.registerStartupListener { argLine ->
        splitupCommandLine(argLine).let { startupArgs ->
            if (startupArgs.isNotEmpty()) {
                val frames = getBrowserFrames()
                frames.elementAtOrElse(0) { BrowserFrame().apply { isVisible = true } }.apply {
                    openExternalFile(startupArgs[0])
                }
            }
        }
    }
}

private fun isLoadedFromJar(): Boolean =
        BrowserFrame::class.java.let { it.getResource(it.simpleName + ".class")?.toString()?.startsWith("jar:") == true }

private fun splitupCommandLine(command: String): List<String> {
    val cmdList = ArrayList<String>()
    val tokenizer = StringTokenizer(command, " \"", true)
    var insideQuotes = false
    val argument = StringBuilder()
    while (tokenizer.hasMoreTokens()) {
        val token = tokenizer.nextToken()
        if (token == "\"") {
            if (insideQuotes && argument.isNotEmpty()) {
                cmdList.add(argument.toString())
                argument.setLength(0)
            }
            insideQuotes = !insideQuotes
        } else if (" ".contains(token)) {
            if (insideQuotes) {
                argument.append(" ")
            } else if (argument.isNotEmpty()) {
                cmdList.add(argument.toString())
                argument.setLength(0)
            }
        } else {
            argument.append(token)
        }
    }
    if (argument.isNotEmpty()) {
        cmdList.add(argument.toString())
    }
    return cmdList
}

