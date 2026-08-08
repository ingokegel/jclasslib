/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.usages

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.config.BrowserPath
import org.gjt.jclasslib.browser.config.IndexHolder
import org.gjt.jclasslib.io.ClassFileReadMode
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.ConstantPoolUtil
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info
import org.gjt.jclasslib.testutil.SwingRobotTest
import org.gjt.jclasslib.testutil.TestBrowserServices
import org.gjt.jclasslib.testutil.clickButtonWithText
import org.gjt.jclasslib.testutil.disposeWindow
import org.gjt.jclasslib.testutil.onEdt
import org.gjt.jclasslib.testutil.readJdkClass
import org.gjt.jclasslib.testutil.showInWindow
import org.gjt.jclasslib.testutil.withFakeAlertFacade
import org.gjt.jclasslib.util.AlertType
import javax.swing.JFrame
import javax.swing.SwingUtilities
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class UsagesScanTest : SwingRobotTest() {

    private fun <T> withComponentWindow(services: TestBrowserServices, block: (JFrame) -> T): T {
        val window = showInWindow(onEdt { services.browserComponent })
        try {
            return block(window)
        } finally {
            disposeWindow(window)
        }
    }

    private fun runSearchAndExpectDialog(services: TestBrowserServices, titleKey: String,
                                         search: () -> Unit, dialogHandler: (org.assertj.swing.fixture.DialogFixture) -> Unit) {
        withComponentWindow(services) {
            SwingUtilities.invokeLater { search() }
            expectDialog(getString(titleKey), dialogHandler)
        }
    }

    private fun runSearchAndCancelDialog(services: TestBrowserServices, titleKey: String, search: () -> Unit) {
        runSearchAndExpectDialog(services, titleKey, search) { dialog ->
            dialog.clickButtonWithText(getString("action.cancel"))
        }
    }

    private fun classWithMarker(marker: String): ClassFile =
        readJdkClass("java.lang.String").also { classFile ->
            ConstantPoolUtil.addConstantUTF8Info(classFile, marker)
        }

    @Test
    fun testJdkClassNamePrefixes() {
        assertTrue(isJdkClassName("java/lang/Object"))
        assertTrue(isJdkClassName("javax/swing/JFrame"))
        assertTrue(isJdkClassName("jdk/internal/Whatever"))
        assertTrue(isJdkClassName("sun/misc/Unsafe"))
        assertTrue(isJdkClassName("com/sun/something/Internal"))
        assertFalse(isJdkClassName("com/example/MyClass"))
        assertFalse(isJdkClassName("kotlin/Unit"))
    }

    @Test
    fun testFindClassUsagesCollectsMatchingConstants() = withFakeAlertFacade {
        val services = TestBrowserServices()
        val marker = "needle_marker"
        services.scannedClassFiles = listOf(classWithMarker(marker), readJdkClass("java.lang.Integer"))

        val usages = withComponentWindow(services) { window ->
            onEdt {
                findClassUsages(services, false, window) { constant ->
                    constant is ConstantUtf8Info && constant.string == marker
                }
            }
        }

        assertEquals(1, usages.size)
        assertEquals("java/lang/String", usages[0].className)
        val classFile = services.scannedClassFiles[0]
        assertEquals(classFile.getConstantPoolIndex(classFile.constantPool.filterIsInstance<ConstantUtf8Info>().last()),
                usages[0].referenceIndex)
        assertEquals(false, services.lastScanIncludeJdk)
        assertEquals(ClassFileReadMode.SKIP_ATTRIBUTES, services.lastScanReadMode)
    }

    @Test
    fun testFindImplementingClassesMatchesSubclass() = withFakeAlertFacade {
        val services = TestBrowserServices()
        services.scannedClassFiles = listOf(
                readJdkClass("java.util.ArrayList"),
                readJdkClass("java.lang.String")
        )

        runSearchAndExpectDialog(services, "found.implementing.classes.title",
                { findImplementingClasses("java/util/AbstractList", services) }) { dialog ->
            dialog.list().requireItemCount(1)
            dialog.list().selectItem(0)
            dialog.clickButtonWithText(getString("action.ok"))
        }

        assertEquals(listOf<Pair<String, BrowserPath?>>("java/util/ArrayList" to null), services.openedClassFiles)
    }

    @Test
    fun testFindImplementingClassesMatchesInterface() = withFakeAlertFacade {
        val services = TestBrowserServices()
        services.scannedClassFiles = listOf(
                readJdkClass("java.util.ArrayList"),
                readJdkClass("java.lang.Integer")
        )

        runSearchAndExpectDialog(services, "found.implementing.classes.title",
                { findImplementingClasses("java/util/List", services) }) { dialog ->
            dialog.list().requireItemCount(1)
            dialog.clickButtonWithText(getString("action.cancel"))
        }

        assertTrue(services.openedClassFiles.isEmpty())
    }

    @Test
    fun testFindImplementingClassesWithoutMatchShowsInfo() = withFakeAlertFacade { alerts ->
        val services = TestBrowserServices()
        services.scannedClassFiles = listOf(readJdkClass("java.lang.Object"))

        withComponentWindow(services) {
            onEdt { findImplementingClasses("java/util/List", services) }
        }

        assertEquals(1, alerts.messages.size)
        assertEquals(AlertType.INFORMATION, alerts.messages[0].alertType)
    }

    @Test
    fun testFindAnnotatedElements() = withFakeAlertFacade {
        val services = TestBrowserServices()
        services.scannedClassFiles = listOf(
                readJdkClass("java.lang.SecurityManager"),
                readJdkClass("java.lang.Integer")
        )

        // java.lang.SecurityManager is annotated with @Deprecated at class level
        runSearchAndCancelDialog(services, "found.annotated.elements.title") {
            findAnnotatedElements("java/lang/Deprecated", services)
        }

        assertEquals(ClassFileReadMode.FULL, services.lastScanReadMode)
    }

    @Test
    fun testFindAnnotatedElementsWithoutMatchShowsInfo() = withFakeAlertFacade { alerts ->
        val services = TestBrowserServices()
        services.scannedClassFiles = listOf(readJdkClass("java.lang.Object"))

        withComponentWindow(services) {
            onEdt { findAnnotatedElements("javax/annotation/Generated", services) }
        }

        assertEquals(1, alerts.messages.size)
        assertEquals(AlertType.INFORMATION, alerts.messages[0].alertType)
    }

    @Test
    fun testMemberUsagesWithMultipleReferencingClassesShowClassList() = withFakeAlertFacade {
        val services = TestBrowserServices()
        val equalsMethod = services.classFile.methods.first { it.name == "equals" }
        val referenceIndex = ConstantPoolUtil.addConstantMethodrefInfo(
                services.classFile, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z")
        services.scannedClassFiles = listOf(
                services.classFile,
                readJdkClass("java.lang.String").also { stringClass ->
                    ConstantPoolUtil.addConstantMethodrefInfo(
                            stringClass, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z")
                }
        )
        val component = onEdt { services.browserComponent }

        runSearchAndExpectDialog(services, "found.classes.with.usages.title",
                { findClassMemberUsages(component, equalsMethod) }) { dialog ->
            dialog.list().selectItem(0)
            dialog.clickButtonWithText(getString("action.ok"))
        }

        assertEquals(1, services.openedClassFiles.size)
        val (className, path) = services.openedClassFiles[0]
        assertEquals("java/lang/Object", className)
        assertEquals(referenceIndex - 1, (path!!.pathComponents[1] as IndexHolder).index)
    }

    @Test
    fun testSingleMemberUsageInSameClassBypassesClassList() = withFakeAlertFacade { alerts ->
        val services = TestBrowserServices()
        val equalsMethod = services.classFile.methods.first { it.name == "equals" }
        // only the open class itself references the member
        ConstantPoolUtil.addConstantMethodrefInfo(services.classFile, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z")
        services.scannedClassFiles = listOf(services.classFile)
        val component = onEdt { services.browserComponent }

        withComponentWindow(services) {
            // no class list dialog appears, the flow goes directly to the in-class usage view,
            // which reports no usages because no structure in the open class references the entry
            onEdt { findClassMemberUsages(component, equalsMethod) }
        }

        assertTrue(services.openedClassFiles.isEmpty())
        assertEquals(1, alerts.messages.size)
        assertEquals(AlertType.INFORMATION, alerts.messages[0].alertType)
    }

    @Test
    fun testConstantUsagesFindsReferencingStructures() = withFakeAlertFacade {
        val services = TestBrowserServices()
        // creates a name-and-type entry referencing the "equals" UTF8 entry
        ConstantPoolUtil.addConstantMethodrefInfo(services.classFile, "java/lang/Object", "equals", "(Ljava/lang/Object;)Z")
        val utf8 = services.classFile.constantPool.filterIsInstance<ConstantUtf8Info>().first { it.string == "equals" }
        val component = onEdt { services.browserComponent }

        runSearchAndCancelDialog(services, "found.usages.title") {
            findConstantUsages(component, utf8)
        }
    }
}
