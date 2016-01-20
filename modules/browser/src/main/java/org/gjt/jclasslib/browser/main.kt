/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

@file:JvmName("BrowserApplication")
package org.gjt.jclasslib.browser

import com.exe4j.runtime.util.LazyFileOutputStream
import com.install4j.api.launcher.StartupNotification
import java.awt.EventQueue
import java.io.BufferedOutputStream
import java.io.File
import java.io.PrintStream
import java.util.*
import javax.swing.UIManager

val APPLICATION_TITLE = "Bytecode viewer"
val SYSTEM_PROPERTY_LAF_DEFAULT = "jclasslib.laf.default"
val WORKSPACE_FILE_SUFFIX = "jcw"

fun main(args: Array<String>) {

    if (!java.lang.Boolean.getBoolean(SYSTEM_PROPERTY_LAF_DEFAULT)) {
        val lookAndFeelClass = UIManager.getSystemLookAndFeelClassName()
        try {
            UIManager.setLookAndFeel(lookAndFeelClass)
        } catch (ex: Exception) {
        }
    }

    if (isLoadedFromJar()) {
        val stdErrFile = File(System.getProperty("java.io.tmpdir"), "jclasslib_error.log")
        System.setErr(PrintStream(BufferedOutputStream(LazyFileOutputStream(stdErrFile.path)), true))
    }

    EventQueue.invokeLater {
        BrowserMDIFrame().apply {
            Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
                throwable.printStackTrace(System.out)
            }
            registerStartupListener(this)
            isVisible = true
            if (args.size > 0) {
                openExternalFile(args[0])
            }
        }
    }
}

private fun registerStartupListener(frame: BrowserMDIFrame) {
    StartupNotification.registerStartupListener { argLine ->
        splitupCommandLine(argLine).let { startupArgs ->
            if (startupArgs.size > 0) {
                frame.openExternalFile(startupArgs[0])
            }
        }
    }
}

private fun isLoadedFromJar(): Boolean =
        BrowserMDIFrame::class.java.let { it.getResource(it.simpleName + ".class").toString().startsWith("jar:")}

private fun splitupCommandLine(command: String): List<String> {
    val cmdList = ArrayList<String>()
    val tokenizer = StringTokenizer(command, " \"", true)
    var insideQuotes = false
    val argument = StringBuilder()
    while (tokenizer.hasMoreTokens()) {
        val token = tokenizer.nextToken()
        if (token == "\"") {
            if (insideQuotes && argument.length > 0) {
                cmdList.add(argument.toString())
                argument.setLength(0)
            }
            insideQuotes = !insideQuotes
        } else if (" ".contains(token)) {
            if (insideQuotes) {
                argument.append(" ")
            } else if (argument.length > 0) {
                cmdList.add(argument.toString())
                argument.setLength(0)
            }
        } else {
            argument.append(token)
        }
    }
    if (argument.length > 0) {
        cmdList.add(argument.toString())
    }
    return cmdList
}

