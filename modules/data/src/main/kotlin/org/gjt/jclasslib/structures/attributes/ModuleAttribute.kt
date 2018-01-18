/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.AccessFlag
import org.gjt.jclasslib.structures.AttributeInfo
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.emptyArraySingleton
import java.io.DataInput
import java.io.DataOutput

/**
 * Describes a Module attribute structure.
 */
class ModuleAttribute(classFile: ClassFile) : AttributeInfo(classFile) {

    /**
     * Constant pool index of the CONSTANT_Module_info structure containing the module name.
     */
    var moduleNameIndex: Int = 0

    /**
     * The access flags of the module.
     * See [org.gjt.jclasslib.structures.AccessFlag]
     */
    var moduleFlags: Int = 0

    /**
     * Verbose description of the module access flags.
     */
    val moduleFlagsVerbose: String
        get() = formatAccessFlagsVerbose(AccessFlag.MODULE_FLAGS, moduleFlags)

    /**
     * Constant pool index of the CONSTANT_Utf8_info structure containing the module version.
     * Contains 0 if no information is available.
     */
    var moduleVersionIndex: Int = 0

    /**
     * Requires declarations of the module.
     */
    var requiresEntries: Array<RequiresEntry> = emptyArraySingleton()

    /**
     * Exports statements of the module.
     */
    var exportsEntries: Array<ExportsEntry> = emptyArraySingleton()

    /**
     * Opens statements of the module.
     */
    var opensEntries: Array<ExportsEntry> = emptyArraySingleton()

    /**
     * Uses statements of the module.
     * Contains the indices of CONSTANT_Class_info structures in the constant pool.
     */
    var usesIndices: IntArray = IntArray(0)

    /**
     * Provides statements of the module.
     */
    var providesEntries: Array<ProvidesEntry> = emptyArraySingleton()

    override fun readData(input: DataInput) {
        moduleNameIndex = input.readUnsignedShort()
        moduleFlags = input.readUnsignedShort()
        moduleVersionIndex = input.readUnsignedShort()

        requiresEntries = input.readList(::RequiresEntry)
        exportsEntries = input.readList(::ExportsEntry)
        opensEntries = input.readList(::ExportsEntry)

        val usesCount = input.readUnsignedShort()
        usesIndices = IntArray(usesCount) {
            input.readUnsignedShort()
        }

        providesEntries = input.readList(::ProvidesEntry)
    }

    private inline fun <reified T : SubStructure> DataInput.readList(entryProvider: () -> T) : Array<T>{
        val count = readUnsignedShort()
        return Array(count) {
            entryProvider().apply {
                read(this@readList)
            }
        }
    }

    override fun writeData(output: DataOutput) {
        output.writeShort(moduleNameIndex)
        output.writeShort(moduleFlags)
        output.writeShort(moduleVersionIndex)

        output.writeList(requiresEntries)
        output.writeList(exportsEntries)
        output.writeList(opensEntries)

        output.writeShort(usesIndices.size)
        usesIndices.forEach { output.writeShort(it) }

        output.writeList(providesEntries)
    }

    private fun DataOutput.writeList(entries: Array<out SubStructure>) {
        writeShort(entries.size)
        entries.forEach { it.write(this) }
    }

    override fun getAttributeLength(): Int =  16 +
            requiresEntries.sumBy { it.length } +
            exportsEntries.sumBy { it.length } +
            opensEntries.sumBy { it.length } +
            2 * usesIndices.size +
            providesEntries.sumBy { it.length }

    override val debugInfo: String
        get() = "with ${requiresEntries.size} requires entries,  " +
                "${exportsEntries.size} exports entries,  " +
                "${opensEntries.size} opens entries,  " +
                "${usesIndices.size} uses entries,  " +
                "${providesEntries.size} provides entries"

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        const val ATTRIBUTE_NAME = "Module"
    }
}
