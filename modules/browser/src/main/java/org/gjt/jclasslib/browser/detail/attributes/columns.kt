/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes

import org.gjt.jclasslib.browser.AbstractDetailPane
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.ConstantPoolHyperlinkListener
import org.gjt.jclasslib.browser.detail.ListDetailPane
import org.gjt.jclasslib.structures.InvalidByteCodeException
import java.util.*
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

abstract class Column(val name: String, val width: Int, val columnClass: Class<*>) {
    open val maxWidth: Int
        get() = Int.MAX_VALUE
    open val minWidth: Int
        get() = 20

    abstract fun getValue(rowIndex: Int): Any

    open fun createTableCellRenderer(): TableCellRenderer? = null
    open fun createTableCellEditor(): TableCellEditor? = null

    open fun link(rowIndex: Int) {
    }

    open fun isEditable(rowIndex: Int) = false
}

abstract class CachingColumn<T : Any>(name: String, width: Int, columnClass: Class<T>) : Column(name, width, columnClass) {
    private val cache = HashMap<Int, T>()

    override fun getValue(rowIndex: Int): Any = cache.getOrPut(rowIndex) {
        createValue(rowIndex)
    }

    protected abstract fun createValue(rowIndex: Int): T
}

abstract class NumberColumn(name: String, width: Int = 60) : CachingColumn<Number>(name, width, Number::class.java)
abstract class StringColumn(name: String, width: Int = 250) : CachingColumn<String>(name, width, String::class.java)
abstract class LinkColumn(name: String, width: Int = 90) : CachingColumn<ListDetailPane.Link>(name, width, ListDetailPane.Link::class.java)
abstract class ConstantPoolLinkColumn(name: String, protected val services: BrowserServices, width: Int = 90) : LinkColumn(name, width) {
    override fun createValue(rowIndex: Int): ListDetailPane.Link {
        val constantPoolIndex = getConstantPoolIndex(rowIndex)
        return ListDetailPane.Link(AbstractDetailPane.CPINFO_LINK_TEXT + constantPoolIndex)
    }

    final override fun link(rowIndex: Int) {
        ConstantPoolHyperlinkListener.link(services, getConstantPoolIndex(rowIndex))
    }

    protected abstract fun getConstantPoolIndex(rowIndex: Int): Int
}
abstract class NamedConstantPoolLinkColumn(name: String, services: BrowserServices, width: Int = 90) : ConstantPoolLinkColumn(name, services, width) {
    final override fun createValue(rowIndex: Int): ListDetailPane.Link {
        val constantPoolIndex = getConstantPoolIndex(rowIndex)
        return LinkRenderer.LinkWithComment(AbstractDetailPane.CPINFO_LINK_TEXT + constantPoolIndex, getComment(constantPoolIndex))
    }

    open protected fun getComment(constantPoolIndex: Int) = getConstantPoolEntryName(constantPoolIndex)

    //TODO duplicate in AbstractDetailPanel, make extension function of BrowserServices
    private fun getConstantPoolEntryName(constantPoolIndex: Int): String {
        try {
            return services.classFile.getConstantPoolEntryName(constantPoolIndex)
        } catch (ex: InvalidByteCodeException) {
            return "invalid constant pool reference"
        }
    }
}

class IndexColumn : NumberColumn("Nr.") {
    override fun createValue(rowIndex: Int) = rowIndex
    override val maxWidth: Int
        get() = width
}


