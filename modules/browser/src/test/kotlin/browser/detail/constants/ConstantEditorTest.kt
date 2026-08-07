/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.constants

import org.gjt.jclasslib.browser.detail.EditResult
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.constants.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class ConstantEditorTest {

    private val classFile = ClassFile()

    @Test
    fun testValidIntegerEditIsApplied() {
        val constant = ConstantIntegerInfo(classFile).apply { int = 5 }
        assertEquals(EditResult.APPLIED, ConstantIntegerEditor().applyValue(constant, "42"))
        assertEquals(42, constant.int)
    }

    @Test
    fun testUnchangedEditIsNotApplied() {
        val constant = ConstantIntegerInfo(classFile).apply { int = 5 }
        val editor = ConstantIntegerEditor()
        assertEquals(EditResult.APPLIED, editor.applyValue(constant, "42"))
        assertEquals(EditResult.UNCHANGED, editor.applyValue(constant, "42"))
        assertEquals(42, constant.int)
    }

    @Test
    fun testNonNumericIntegerEditIsRejected() {
        val constant = ConstantIntegerInfo(classFile).apply { int = 5 }
        assertEquals(EditResult.INVALID, ConstantIntegerEditor().applyValue(constant, "abc"))
        assertEquals(5, constant.int)
    }

    @Test
    fun testOutOfRangeIntegerEditIsRejected() {
        val constant = ConstantIntegerInfo(classFile).apply { int = 5 }
        assertEquals(EditResult.INVALID, ConstantIntegerEditor().applyValue(constant, "9999999999"))
        assertEquals(5, constant.int)
    }

    @Test
    fun testValidLongEditIsApplied() {
        val constant = ConstantLongInfo(classFile).apply { long = 5L }
        assertEquals(EditResult.APPLIED, ConstantLongEditor().applyValue(constant, "123456789123456789"))
        assertEquals(123456789123456789, constant.long)
    }

    @Test
    fun testOutOfRangeLongEditIsRejected() {
        val constant = ConstantLongInfo(classFile).apply { long = 5L }
        assertEquals(EditResult.INVALID, ConstantLongEditor().applyValue(constant, "99999999999999999999"))
        assertEquals(5L, constant.long)
    }

    @Test
    fun testValidDoubleEditIsApplied() {
        val constant = ConstantDoubleInfo(classFile).apply { double = 1.0 }
        assertEquals(EditResult.APPLIED, ConstantDoubleEditor().applyValue(constant, "2.5"))
        assertEquals(2.5, constant.double)
    }

    @Test
    fun testNonNumericDoubleEditIsRejected() {
        val constant = ConstantDoubleInfo(classFile).apply { double = 1.0 }
        assertEquals(EditResult.INVALID, ConstantDoubleEditor().applyValue(constant, "abc"))
        assertEquals(1.0, constant.double)
    }

    @Test
    fun testUtf8EditAcceptsAnyString() {
        val constant = ConstantUtf8Info(classFile).apply { string = "old" }
        assertEquals(EditResult.APPLIED, ConstantUtf8Editor().applyValue(constant, "new value"))
        assertEquals("new value", constant.string)
    }

    @Test
    fun testSupportedConstantTypesHaveEditors() {
        assertNotNull(getConstantEdit(ConstantUtf8Info(classFile)))
        assertNotNull(getConstantEdit(ConstantIntegerInfo(classFile)))
        assertNotNull(getConstantEdit(ConstantLongInfo(classFile)))
        assertNotNull(getConstantEdit(ConstantFloatInfo(classFile)))
        assertNotNull(getConstantEdit(ConstantDoubleInfo(classFile)))
        assertNotNull(getConstantEdit(ConstantClassInfo(classFile)))
        assertNotNull(getConstantEdit(ConstantStringInfo(classFile)))
    }

    @Test
    fun testUnsupportedConstantTypesHaveNoEditor() {
        assertNull(getConstantEdit(ConstantMethodrefInfo(classFile)))
        assertNull(getConstantEdit(ConstantNameAndTypeInfo(classFile)))
    }
}
