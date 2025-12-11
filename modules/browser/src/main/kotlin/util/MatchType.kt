/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import org.gjt.jclasslib.browser.BrowserBundle
import org.jetbrains.annotations.Nls
import java.util.regex.Matcher
import java.util.regex.Pattern

enum class MatchType(@param:Nls val verbose: String) {
    EQUALS(BrowserBundle.getString("match.equals")) {
        override fun matches(string: String, spec: String, matcher: Matcher?): Boolean {
            return string == spec
        }
    },
    START_WITH(BrowserBundle.getString("match.starts.with")) {
        override fun matches(string: String, spec: String, matcher: Matcher?): Boolean {
            return string.startsWith(spec)
        }
    },
    ENDS_WIDTH(BrowserBundle.getString("match.ends.with")) {
        override fun matches(string: String, spec: String, matcher: Matcher?): Boolean {
            return string.endsWith(spec)
        }
    },
    CONTAINS(BrowserBundle.getString("match.contains")) {
        override fun matches(string: String, spec: String, matcher: Matcher?): Boolean {
            return string.contains(spec)
        }
    },
    REGEX(BrowserBundle.getString("match.regex")) {
        override fun matches(string: String, spec: String, matcher: Matcher?): Boolean {
            requireNotNull(matcher)
            matcher.reset(string)
            return matcher.matches()
        }

        override fun createMatcher(spec: String): Matcher? {
            return Pattern.compile(spec).matcher("")
        }
    };

    abstract fun matches(string: String, spec: String, matcher: Matcher?): Boolean
    open fun createMatcher(spec: String): Matcher? = null

    override fun toString() = verbose
}