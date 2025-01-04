/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.SupportedLocale.entries
import org.jetbrains.annotations.PropertyKey
import java.text.MessageFormat
import java.util.*

object BrowserBundle {
    const val BUNDLE_NAME = "org.gjt.jclasslib.browser.messages.Browser"
    private val overriddenLocale = System.getProperty("jclasslib.locale")

    private val bundle by lazy { ResourceBundle.getBundle(BUNDLE_NAME, if (overriddenLocale == null) Locale.getDefault() else Locale(overriddenLocale)) }

    fun getString(@PropertyKey(resourceBundle = BUNDLE_NAME) key: String, vararg params: Any): String {
        return applyFormat(bundle.getString(key), *params)
    }

    private fun applyFormat(value: String, vararg params: Any): String =
        if (params.isNotEmpty() && value.contains('{')) {
            MessageFormat(value).format(params)
        } else {
            value
        }

}

enum class SupportedLocale(val localeCode: String, val displayName: String) {
    AUTO_DETECT("", getString("menu.language.auto.detect")),
    ENGLISH("en", "English"),
    GERMAN("de", "Deutsch"),
    SIMPLIFIED_CHINESE("zh_CN", "简体中文"),
    POLISH("pl", "Polski");

    companion object {
        fun findByLocaleCode(localeCode: String) : SupportedLocale =
            entries.find { it.localeCode == localeCode } ?: AUTO_DETECT
    }
}
