/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.bytecode.BranchInstruction
import org.gjt.jclasslib.bytecode.Opcode
import org.gjt.jclasslib.bytecode.SimpleImmediateByteInstruction
import org.gjt.jclasslib.testutil.SwingRobotTest
import org.gjt.jclasslib.testutil.withFakeAlertFacade
import org.gjt.jclasslib.util.AlertType
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ImmediateEditActionUiTest : SwingRobotTest() {

    @Test
    fun testValidEditViaDialogAppliesValue() = withFakeAlertFacade { alerts ->
        val instruction = SimpleImmediateByteInstruction(Opcode.BIPUSH, isWide = false, immediateByte = 5)
        val executed = driveInputDialog("200") {
            ImmediateByteEditAction("test").execute(instruction, null)
        }
        assertTrue(executed)
        assertEquals(200, instruction.immediateByte)
        assertTrue(alerts.messages.isEmpty())
    }

    @Test
    fun testInvalidEditViaDialogShowsWarning() = withFakeAlertFacade { alerts ->
        val instruction = SimpleImmediateByteInstruction(Opcode.BIPUSH, isWide = false, immediateByte = 5)
        val executed = driveInputDialog("abc") {
            ImmediateByteEditAction("test").execute(instruction, null)
        }
        assertFalse(executed)
        assertEquals(5, instruction.immediateByte)
        assertEquals(1, alerts.messages.size)
        assertEquals(AlertType.WARNING, alerts.messages[0].alertType)
    }

    @Test
    fun testOutOfRangeEditViaDialogShowsWarning() = withFakeAlertFacade { alerts ->
        val instruction = BranchInstruction(Opcode.IFEQ, branchOffset = 3)
        val executed = driveInputDialog("70000") {
            BranchEditAction().execute(instruction, null)
        }
        assertFalse(executed)
        assertEquals(3, instruction.branchOffset)
        assertEquals(1, alerts.messages.size)
        assertEquals(AlertType.WARNING, alerts.messages[0].alertType)
    }

    @Test
    fun testCancelledEditViaDialogIsNotExecuted() = withFakeAlertFacade { alerts ->
        val instruction = SimpleImmediateByteInstruction(Opcode.BIPUSH, isWide = false, immediateByte = 5)
        val executed = driveInputDialog(null) {
            ImmediateByteEditAction("test").execute(instruction, null)
        }
        assertFalse(executed)
        assertEquals(5, instruction.immediateByte)
        assertTrue(alerts.messages.isEmpty())
    }
}
