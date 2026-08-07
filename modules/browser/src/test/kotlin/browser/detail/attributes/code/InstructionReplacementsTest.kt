/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.bytecode.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InstructionReplacementsTest {

    @Test
    fun testCurrentOpcodeIsNeverOfferedAsReplacement() {
        val instructions = listOf<Instruction>(
                SimpleInstruction(Opcode.IADD),
                SimpleImmediateByteInstruction(Opcode.BIPUSH, isWide = false),
                SimpleImmediateShortInstruction(Opcode.LDC_W),
                BranchInstruction(Opcode.IFEQ),
                WideBranchInstruction(Opcode.GOTO_W)
        )
        instructions.forEach { instruction ->
            assertFalse(instruction.opcode in getReplacementOpcodes(instruction))
            assertFalse(instruction.opcode in getStackCompatibleReplacementOpcodes(instruction))
        }
    }

    @Test
    fun testReplacementsStayWithinStructuralClass() {
        assertTrue(Opcode.ISUB in getReplacementOpcodes(SimpleInstruction(Opcode.IADD)))
        assertFalse(Opcode.BIPUSH in getReplacementOpcodes(SimpleInstruction(Opcode.IADD)))
        assertFalse(Opcode.IFEQ in getReplacementOpcodes(SimpleInstruction(Opcode.IADD)))

        assertTrue(Opcode.SIPUSH in getReplacementOpcodes(SimpleImmediateShortInstruction(Opcode.LDC_W)))
        assertFalse(Opcode.LDC in getReplacementOpcodes(SimpleImmediateShortInstruction(Opcode.LDC_W)))

        assertTrue(Opcode.GOTO in getReplacementOpcodes(BranchInstruction(Opcode.IFEQ)))
        assertFalse(Opcode.IADD in getReplacementOpcodes(BranchInstruction(Opcode.IFEQ)))

        assertEquals(listOf(Opcode.JSR_W), getReplacementOpcodes(WideBranchInstruction(Opcode.GOTO_W)))
    }

    @Test
    fun testStackCompatibleReplacementsRequireSameStackEffect() {
        val replacements = getStackCompatibleReplacementOpcodes(SimpleInstruction(Opcode.IADD))

        assertTrue(Opcode.ISUB in replacements)
        assertTrue(Opcode.IMUL in replacements)
        assertTrue(Opcode.IAND in replacements)

        assertFalse(Opcode.LADD in replacements)
        assertFalse(Opcode.INEG in replacements)
        assertFalse(Opcode.POP in replacements)
        assertFalse(Opcode.ICONST_0 in replacements)
    }

    @Test
    fun testStackCompatibleBranchReplacements() {
        val replacements = getStackCompatibleReplacementOpcodes(BranchInstruction(Opcode.IFEQ))

        assertTrue(Opcode.IFNE in replacements)
        assertTrue(Opcode.IFNULL in replacements)

        assertFalse(Opcode.IF_ICMPEQ in replacements)
        assertFalse(Opcode.GOTO in replacements)
        assertFalse(Opcode.JSR in replacements)
    }

    @Test
    fun testInstructionTypesWithoutReplacementSupport() {
        assertTrue(getReplacementOpcodes(IncrementInstruction(wide = false)).isEmpty())
        assertTrue(getStackCompatibleReplacementOpcodes(IncrementInstruction(wide = false)).isEmpty())
    }

    @Test
    fun testOpcodesWithoutStackInfoHaveNoCompatibleReplacements() {
        assertTrue(getReplacementOpcodes(SimpleInstruction(Opcode.BREAKPOINT)).isNotEmpty())
        assertTrue(getStackCompatibleReplacementOpcodes(SimpleInstruction(Opcode.BREAKPOINT)).isEmpty())
    }

    @Test
    fun testWideBranchReplacementIsNotStackCompatible() {
        assertTrue(getStackCompatibleReplacementOpcodes(WideBranchInstruction(Opcode.GOTO_W)).isEmpty())
    }

    @Test
    fun testStackChangeValueEquality() {
        val intToInt = SingleStackChange(listOf(StackValueType.INTEGER), listOf(StackValueType.INTEGER))

        assertTrue(intToInt.isReplacementCompatibleWith(
                SingleStackChange(listOf(StackValueType.INTEGER), listOf(StackValueType.INTEGER))))
        assertFalse(intToInt.isReplacementCompatibleWith(
                SingleStackChange(listOf(StackValueType.LONG), listOf(StackValueType.INTEGER))))
        assertFalse(intToInt.isReplacementCompatibleWith(
                SingleStackChange(listOf(StackValueType.INTEGER), listOf(StackValueType.LONG))))
        assertFalse(intToInt.isReplacementCompatibleWith(
                SingleStackChange(listOf(StackValueType.INTEGER, StackValueType.INTEGER), listOf(StackValueType.INTEGER))))
    }
}
