/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.usages

import org.gjt.jclasslib.util.MatchType
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MatchTypeTest {

    private fun MatchType.matches(string: String, spec: String) = matches(string, spec, createMatcher(spec))

    @Test
    fun testEquals() {
        assertTrue(MatchType.EQUALS.matches("abc", "abc"))
        assertFalse(MatchType.EQUALS.matches("abcd", "abc"))
        assertFalse(MatchType.EQUALS.matches("Abc", "abc"))
    }

    @Test
    fun testStartsWith() {
        assertTrue(MatchType.START_WITH.matches("abcd", "abc"))
        assertTrue(MatchType.START_WITH.matches("abc", "abc"))
        assertFalse(MatchType.START_WITH.matches("xabc", "abc"))
    }

    @Test
    fun testEndsWith() {
        assertTrue(MatchType.ENDS_WIDTH.matches("xabc", "abc"))
        assertFalse(MatchType.ENDS_WIDTH.matches("abcx", "abc"))
    }

    @Test
    fun testContains() {
        assertTrue(MatchType.CONTAINS.matches("xabcx", "abc"))
        assertFalse(MatchType.CONTAINS.matches("ab", "abc"))
    }

    @Test
    fun testRegex() {
        assertTrue(MatchType.REGEX.matches("abc", "a.c"))
        assertTrue(MatchType.REGEX.matches("abc", "a.*"))
        assertFalse(MatchType.REGEX.matches("xabc", "a.c"))
    }
}
