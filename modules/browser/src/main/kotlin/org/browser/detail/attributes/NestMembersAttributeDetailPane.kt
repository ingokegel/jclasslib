/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.TableDetailPane
import org.gjt.jclasslib.structures.attributes.NestMembersAttribute
import org.gjt.jclasslib.structures.attributes.NestMembersEntry
import java.util.*

class NestMembersAttributeDetailPane(services: BrowserServices) : TableDetailPane<NestMembersAttribute>(NestMembersAttribute::class.java, services) {

    override fun createTableModel(attribute: NestMembersAttribute) = AttributeTableModel(attribute.entries)

    override val rowHeightFactor: Float
        get() = 2f

    inner class AttributeTableModel(rows: Array<NestMembersEntry>) : ColumnTableModel<NestMembersEntry>(rows) {

        override fun buildColumns(columns: ArrayList<Column<NestMembersEntry>>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : NamedConstantPoolLinkColumn<NestMembersEntry>("Class Name", services, 200) {
                    override fun getConstantPoolIndex(row: NestMembersEntry) = row.classInfoIndex
                })
            }
        }
    }
}
