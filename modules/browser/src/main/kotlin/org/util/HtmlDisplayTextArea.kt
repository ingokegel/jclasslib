/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import java.awt.*
import javax.swing.JEditorPane
import javax.swing.UIManager
import javax.swing.text.StyleConstants
import javax.swing.text.View
import javax.swing.text.html.HTML
import javax.swing.text.html.HTMLDocument
import javax.swing.text.html.HTMLEditorKit
import javax.swing.text.html.StyleSheet

open class HtmlDisplayTextArea(text: String? = null) : JEditorPane(), TextDisplay {

    var inverted: Boolean = false
        set(inverted) {
            field = inverted
            styleSheet?.apply {
                addRule("a {color : #" + getHexValue(if (inverted) foreground else getLinkColor()) + " }")
                addRule("a:active {color : #" + getHexValue(if (inverted) foreground else getActiveLinkColor()) + " }")
            }
        }

    init {
        isEditable = false
        background = UIManager.getColor("Label.background")
        isRequestFocusEnabled = false
        isFocusable = false
        margin = NO_MARGIN
        isOpaque = false
        updateUI()

        editorKit = HTMLEditorKit()

        styleSheet?.apply {
            addRule("body {color : #" + getHexValue(UIManager.getColor("Label.foreground")) + " }")
            val font = UIManager.getFont("Label.font")
            addRule("body {font-size : " + font.size + "pt; }")
            addRule("body {font-family :" + font.fontName + "; }")
        }
        inverted = false

        if (text != null) {
            setText(text)
        }
    }

    override fun getMinimumSize(): Dimension = super.getMinimumSize().apply {
        width = 0
    }

    override fun setForeground(fg: Color) {
        super.setForeground(fg)
        styleSheet?.apply {
            addRule("body {color : #" + getHexValue(fg) + " }")
        }
    }

    private fun getHexValue(color: Color): String = StringBuilder().apply {
        appendComponent(color.red)
        appendComponent(color.green)
        appendComponent(color.blue)
    }.toString()

    private fun StringBuilder.appendComponent(component: Int) {
        append(Integer.toHexString(component).padStart(2, '0'))
    }

    private val htmlDocument: HTMLDocument?
        get() = if (document is HTMLDocument) document as HTMLDocument else null

    private val styleSheet: StyleSheet?
        get() = htmlDocument?.styleSheet

    override fun setText(text: String) {
        super.setText(if (text.startsWith("<html>")) "<html>$text" else text)
    }

    override fun getPreferredSize(): Dimension {
        return super.getPreferredSize()?.apply {
            if (height == 0) {
                height = 10
            }
        } ?: Dimension(400, 400)
    }

    override fun getBaseline(width: Int, height: Int): Int {
        val insets = insets
        val rootView = getUI().getRootView(this)
        if (rootView.viewCount > 0) {
            val baseline = insets.top
            val fieldBaseline = getBaseline(rootView.getView(0),
                    width - insets.left - insets.right,
                    height - insets.top - insets.bottom
            )
            if (fieldBaseline >= 0) {
                return baseline + fieldBaseline
            }
        }
        return -1
    }

    // methods copied from BasicHTML
    private fun getBaseline(view: View, w: Int, h: Int): Int {
        if (hasParagraph(view)) {
            view.setSize(w.toFloat(), h.toFloat())
            return getBaseline(view, Rectangle(0, 0, w, h))
        }
        return -1
    }

    private fun getBaseline(view: View, bounds: Shape?): Int {
        if (view.viewCount == 0) {
            return -1
        }
        val name: Any? = view.element.attributes?.getAttribute(StyleConstants.NameAttribute)
        val index = if (name === HTML.Tag.HTML && view.viewCount > 1) 1 else 0
        val correctedBounds = view.getChildAllocation(index, bounds) ?: return -1
        val child = view.getView(index)
        return if (view is javax.swing.text.ParagraphView) {
            val rect: Rectangle = correctedBounds as? Rectangle ?: correctedBounds.bounds
            rect.y + (rect.height * child.getAlignment(View.Y_AXIS)).toInt()
        } else {
            getBaseline(child, correctedBounds)
        }
    }

    private fun hasParagraph(view: View): Boolean {
        if (view is javax.swing.text.ParagraphView) {
            return true
        }
        if (view.viewCount == 0) {
            return false
        }
        val name: Any? = view.element.attributes?.getAttribute(StyleConstants.NameAttribute)
        val index = if (name === HTML.Tag.HTML && view.viewCount > 1) 1 else 0
        return hasParagraph(view.getView(index))
    }

    override fun getBaselineResizeBehavior() = BaselineResizeBehavior.CONSTANT_ASCENT

    companion object {
        private val NO_MARGIN = Insets(0, 0, 0, 0)
    }

}
