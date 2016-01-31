/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.attributes.InnerClassesAttribute
import org.gjt.jclasslib.structures.attributes.InnerClassesEntry
import java.util.*

class InnerClassesAttributeDetailPane(services: BrowserServices) : ColumnListDetailPane<InnerClassesAttribute>(services) {

    override fun createTableModel(attribute: InnerClassesAttribute) = AttributeTableModel(attribute)
    override val attributeClass: Class<InnerClassesAttribute>
        get() = InnerClassesAttribute::class.java

    override val rowHeightFactor: Float
        get() = 2f

    protected inner class AttributeTableModel(attribute: InnerClassesAttribute) : ColumnTableModel<InnerClassesAttribute>(attribute) {

        override fun buildColumns(columns: ArrayList<Column>) {
            super.buildColumns(columns)
            columns.apply {
                add(object : NamedConstantPoolLinkColumn("Inner Class", services, 160) {
                    override fun getConstantPoolIndex(rowIndex: Int) = innerClasses[rowIndex].innerClassInfoIndex
                })
                add(object : NamedConstantPoolLinkColumn("Outer Class", services, 160) {
                    override fun getConstantPoolIndex(rowIndex: Int) = innerClasses[rowIndex].outerClassInfoIndex
                })
                add(object : NamedConstantPoolLinkColumn("Inner Name", services, 110) {
                    override fun getConstantPoolIndex(rowIndex: Int) = innerClasses[rowIndex].innerNameIndex
                })
                add(object : StringColumn("Access Flags", 200) {
                    override fun createValue(rowIndex: Int) =
                            "${innerClasses[rowIndex].innerClassFormattedAccessFlags} [${innerClasses[rowIndex].innerClassAccessFlagsVerbose}]"
                })
            }
        }

        override fun getRowCount(): Int {
            return innerClasses.size
        }

        private val innerClasses: Array<InnerClassesEntry>
            get() = attribute.classes

    }
}
