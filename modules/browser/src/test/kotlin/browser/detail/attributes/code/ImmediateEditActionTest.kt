/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.browser.detail.EditResult
import org.gjt.jclasslib.bytecode.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ImmediateEditActionTest {

    @Test
    fun testValidByteEditIsApplied() {
        val instruction = SimpleImmediateByteInstruction(Opcode.BIPUSH, isWide = false, immediateByte = 5)
        assertEquals(EditResult.APPLIED, ImmediateByteEditAction("test").applyValue(instruction, "200"))
        assertEquals(200, instruction.immediateByte)
    }

    @Test
    fun testOutOfRangeByteEditIsRejected() {
        val instruction = SimpleImmediateByteInstruction(Opcode.BIPUSH, isWide = false, immediateByte = 5)
        assertEquals(EditResult.INVALID, ImmediateByteEditAction("test").applyValue(instruction, "256"))
        assertEquals(5, instruction.immediateByte)
    }

    @Test
    fun testNonNumericByteEditIsRejected() {
        val instruction = SimpleImmediateByteInstruction(Opcode.BIPUSH, isWide = false, immediateByte = 5)
        assertEquals(EditResult.INVALID, ImmediateByteEditAction("test").applyValue(instruction, "abc"))
        assertEquals(5, instruction.immediateByte)
    }

    @Test
    fun testCancelledEditLeavesInstructionUnchanged() {
        val instruction = SimpleImmediateByteInstruction(Opcode.BIPUSH, isWide = false, immediateByte = 5)
        assertEquals(EditResult.UNCHANGED, ImmediateByteEditAction("test").applyValue(instruction, null))
        assertEquals(5, instruction.immediateByte)
    }

    @Test
    fun testUnchangedEditIsNotApplied() {
        val instruction = SimpleImmediateByteInstruction(Opcode.BIPUSH, isWide = false, immediateByte = 5)
        assertEquals(EditResult.UNCHANGED, ImmediateByteEditAction("test").applyValue(instruction, "5"))
        assertEquals(5, instruction.immediateByte)
    }

    @Test
    fun testValidBranchOffsetEditIsApplied() {
        val instruction = BranchInstruction(Opcode.IFEQ, branchOffset = 3)
        assertEquals(EditResult.APPLIED, BranchEditAction().applyValue(instruction, "10"))
        assertEquals(10, instruction.branchOffset)
    }

    @Test
    fun testNegativeBranchOffsetEditIsRejected() {
        val instruction = BranchInstruction(Opcode.IFEQ, branchOffset = 3)
        assertEquals(EditResult.INVALID, BranchEditAction().applyValue(instruction, "-1"))
        assertEquals(3, instruction.branchOffset)
    }

    @Test
    fun testValidWideBranchOffsetEditIsApplied() {
        val instruction = WideBranchInstruction(Opcode.GOTO_W, branchOffset = 3)
        assertEquals(EditResult.APPLIED, WideBranchEditAction().applyValue(instruction, "70000"))
        assertEquals(70000, instruction.branchOffset)
    }

    @Test
    fun testIncrementConstantEditIsApplied() {
        val instruction = IncrementInstruction(wide = false)
        assertEquals(EditResult.APPLIED, IncrementConstantEditAction().applyValue(instruction, "5"))
        assertEquals(5, instruction.incrementConst)
    }

    @Test
    fun testImmediateEditActionSets() {
        assertEquals(1, getImmediateEditActions(SimpleImmediateByteInstruction(Opcode.BIPUSH, isWide = false)).size)
        assertEquals(2, getImmediateEditActions(IncrementInstruction(wide = false)).size)
        assertEquals(1, getImmediateEditActions(BranchInstruction(Opcode.IFEQ)).size)
        assertTrue(getImmediateEditActions(SimpleInstruction(Opcode.NOP)).isEmpty())
    }
}
