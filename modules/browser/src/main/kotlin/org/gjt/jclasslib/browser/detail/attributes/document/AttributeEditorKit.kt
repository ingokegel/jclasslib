/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes.document

import javax.swing.text.*

class AttributeEditorKit : StyledEditorKit() {

    override fun getViewFactory() = AttributeEditorViewFactory()

    class AttributeEditorViewFactory : ViewFactory {
        override fun create(element: Element) = when (element.name) {
            AbstractDocument.ContentElementName -> LabelView(element)
            AbstractDocument.ParagraphElementName -> LineView(element)
            AbstractDocument.SectionElementName -> BoxView(element, View.Y_AXIS)
            StyleConstants.ComponentElementName -> ComponentView(element)
            StyleConstants.IconElementName -> IconView(element)
            else -> LabelView(element)
        }
    }

    class LineView(element: Element) : ParagraphView(element) {

        override fun isVisible() = true
        override fun getMinimumSpan(axis: Int) = getPreferredSpan(axis)
        override fun getResizeWeight(axis: Int) = when (axis) {
            View.X_AXIS -> 1
            View.Y_AXIS -> 0
            else -> throw IllegalArgumentException("Invalid axis: $axis")
        }

        override fun getAlignment(axis: Int) = if (axis == View.X_AXIS) {
            0f
        } else {
            super.getAlignment(axis)
        }

        override fun layout(width: Int, height: Int) {
            super.layout(Integer.MAX_VALUE - 1, height)
        }
    }
}
