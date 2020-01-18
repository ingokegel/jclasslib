/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.KeyValueDetailPane
import org.gjt.jclasslib.browser.detail.attributes.document.AttributeDocument
import org.gjt.jclasslib.browser.detail.attributes.document.DocumentDetailPane
import org.gjt.jclasslib.browser.detail.attributes.document.LineNumberCounts
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.attributes.ExportsEntry
import org.gjt.jclasslib.structures.attributes.ModuleAttribute
import util.LightOrDarkColor
import java.awt.Color
import javax.swing.text.StyleContext

class ModuleAttributeDetailPane(services: BrowserServices) : DocumentDetailPane<ModuleAttribute, ModuleDocument>(ModuleAttribute::class.java, ModuleDocument::class.java, services) {

    override fun createKeyValueDetailPane() = ModuleAttributeValueDetailPane()

    override fun createDocument(styles: StyleContext, attribute: ModuleAttribute, classFile: ClassFile): ModuleDocument =
            ModuleDocument(styles, attribute, classFile)

    override fun offsetToPosition(offset: Int): Int = offset

    inner class ModuleAttributeValueDetailPane : KeyValueDetailPane<ModuleAttribute>(ModuleAttribute::class.java, services) {
        override fun addLabels() {
            addConstantPoolLink("Module version:", ModuleAttribute::moduleVersionIndex)
        }
    }
}

class ModuleDocument(styles: StyleContext, private val attribute: ModuleAttribute, classFile: ClassFile) : AttributeDocument(styles, classFile) {

    init {
        setupDocument()
    }

    override fun addContent(): LineNumberCounts? {
        appendString(attribute.moduleFlagsVerbose, STYLE_NORMAL)
        appendString((if (length > 0) " " else "") + "module", STYLE_NORMAL)
        addConstantPoolLink(attribute.moduleNameIndex)
        appendString(" {", STYLE_NORMAL)

        attribute.requiresEntries.textBlock { requiresEntry ->
            addStatement("requires", requiresEntry.flagsVerbose, requiresEntry.index) {
                if (requiresEntry.versionIndex > 0) {
                    appendString(" version", STYLE_VERSION)
                    addConstantPoolLink(requiresEntry.versionIndex)
                }
            }
        }

        addExportEntries("exports", attribute.exportsEntries)
        addExportEntries("opens", attribute.opensEntries)

        attribute.usesIndices.toTypedArray().textBlock { index ->
            addStatement("uses", "", index)
        }

        attribute.providesEntries.textBlock { providesEntry ->
            addStatement("provides", "", providesEntry.index) {
                appendParameters(providesEntry.withIndices, "with")
            }
        }

        appendString("}", STYLE_NORMAL)
        appendBatchLineFeed()
        return null
    }

    private fun addExportEntries(keyword: String, entries: Array<ExportsEntry>) {
        entries.textBlock { exportEntry ->
            addStatement(keyword, exportEntry.flagsVerbose, exportEntry.index) {
                appendParameters(exportEntry.toIndices, "to")
            }
        }
    }

    private fun addStatement(keyword: String, flagsVerbose: String, index: Int, additional: (() -> Unit)? = null) {
        appendString("$TAB$keyword $flagsVerbose".trimEnd(), STYLE_NORMAL)
        addConstantPoolLink(index)
        additional?.invoke()
        appendBatchLineFeed()
    }

    private fun appendParameters(indices: IntArray, keyword: String) {
        indices.forEachIndexed { i, index ->
            if (i == 0) {
                appendString(" $keyword", STYLE_NORMAL)
            }
            if (indices.size > 1) {
                appendBatchLineFeed()
                appendString(TAB.repeat(2), STYLE_NORMAL)
            }
            addConstantPoolLink(index)
            if (i < indices.size - 1) {
                appendString(",", STYLE_NORMAL)
            }
        }
    }

    private fun <T> Array<T>.textBlock(f: (T) -> Unit) {
        if (!isEmpty() && length > 0) {
            appendBatchLineFeed()
        }
        forEach(f)
    }

    companion object {
        val STYLE_VERSION = style {
            foreground = LightOrDarkColor(Color(255, 0, 255), Color(180, 80, 180))
            bold = true
        }
    }
}
