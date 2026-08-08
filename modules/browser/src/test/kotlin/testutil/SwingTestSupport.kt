/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.testutil

import org.assertj.swing.annotation.GUITest
import org.assertj.swing.core.GenericTypeMatcher
import org.assertj.swing.core.Robot
import org.assertj.swing.edt.GuiActionRunner
import org.assertj.swing.finder.JOptionPaneFinder
import org.assertj.swing.fixture.JOptionPaneFixture
import org.assertj.swing.fixture.JPopupMenuFixture
import org.assertj.swing.junit.jupiter.testcase.AssertJSwingJupiterTestCase
import org.gjt.jclasslib.browser.BrowserComponent
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.browser.config.BrowserPath
import org.gjt.jclasslib.io.ClassFileReader
import org.gjt.jclasslib.io.getJrtInputStream
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.util.AlertFacade
import org.gjt.jclasslib.util.AlertType
import org.gjt.jclasslib.util.OptionAlertResult
import org.gjt.jclasslib.util.alertFacade
import java.awt.Component
import java.awt.Container
import java.awt.event.ActionEvent
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.swing.AbstractAction
import javax.swing.Action
import javax.swing.JMenuItem
import javax.swing.JPopupMenu
import javax.swing.SwingUtilities
import javax.swing.tree.TreePath

class RecordedMessage(val mainMessage: String, val contentMessage: String?, val alertType: AlertType)

class RecordedOptionDialog(val mainMessage: String, val options: List<String>, val alertType: AlertType)

class FakeAlertFacade : AlertFacade {
    val messages = mutableListOf<RecordedMessage>()
    val optionDialogs = mutableListOf<RecordedOptionDialog>()
    var nextOptionDialogResult = OptionAlertResult(0, false)

    override fun showOptionDialog(parent: Component?, mainMessage: String, contentMessage: String?, options: Array<String>, alertType: AlertType, suppressionShown: Boolean): OptionAlertResult {
        optionDialogs.add(RecordedOptionDialog(mainMessage, options.toList(), alertType))
        return nextOptionDialogResult
    }

    override fun showMessage(parent: Component?, mainMessage: String, contentMessage: String?, alertType: AlertType, suppressionShown: Boolean): Boolean {
        messages.add(RecordedMessage(mainMessage, contentMessage, alertType))
        return false
    }
}

fun <T> withFakeAlertFacade(facade: FakeAlertFacade = FakeAlertFacade(), block: (FakeAlertFacade) -> T): T {
    val previous = alertFacade
    alertFacade = facade
    try {
        return block(facade)
    } finally {
        alertFacade = previous
    }
}

fun readJdkClass(className: String = "java.lang.Object"): ClassFile =
    ClassFileReader.readFromInputStream(
        getJrtInputStream("java.base/${className.replace('.', '/')}.class", File(System.getProperty("java.home")))
    )

class TestBrowserServices(override val classFile: ClassFile = readJdkClass()) : BrowserServices {
    var modifiedCount = 0
        private set

    override val browserComponent: BrowserComponent by lazy { BrowserComponent(this) }
    override val backwardAction: Action = object : AbstractAction() {
        override fun actionPerformed(e: ActionEvent) = Unit
    }
    override val forwardAction: Action = object : AbstractAction() {
        override fun actionPerformed(e: ActionEvent) = Unit
    }

    override fun activate() = Unit

    override fun modified() {
        modifiedCount++
    }

    override fun openClassFile(className: String, browserPath: BrowserPath?) = Unit
    override fun canOpenClassFiles() = true
    override fun canSaveClassFiles() = true
    override fun showURL(urlSpec: String) = Unit
}

class TestDetailPane(services: BrowserServices) : DetailPane<Any>(Any::class.java, services) {
    override fun show(treePath: TreePath) = Unit
    override fun setupComponent() = Unit
}

fun <T> onEdt(action: () -> T): T = GuiActionRunner.execute(Callable { action() })!!

fun JOptionPaneFixture.replaceText(text: String) {
    textBox().deleteText()
    textBox().enterText(text)
}

fun JOptionPaneFixture.confirm() {
    okButton().click()
}

fun JOptionPaneFixture.cancel() {
    cancelButton().click()
}

fun findActivePopupMenuFixture(robot: Robot): JPopupMenuFixture =
    JPopupMenuFixture(robot, robot.finder().findByType(JPopupMenu::class.java, true))

fun JPopupMenuFixture.clickMenuItemWithText(text: String) {
    menuItem(object : GenericTypeMatcher<JMenuItem>(JMenuItem::class.java) {
        override fun isMatching(item: JMenuItem) = item.text == text
    }).click()
}

fun Component.descendants(): Sequence<Component> =
    (this as? Container)?.components.orEmpty().asSequence().flatMap { component ->
        sequenceOf(component) + component.descendants()
    }

inline fun <reified T : Component> Robot.findAllByType(root: Container): Collection<T> =
    finder().findAll(root, org.assertj.swing.core.TypeMatcher(T::class.java)).filterIsInstance<T>()

@GUITest
abstract class SwingRobotTest : AssertJSwingJupiterTestCase() {

    override fun onSetUp() = Unit

    fun <T> driveInputDialog(input: String?, action: () -> T): T = driveInputDialog({ dialog ->
        if (input != null) {
            dialog.replaceText(input)
            dialog.confirm()
        } else {
            dialog.cancel()
        }
    }, action)

    fun <T> driveInputDialog(dialogHandler: (JOptionPaneFixture) -> Unit, action: () -> T): T {
        val result = AtomicReference<T?>()
        val failure = AtomicReference<Throwable?>()
        val latch = CountDownLatch(1)
        SwingUtilities.invokeLater {
            try {
                result.set(action())
            } catch (t: Throwable) {
                failure.set(t)
            } finally {
                latch.countDown()
            }
        }
        val optionPane = try {
            JOptionPaneFinder.findOptionPane().withTimeout(DIALOG_TIMEOUT_MS).using(robot())
        } catch (t: Throwable) {
            failure.get()?.let { throw it }
            throw t
        }
        dialogHandler(optionPane)
        if (!latch.await(DIALOG_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
            error("Action did not complete after the dialog was closed")
        }
        failure.get()?.let { throw it }
        @Suppress("UNCHECKED_CAST")
        return result.get() as T
    }

    fun expectOptionPane(input: String?) = expectOptionPane { dialog ->
        if (input != null) {
            dialog.replaceText(input)
            dialog.confirm()
        } else {
            dialog.cancel()
        }
    }

    fun expectOptionPane(dialogHandler: (JOptionPaneFixture) -> Unit) {
        val optionPane = JOptionPaneFinder.findOptionPane().withTimeout(DIALOG_TIMEOUT_MS).using(robot())
        dialogHandler(optionPane)
    }

    companion object {
        private const val DIALOG_TIMEOUT_MS = 10_000L
    }
}
