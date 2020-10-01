/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.structures.attributes.RecordAttribute
import org.gjt.jclasslib.structures.attributes.RecordEntry
import java.util.*

class RecordAttributeDetailPane(services: BrowserServices) : TableDetailPane<RecordAttribute>(RecordAttribute::class.java, services) {

    override fun createTableModel(attribute: RecordAttribute) = AttributeTableModel(attribute.entries)

    override val rowHeightFactor: Float
        get() = 2f

    inner class AttributeTableModel(rows: Array<RecordEntry>) : ColumnTableModel<RecordEntry>(rows) {

        override fun buildColumns(columns: ArrayList<Column<RecordEntry>>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : NamedConstantPoolLinkColumn<RecordEntry>(getString("column.name"), services, 200) {
                    override fun getConstantPoolIndex(row: RecordEntry) = row.nameIndex
                })
                add(object : NamedConstantPoolLinkColumn<RecordEntry>(getString("column.descriptor"), services, 200) {
                    override fun getConstantPoolIndex(row: RecordEntry) = row.descriptionIndex
                })
                //The record entries can hold additional attributes
                add(object : StringColumn<RecordEntry>(getString("tree.attributes"), 200) {
                    override fun createValue(row: RecordEntry)  = "${row.attributes.size}: " +
                            row.attributes.joinToString { it -> "${it.name}" }
                })
            }
        }
    }
}
