/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package browser

import org.jetbrains.annotations.PropertyKey
import java.text.MessageFormat
import java.util.*

object BrowserBundle {
    const val BUNDLE_NAME = "org.gjt.jclasslib.browser.messages.Browser"

    private val bundle by lazy { ResourceBundle.getBundle(BUNDLE_NAME) }

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

