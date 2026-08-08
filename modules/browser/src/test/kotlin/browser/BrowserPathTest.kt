/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.config.*
import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.MethodInfo
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info
import org.gjt.jclasslib.testutil.onEdt
import org.gjt.jclasslib.testutil.selectNode
import org.gjt.jclasslib.testutil.selectedNode
import org.gjt.jclasslib.testutil.testBrowserComponent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class BrowserPathTest {

    @Test
    fun testMethodSelectionIsRecordedByNameAndDescriptor() {
        val component = testBrowserComponent()
        val methodNode = onEdt { component.selectNode { (it.element as? MethodInfo)?.name == "equals" } }

        val path = onEdt { component.browserPath }

        val components = path!!.pathComponents
        assertEquals(2, components.size)
        assertIs<CategoryHolder>(components[0])
        val reference = components[1]
        assertIs<ReferenceHolder>(reference)
        assertEquals("equals", reference.name)
        assertEquals("(Ljava/lang/Object;)Z", reference.type)
        assertTrue(reference.matches(methodNode))
    }

    @Test
    fun testAttributeSelectionIsRecordedByName() {
        val component = testBrowserComponent()
        onEdt { component.selectNode { (it.element as? AttributeInfo)?.name == "SourceFile" } }

        val path = onEdt { component.browserPath }

        val components = path!!.pathComponents
        assertEquals(2, components.size)
        assertIs<CategoryHolder>(components[0])
        val attribute = components[1]
        assertIs<AttributeHolder>(attribute)
        assertEquals("SourceFile", attribute.name)
    }

    @Test
    fun testConstantPoolSelectionIsRecordedByIndexWhenFilterShowsAll() {
        val component = testBrowserComponent()
        val node = onEdt { component.selectNode { it.element is ConstantUtf8Info } }

        val path = onEdt { component.browserPath }

        val components = path!!.pathComponents
        assertEquals(2, components.size)
        assertIs<CategoryHolder>(components[0])
        val index = components[1]
        assertIs<IndexHolder>(index)
        assertEquals(node.index, index.index)
    }

    @Test
    fun testPathResolutionRestoresSelection() {
        val component = testBrowserComponent()
        val methodNode = onEdt { component.selectNode { (it.element as? MethodInfo)?.name == "equals" } }
        val path = onEdt { component.browserPath }

        onEdt { component.selectNode { it.type == NodeType.GENERAL } }
        onEdt { component.browserPath = path }

        assertEquals(methodNode, onEdt { component.selectedNode() })
    }

    @Test
    fun testPathResolutionStopsAtFirstUnmatchedComponent() {
        val component = testBrowserComponent()
        val path = BrowserPath().apply {
            addPathComponent(CategoryHolder(NodeType.METHODS))
            addPathComponent(ReferenceHolder("noSuchMethod", "()V"))
        }

        onEdt { component.browserPath = path }

        assertEquals(NodeType.METHODS, onEdt { component.selectedNode() }.type)
    }
}
