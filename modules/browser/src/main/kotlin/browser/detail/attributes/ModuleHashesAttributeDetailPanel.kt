/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.KeyValueDetailPane
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.structures.attributes.HashEntry
import org.gjt.jclasslib.structures.attributes.ModuleHashesAttribute

class ModuleHashesAttributeDetailPane(services: BrowserServices) : TableDetailPane<ModuleHashesAttribute>(ModuleHashesAttribute::class.java, services) {

    override fun createKeyValueDetailPane() = ModuleHashesKeyValueDetailPane()
    override fun createTableModel(attribute: ModuleHashesAttribute) = AttributeTableModel(attribute.hashEntries)

    inner class AttributeTableModel(rows: Array<HashEntry>) : ColumnTableModel<HashEntry>(rows) {
        override fun buildColumns(columns: ArrayList<Column<HashEntry>>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : ConstantPoolLinkColumn<HashEntry>(getString("column.module"), services) {
                    override fun getConstantPoolIndex(row: HashEntry) = row.moduleNameIndex
                })
                add(object : StringColumn<HashEntry>(getString("column.verbose")) {
                    override fun createValue(row: HashEntry) = getConstantPoolEntryName(row.moduleNameIndex)
                })
                add(object : StringColumn<HashEntry>(getString("column.hash"), 700) {
                    override fun createValue(row: HashEntry) =
                            row.hashValues.joinToString(separator = "") { it.toString(16).padStart(2, '0') }
                })
            }
        }
    }

    inner class ModuleHashesKeyValueDetailPane : KeyValueDetailPane<ModuleHashesAttribute>(ModuleHashesAttribute::class.java, services) {
        override fun addLabels() {
            addConstantPoolLink(getString("key.algorithm"), ModuleHashesAttribute::algorithmIndex)
        }
    }
}
