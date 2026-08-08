/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.attach

import org.gjt.jclasslib.browser.AttachableVm
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.VmConnection
import org.gjt.jclasslib.browser.attachToVm
import org.gjt.jclasslib.testutil.BrowserAppFixture
import org.gjt.jclasslib.testutil.SleepTarget
import org.gjt.jclasslib.testutil.SwingRobotTest
import org.gjt.jclasslib.testutil.VmTargetFixture
import org.gjt.jclasslib.testutil.clickButtonWithText
import org.gjt.jclasslib.testutil.onEdt
import org.gjt.jclasslib.testutil.withFakeAlertFacade
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import javax.swing.SwingUtilities
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AttachFlowTest : SwingRobotTest() {

    private lateinit var fixture: BrowserAppFixture
    private val target = VmTargetFixture()

    override fun onSetUp() {
        fixture = BrowserAppFixture()
        fixture.focusFrame(robot())
    }

    override fun onTearDown() {
        fixture.dispose()
    }

    @BeforeTest
    fun startVm() {
        target.start()
    }

    @AfterTest
    fun stopVm() {
        target.stop()
    }

    private fun attachThroughDialog(selectTarget: Boolean): VmConnection? {
        val result = AtomicReference<VmConnection?>()
        val latch = CountDownLatch(1)
        SwingUtilities.invokeLater {
            result.set(attachToVm(fixture.frame))
            latch.countDown()
        }
        expectDialog(getString("window.select.running.jvm")) { dialog ->
            if (selectTarget) {
                val list = dialog.list()
                val index = (0 until list.target().model.size).first {
                    (list.target().model.getElementAt(it) as AttachableVm).descriptor.id() == target.pid.toString()
                }
                list.selectItem(index)
                dialog.clickButtonWithText(getString("action.ok"))
            } else {
                dialog.clickButtonWithText(getString("action.cancel"))
            }
        }
        if (!latch.await(30, TimeUnit.SECONDS)) {
            val windows = onEdt {
                java.awt.Window.getWindows().map {
                    "${it.javaClass.simpleName}(name=${it.name}, title=${(it as? java.awt.Dialog)?.title}, showing=${it.isShowing}, bounds=${it.bounds})"
                }
            }
            error("attach did not complete, windows: $windows")
        }
        return result.get()
    }

    @Test
    fun testAttachThroughDialog() = withFakeAlertFacade { alerts ->
        val connection = attachThroughDialog(selectTarget = true)

        assertTrue(alerts.messages.isEmpty(),
                "attach produced errors: ${alerts.messages.map { it.mainMessage }}; agent jar: ${System.getProperty("jclasslib.agent.jar")}")
        assertNotNull(connection)
        assertTrue(connection.communicator.classes.any { it.className.contains(SleepTarget.CLASS_NAME) })
        connection.close()
    }

    @Test
    fun testCancelledSelectionAttachesToNothing() {
        val connection = attachThroughDialog(selectTarget = false)

        assertNull(connection)
    }
}
