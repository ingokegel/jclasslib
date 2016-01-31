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
import org.gjt.jclasslib.structures.InvalidByteCodeException
import java.util.*
import javax.swing.table.TableCellEditor
import javax.swing.table.TableCellRenderer

abstract class Column<T : Any>(val name: String, val width: Int, val columnClass: Class<*>) {
    open val maxWidth: Int
        get() = Int.MAX_VALUE
    open val minWidth: Int
        get() = 20

    abstract fun getValue(row: T): Any

    open fun createTableCellRenderer(): TableCellRenderer? = null
    open fun createTableCellEditor(): TableCellEditor? = null

    open fun link(row: T) {
    }

    open fun isEditable(row: T) = false
}

abstract class CachingColumn<T : Any, R : Any>(name: String, width: Int, columnClass: Class<R>) : Column<T>(name, width, columnClass) {
    private val cache = HashMap<T, R>()

    override fun getValue(row: T): Any = cache.getOrPut(row) {
        createValue(row)
    }

    protected abstract fun createValue(row: T): R
}

abstract class NumberColumn<T: Any>(name: String, width: Int = 60) : CachingColumn<T, Number>(name, width, Number::class.java)
abstract class StringColumn<T: Any>(name: String, width: Int = 250) : CachingColumn<T, String>(name, width, String::class.java)
abstract class LinkColumn<T: Any>(name: String, width: Int = 90) : CachingColumn<T, Link>(name, width, Link::class.java)

abstract class ConstantPoolLinkColumn<T: Any>(name: String, protected val services: BrowserServices, width: Int = 90) : LinkColumn<T>(name, width) {
    override fun createValue(row: T): Link {
        val constantPoolIndex = getConstantPoolIndex(row)
        return Link(AbstractDetailPane.CPINFO_LINK_TEXT + constantPoolIndex)
    }

    final override fun link(row: T) {
        ConstantPoolHyperlinkListener.link(services, getConstantPoolIndex(row))
    }

    protected abstract fun getConstantPoolIndex(row: T): Int
}

abstract class NamedConstantPoolLinkColumn<T: Any>(name: String, services: BrowserServices, width: Int = 90) : ConstantPoolLinkColumn<T>(name, services, width) {
    final override fun createValue(row: T): Link {
        val constantPoolIndex = getConstantPoolIndex(row)
        return LinkWithComment(AbstractDetailPane.CPINFO_LINK_TEXT + constantPoolIndex, getComment(constantPoolIndex))
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

open class Link(val text: String) {
    override fun toString() = text
}
class LinkWithComment(linkValue: String, val commentValue: String) : Link(linkValue)
