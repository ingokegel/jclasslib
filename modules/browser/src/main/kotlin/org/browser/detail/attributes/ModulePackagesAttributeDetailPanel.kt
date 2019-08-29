/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.structures.attributes.ModulePackagesAttribute
import java.util.*

class ModulePackagesAttributeDetailPane(services: BrowserServices) : TableDetailPane<ModulePackagesAttribute>(ModulePackagesAttribute::class.java, services) {

    override fun createTableModel(attribute: ModulePackagesAttribute) = AttributeTableModel(attribute.indices)

    inner class AttributeTableModel(rows: IntArray) : ColumnTableModel<Int>(rows.toTypedArray()) {
        override fun buildColumns(columns: ArrayList<Column<Int>>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : ConstantPoolLinkColumn<Int>("Package", services) {
                    override fun getConstantPoolIndex(row: Int) = row
                })
                add(object : StringColumn<Int>("Verbose", 400) {
                    override fun createValue(row: Int) = getConstantPoolEntryName(row)
                })
            }
        }
    }
}
