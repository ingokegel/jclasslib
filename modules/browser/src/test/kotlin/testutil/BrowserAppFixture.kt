/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.testutil

import org.assertj.swing.core.Robot
import org.gjt.jclasslib.browser.BrowserFrame
import org.gjt.jclasslib.browser.BrowserTab
import org.gjt.jclasslib.browser.BrowserTreeNode
import org.gjt.jclasslib.browser.DefaultSavingConfirmationPolicy
import org.gjt.jclasslib.browser.savingConfirmationPolicy
import org.gjt.jclasslib.io.writeToByteArray
import org.gjt.jclasslib.structures.ClassFile
import java.awt.Point
import java.io.File

class BrowserAppFixture {

    val frame: BrowserFrame = onEdt {
        BrowserFrame().apply {
            setSize(1000, 800)
            isVisible = true
        }
    }

    fun openClass(classFile: ClassFile, file: File): BrowserTab {
        file.writeBytes(classFile.writeToByteArray())
        return onEdt { frame.openClassFromFile(file) }
    }

    fun focusFrame(robot: Robot) {
        // metacity consumes the first click for focusing the window
        robot.click(frame, Point(frame.width / 2, 5))
        robot.waitForIdle()
    }

    fun selectNode(tab: BrowserTab, predicate: (BrowserTreeNode) -> Boolean): BrowserTreeNode = onEdt {
        tab.browserComponent.selectNode(predicate)
    }

    fun dispose() {
        onEdt { frame.dispose() }
    }
}

fun resetSavingConfirmationPolicy() {
    savingConfirmationPolicy = DefaultSavingConfirmationPolicy()
}

fun BrowserTab.modifyFirstUtf8(newValue: String) {
    val constant = classFile.constantPool.filterIsInstance<org.gjt.jclasslib.structures.constants.ConstantUtf8Info>().first()
    constant.string = newValue
    browserComponent.isModified = true
}
