/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.config.CategoryHolder
import org.gjt.jclasslib.browser.config.ReferenceHolder
import org.gjt.jclasslib.browser.detail.ClassElementOpener
import org.gjt.jclasslib.structures.ConstantPoolUtil
import org.gjt.jclasslib.structures.attributes.BootstrapMethodsAttribute
import org.gjt.jclasslib.structures.attributes.LineNumberTableAttribute
import org.gjt.jclasslib.structures.constants.ConstantClassInfo
import org.gjt.jclasslib.structures.constants.ConstantMethodrefInfo
import org.gjt.jclasslib.testutil.*
import org.gjt.jclasslib.util.AlertType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class NavigationLinksTest {

    @Test
    fun testConstantPoolLinkSelectsEntryAndReplacesHistory() {
        val services = TestBrowserServices()
        val component = testBrowserComponent(services)
        onEdt { component.selectNode { it.type == NodeType.GENERAL } }

        onEdt { constantPoolLink(services, 2) }

        val selected = onEdt { component.selectedNode() }
        assertEquals(NodeType.CONSTANT_POOL_ENTRY, selected.type)
        assertEquals(1, selected.index)

        // the detour through the constant pool category is not in the history
        onEdt { component.history.historyBackward() }
        assertEquals(NodeType.GENERAL, onEdt { component.selectedNode() }.type)
    }

    @Test
    fun testConstantPoolLinkWithZeroIndexIsIgnored() {
        val services = TestBrowserServices()
        val component = testBrowserComponent(services)
        val selected = onEdt { component.selectNode { it.type == NodeType.GENERAL } }

        onEdt { constantPoolLink(services, 0) }

        assertEquals(selected, onEdt { component.selectedNode() })
    }

    @Test
    fun testConstantPoolLinkBeyondPoolSizeIsIgnored() {
        val services = TestBrowserServices()
        val component = testBrowserComponent(services)
        val selected = onEdt { component.selectNode { it.type == NodeType.GENERAL } }

        onEdt { constantPoolLink(services, 100000) }

        assertEquals(selected, onEdt { component.selectedNode() })
    }

    @Test
    fun testAttributeLinkSelectsAttributeNode() {
        // java.lang.String has a class-level BootstrapMethods attribute
        val services = TestBrowserServices(readJdkClass("java.lang.String"))
        val component = testBrowserComponent(services)

        onEdt { classAttributeLink(services, 0, BootstrapMethodsAttribute::class.java) }

        assertEquals(NodeType.ATTRIBUTE, onEdt { component.selectedNode() }.type)
    }

    @Test
    fun testMissingAttributeLinkShowsError() = withFakeAlertFacade { alerts ->
        val services = TestBrowserServices()
        val component = testBrowserComponent(services)
        val selected = onEdt { component.selectNode { it.type == NodeType.GENERAL } }

        onEdt { classAttributeLink(services, 0, LineNumberTableAttribute::class.java) }

        assertEquals(1, alerts.messages.size)
        assertEquals(AlertType.ERROR, alerts.messages[0].alertType)
        assertEquals(selected, onEdt { component.selectedNode() })
    }

    @Test
    fun testMemberReferenceOpensDefiningClass() {
        val services = TestBrowserServices()
        val detailPane = onEdt { TestDetailPane(services) }
        val classFile = services.classFile
        // toString() is declared in AbstractCollection, not in ArrayList or AbstractList
        val methodrefIndex = ConstantPoolUtil.addConstantMethodrefInfo(
                classFile, "java/util/ArrayList", "toString", "()Ljava/lang/String;")
        val methodref = classFile.getConstantPoolEntry(methodrefIndex, ConstantMethodrefInfo::class)
        val opener = onEdt {
            ClassElementOpener(detailPane).apply {
                setConstant(methodref)
            }
        }

        onEdt { opener.doClick() }

        assertEquals(1, services.openedClassFiles.size)
        val (className, path) = services.openedClassFiles[0]
        assertEquals("java.util.AbstractCollection", className)
        val components = path!!.pathComponents
        assertEquals(CategoryHolder(NodeType.METHODS), components[0])
        assertEquals(ReferenceHolder("toString", "()Ljava/lang/String;"), components[1])
    }

    @Test
    fun testSelfReferenceIsNotOffered() {
        val services = TestBrowserServices()
        val detailPane = onEdt { TestDetailPane(services) }
        val classInfo = ConstantClassInfo(services.classFile).apply {
            nameIndex = ConstantPoolUtil.addConstantUTF8Info(services.classFile, "java/lang/Object")
        }
        val opener = onEdt {
            ClassElementOpener(detailPane).apply {
                setConstant(classInfo)
            }
        }

        assertFalse(opener.isVisible)
    }
}
