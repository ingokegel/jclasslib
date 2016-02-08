/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.util.HtmlDisplayTextArea
import org.gjt.jclasslib.util.TextDisplay
import javax.swing.event.HyperlinkEvent

class LineControl<T : Any>(private val keyLabel: TextDisplay, private val nameLabel: HtmlDisplayTextArea?, private val nameVerboseLabel: HtmlDisplayTextArea? = null) {

    private var visibilityPredicate : ((T) -> Boolean)? = null

    fun linkHandler(handler: (String) -> Unit) : LineControl<T> {
        nameLabel?.addHyperlinkListener{ e ->
            if (e.eventType == HyperlinkEvent.EventType.ACTIVATED) {
                val description = e.description
                handler(description)
            }
        }
        return this
    }

    fun visibilityPredicate(visibilityPredicate: (T) -> Boolean) : LineControl<T> {
        this.visibilityPredicate = visibilityPredicate
        return this
    }

    fun show(element: T) {
        visibilityPredicate?.let {
            val show = it(element)
            keyLabel.setVisible(show)
            nameLabel?.setVisible(show)
            nameVerboseLabel?.setVisible(show)
        }
    }

}