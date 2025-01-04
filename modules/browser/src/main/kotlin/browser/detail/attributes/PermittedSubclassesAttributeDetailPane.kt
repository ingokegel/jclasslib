/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.structures.attributes.PermittedSubclassesAttribute

class PermittedSubclassesAttributeDetailPane(services: BrowserServices) : TableDetailPane<PermittedSubclassesAttribute>(PermittedSubclassesAttribute::class.java, services) {

    override fun createTableModel(attribute: PermittedSubclassesAttribute) = AttributeTableModel(attribute.entries)

    override val rowHeightFactor: Float
        get() = 2f

    inner class AttributeTableModel(rows: Array<Int>) : ColumnTableModel<Int>(rows) {

        override fun buildColumns(columns: ArrayList<Column<Int>>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : NamedConstantPoolLinkColumn<Int>(getString("column.class.name"), services,200) {
                    override fun getConstantPoolIndex(row: Int) = row
                })
            }
        }
    }
}
