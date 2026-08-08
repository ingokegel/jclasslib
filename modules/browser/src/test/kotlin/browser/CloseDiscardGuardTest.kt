/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.testutil.TestBrowserServices
import org.gjt.jclasslib.testutil.onEdt
import org.gjt.jclasslib.testutil.withFakeAlertFacade
import org.gjt.jclasslib.util.OptionAlertResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CloseDiscardGuardTest {

    @Test
    fun testUnmodifiedClassCanBeClosedWithoutConfirmation() = withFakeAlertFacade { alerts ->
        val browserComponent = onEdt { TestBrowserServices().browserComponent }

        assertTrue(browserComponent.canRemove())
        assertTrue(alerts.optionDialogs.isEmpty())
    }

    @Test
    fun testModifiedClassIsClosedAfterDiscardConfirmation() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = OptionAlertResult(0, false)
        val browserComponent = onEdt { TestBrowserServices().browserComponent.apply { isModified = true } }

        assertTrue(browserComponent.canRemove())
        assertEquals(1, alerts.optionDialogs.size)
    }

    @Test
    fun testModifiedClassIsKeptOpenWhenDiscardIsCancelled() = withFakeAlertFacade { alerts ->
        alerts.nextOptionDialogResult = OptionAlertResult(1, false)
        val browserComponent = onEdt { TestBrowserServices().browserComponent.apply { isModified = true } }

        assertFalse(browserComponent.canRemove())
        assertEquals(1, alerts.optionDialogs.size)
    }
}
