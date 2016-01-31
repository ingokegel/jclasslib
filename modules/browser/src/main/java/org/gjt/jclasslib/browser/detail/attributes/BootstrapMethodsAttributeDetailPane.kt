/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.ConstantPoolHyperlinkListener
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.structures.attributes.BootstrapMethodsAttribute
import org.gjt.jclasslib.structures.attributes.BootstrapMethodsEntry
import org.gjt.jclasslib.util.MultiLineHtmlCellHandler
import java.util.*
import javax.swing.JTable

class BootstrapMethodsAttributeDetailPane(services: BrowserServices) : TableDetailPane<BootstrapMethodsAttribute>(services) {

    override fun createTableModel(attribute: BootstrapMethodsAttribute) = AttributeTableModel(attribute)
    override val attributeClass: Class<BootstrapMethodsAttribute>
        get() = BootstrapMethodsAttribute::class.java

    override val isVariableRowHeight: Boolean
        get() = true

    override val autoResizeMode: Int
        get() = JTable.AUTO_RESIZE_LAST_COLUMN

    protected inner class AttributeTableModel(attribute: BootstrapMethodsAttribute) : ColumnTableModel<BootstrapMethodsAttribute>(attribute) {

        override fun buildColumns(columns: ArrayList<Column>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : NamedConstantPoolLinkColumn("Bootstrap Method", services, 300) {
                    override fun getConstantPoolIndex(rowIndex: Int) = bootstrapMethods[rowIndex].methodRefIndex
                })

                add(object : StringColumn("Arguments", 400) {
                    override fun createValue(rowIndex: Int): String {
                        return bootstrapMethods[rowIndex].verbose.replace("\n", "<br>")
                    }
                    override fun createTableCellRenderer() = createTableCellEditor()
                    override fun createTableCellEditor() = MultiLineHtmlCellHandler() { description ->
                        ConstantPoolHyperlinkListener.link(services, Integer.parseInt(description))
                    }
                    override fun isEditable(rowIndex: Int) = true
                })
            }
        }

        override fun getRowCount(): Int {
            return bootstrapMethods.size
        }

        private val bootstrapMethods: Array<BootstrapMethodsEntry>
            get() = attribute.methods

    }
}
