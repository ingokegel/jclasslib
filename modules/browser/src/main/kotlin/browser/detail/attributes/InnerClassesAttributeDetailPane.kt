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
import org.gjt.jclasslib.structures.attributes.InnerClassesAttribute
import org.gjt.jclasslib.structures.attributes.InnerClassesEntry
import java.util.*

class InnerClassesAttributeDetailPane(services: BrowserServices) : TableDetailPane<InnerClassesAttribute>(InnerClassesAttribute::class.java, services) {

    override fun createTableModel(attribute: InnerClassesAttribute) = AttributeTableModel(attribute.classes)

    override val rowHeightFactor: Float
        get() = 2f

    inner class AttributeTableModel(rows: Array<InnerClassesEntry>) : ColumnTableModel<InnerClassesEntry>(rows) {

        override fun buildColumns(columns: ArrayList<Column<InnerClassesEntry>>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : NamedConstantPoolLinkColumn<InnerClassesEntry>(getString("column.inner.class"), services, 160) {
                    override fun getConstantPoolIndex(row: InnerClassesEntry) = row.innerClassInfoIndex
                })
                add(object : NamedConstantPoolLinkColumn<InnerClassesEntry>(getString("column.outer.class"), services, 160) {
                    override fun getConstantPoolIndex(row: InnerClassesEntry) = row.outerClassInfoIndex
                })
                add(object : NamedConstantPoolLinkColumn<InnerClassesEntry>(getString("column.inner.name"), services, 110) {
                    override fun getConstantPoolIndex(row: InnerClassesEntry) = row.innerNameIndex
                })
                add(object : StringColumn<InnerClassesEntry>(getString("column.access.flags"), 200) {
                    override fun createValue(row: InnerClassesEntry) =
                            "${row.innerClassFormattedAccessFlags} [${row.innerClassAccessFlagsVerbose}]"
                })
            }
        }
    }
}
