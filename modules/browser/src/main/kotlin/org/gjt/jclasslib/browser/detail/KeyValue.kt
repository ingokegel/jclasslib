/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.util.ExtendedJLabel
import org.gjt.jclasslib.util.HtmlDisplayTextArea
import org.gjt.jclasslib.util.TextDisplay
import javax.swing.JComponent
import javax.swing.event.HyperlinkEvent

abstract class KeyValue<T : Any, L>(key: String, val valueLabel: L, val commentLabel: L? = null) where L : JComponent, L : TextDisplay {

    val keyLabel = ExtendedJLabel(key)
    private var visibilityPredicate: ((T) -> Boolean)? = null

    fun visibilityPredicate(visibilityPredicate: (T) -> Boolean) {
        this.visibilityPredicate = visibilityPredicate
    }

    fun show(element: T) {
        visibilityPredicate?.let {
            val show = it(element)
            keyLabel.setVisible(show)
            valueLabel.setVisible(show)
            commentLabel?.setVisible(show)
        }
    }
}

class DefaultKeyValue<T : Any>(key: String, valueLabel: ExtendedJLabel, commentLabel: ExtendedJLabel? = null) : KeyValue<T, ExtendedJLabel>(key, valueLabel, commentLabel)

class HtmlKeyValue<T : Any>(key: String, valueLabel: HtmlDisplayTextArea, commentLabel: HtmlDisplayTextArea? = null) : KeyValue<T, HtmlDisplayTextArea>(key, valueLabel, commentLabel) {
    fun linkHandler(handler: (String) -> Unit) {
        valueLabel.addHyperlinkListener { e ->
            if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                val description = e.description
                handler(description)
            }
        }
    }
}