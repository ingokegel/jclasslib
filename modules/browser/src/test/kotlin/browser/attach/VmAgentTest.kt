/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.attach

import org.gjt.jclasslib.browser.AttachableVm
import org.gjt.jclasslib.browser.VmConnection
import org.gjt.jclasslib.browser.config.classpath.ClassTreeNode
import org.gjt.jclasslib.browser.config.classpath.ClasspathEntry
import org.gjt.jclasslib.browser.config.classpath.ClasspathVmEntry
import org.gjt.jclasslib.browser.connectToVm
import org.gjt.jclasslib.io.ClassFileReader
import org.gjt.jclasslib.testutil.SleepTarget
import org.gjt.jclasslib.testutil.VmTargetFixture
import org.gjt.jclasslib.testutil.withFakeAlertFacade
import org.gjt.jclasslib.util.AlertType
import java.io.ByteArrayInputStream
import javax.swing.tree.DefaultTreeModel
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class VmAgentTest {

    private val fixture = VmTargetFixture()
    private lateinit var attachableVm: AttachableVm

    @BeforeTest
    fun startVm() {
        attachableVm = fixture.start()
    }

    @AfterTest
    fun stopVm() {
        fixture.stop()
    }

    @Test
    fun testListLoadedClasses() = withFakeAlertFacade {
        val connection = fixture.connect(attachableVm)

        val classes = connection.communicator.classes
        assertTrue(classes.any { it.className == SleepTarget.CLASS_NAME })
        assertTrue(classes.none { it.className.startsWith("[") }, "array classes must be excluded")
        // the lambda in SleepTarget.main creates a hidden class
        assertTrue(classes.any { it.className.contains('/') }, "hidden classes are reported by the agent")
        // the test class is loaded by the application class loader and has no module
        assertNull(classes.first { it.className == SleepTarget.CLASS_NAME }.moduleName)
    }

    @Test
    fun testReadClassFromVm() = withFakeAlertFacade {
        val connection = fixture.connect(attachableVm)

        val bytes = connection.communicator.getClassFile(SleepTarget.CLASS_FILE_NAME)

        assertNotNull(bytes)
        val classFile = ClassFileReader.readFromInputStream(ByteArrayInputStream(bytes))
        assertEquals(SleepTarget.CLASS_FILE_NAME, classFile.thisClassName)
    }

    @Test
    fun testReadUnknownClassFromVm() = withFakeAlertFacade {
        val connection = fixture.connect(attachableVm)

        assertNull(connection.communicator.getClassFile("org/example/NoSuchClass"))
    }

    @Test
    fun testRedefineWithSameBytesSucceeds() = withFakeAlertFacade {
        val connection = fixture.connect(attachableVm)
        val fileName = SleepTarget.CLASS_FILE_NAME
        val bytes = connection.communicator.getClassFile(fileName)!!

        val result = connection.communicator.replaceClassFile(fileName, bytes)

        assertTrue(result.isSuccess)
    }

    @Test
    fun testRedefineUnknownClassFails() = withFakeAlertFacade {
        val connection = fixture.connect(attachableVm)

        val result = connection.communicator.replaceClassFile("org/example/NoSuchClass", byteArrayOf())

        assertFalse(result.isSuccess)
        assertNotNull(result.errorMessage)
        Unit
    }

    @Test
    fun testRedefineWithMalformedBytesFails() = withFakeAlertFacade {
        val connection = fixture.connect(attachableVm)

        val result = connection.communicator.replaceClassFile(
                SleepTarget.CLASS_FILE_NAME, byteArrayOf(1, 2, 3))

        assertFalse(result.isSuccess)
        assertNotNull(result.errorMessage)
        Unit
    }

    @Test
    fun testRepeatedAgentLoadDoesNotRegisterTwice() = withFakeAlertFacade {
        val first = fixture.connect(attachableVm)
        val second = fixture.connect(attachableVm)

        assertTrue(first.communicator.classes.isNotEmpty())
        assertTrue(second.communicator.classes.isNotEmpty())
    }

    @Test
    fun testHiddenClassesAreExcludedFromBrowsing() = withFakeAlertFacade {
        val connection = fixture.connect(attachableVm)
        val entry = ClasspathVmEntry(connection)
        val classPathModel = DefaultTreeModel(ClassTreeNode())
        val modulePathModel = DefaultTreeModel(ClassTreeNode())

        entry.mergeClassesIntoTree(classPathModel, modulePathModel, true)

        val root = classPathModel.root as ClassTreeNode
        val names = root.depthFirstEnumeration().asSequence()
                .map { (it as ClassTreeNode).userObject?.toString() ?: "" }.toList()
        assertTrue(names.none { it.contains('/') }, "hidden classes must be excluded from the tree")
        assertTrue(names.any { it.contains("SleepTarget") })
    }

    @Test
    fun testFindClassInVmEntry() = withFakeAlertFacade {
        val connection = fixture.connect(attachableVm)
        val entry = ClasspathVmEntry(connection)

        val result = entry.findClass(SleepTarget.CLASS_NAME, false)

        assertNotNull(result)
        assertEquals(SleepTarget.CLASS_FILE_NAME, result.fileName)
        assertEquals(ClasspathEntry.UNNAMED_MODULE, result.moduleName)
    }

    @Test
    fun testDetachClosesConnection() = withFakeAlertFacade {
        val connection = fixture.connect(attachableVm)

        connection.close()

        assertFailsWith<Exception> {
            connection.communicator.classes
        }
        Unit
    }

    @Test
    fun testAttachToDeadVmShowsError() = withFakeAlertFacade { alerts ->
        fixture.stop()

        val connection = connectToVm(attachableVm, null)

        assertNull(connection)
        assertEquals(1, alerts.messages.size)
        assertEquals(AlertType.ERROR, alerts.messages[0].alertType)
        Unit
    }
}
