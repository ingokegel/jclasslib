/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import java.awt.Font
import java.util.*
import javax.swing.text.AttributeSet
import javax.swing.text.DefaultStyledDocument
import javax.swing.text.StyleContext

open class BatchDocument(styles : StyleContext = StyleContext()) : DefaultStyledDocument(styles) {

    private val batch = arrayListOf<DefaultStyledDocument.ElementSpec>()
    private val fontCache = HashMap<AttributeSet, Font>()
    private var batchLength : Int = 0

    fun appendBatchString(string: String, attributes: AttributeSet) {
        val attributesCopy = attributes.copyAttributes()
        val chars = string.toCharArray()
        addBatch(DefaultStyledDocument.ElementSpec(attributesCopy, DefaultStyledDocument.ElementSpec.ContentType, chars, 0, string.length))
    }

    open fun appendBatchLineFeed(attributes: AttributeSet) {
        addBatch(ElementSpec(attributes, ElementSpec.ContentType, EOL_ARRAY, 0, 1))
        val paragraph = getParagraphElement(0)
        val paragraphAttributes = paragraph.attributes
        addBatch(DefaultStyledDocument.ElementSpec(null, DefaultStyledDocument.ElementSpec.EndTagType))
        addBatch(DefaultStyledDocument.ElementSpec(paragraphAttributes, DefaultStyledDocument.ElementSpec.StartTagType))
    }

    private fun addBatch(elementSpec: ElementSpec) {
        if (batch.isEmpty()) {
            batchLength = super.getLength()
        }
        batch.add(elementSpec)
        batchLength += elementSpec.length
    }

    fun processBatchUpdates(offset: Int) {
        val inserts = arrayOfNulls<DefaultStyledDocument.ElementSpec>(batch.size)
        batch.toArray<ElementSpec>(inserts)
        super.insert(offset, inserts)
        batch.clear()
        batchLength = 0
    }

    override fun getLength() = if (batch.isEmpty()) {
        super.getLength()
    } else {
        batchLength
    }

    override fun getFont(attr: AttributeSet): Font {
        return fontCache.getOrPut(attr) {
            super.getFont(attr)
        }
    }

    companion object {
        private val EOL_ARRAY = charArrayOf('\n')
    }
}