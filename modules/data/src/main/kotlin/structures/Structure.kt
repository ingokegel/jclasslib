/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

/**
 * Test
 */
package org.gjt.jclasslib.structures

import java.io.DataInput
import java.io.DataOutput

/**
 * Base class for all structures defined in the class file format.
 * Provides common services such as reading, writing and debugging.
 */
abstract class Structure {

    /**
     * Read this structure from the given DataInput.
     * Expects DataInput to be in JVM class file format and just
     * before a structure of this kind.
     *
     * If the system property [SYSTEM_PROPERTY_DEBUG] is set, logging output
     * is printed.
     * @param input the DataInput for reading
     */
    fun read(input: DataInput) {
        readData(input)
        if (isDebug) debug("read ${this::class.java.simpleName} $debugInfo", input)
    }

    /**
     * Implement this method to read the structure.
     * @param input the DataInput for reading
     */
    protected abstract fun readData(input: DataInput)

    /**
     * Write this structure to the given DataOutput.
     * The written bytes are in JVM class file format.
     *
     * If the system property [SYSTEM_PROPERTY_DEBUG] is set, logging output
     * is printed.
     * @param output the DataOutput for writing
     */
    fun write(output: DataOutput) {
        writeData(output)
        if (isDebug) debug("wrote ${this::class.java.simpleName} $debugInfo", output)
    }

    /**
     * Implement this method to read the structure.
     * @param output the DataOutput for writing
     */
    protected abstract fun writeData(output: DataOutput)

    /**
     * Returns specific debugging information for this structure.
     */
    protected abstract val debugInfo: String

    /**
     * Utility method for derived structures. Format an int value as a hex string.
     * @param bytes the int value to print as a hex string
     * @return the hex string
     */
    protected fun formatBytes(bytes: Int): String = bytes.paddedHex(8)

    /**
     * Utility method for derived structures. Format a class file flag or an
     * unsigned short value as a hex string.
     * @param flags the unsigned short value to print as a hex string
     * @return the hex string
     */
    protected fun formatFlags(flags: Int): String = flags.paddedHex(4)

    /**
     * Utility method for derived structures. Format a class file flag as
     * a space-separated list of verbose modifiers.
     * @param availableFlags array with the flags available for the derived structure
     * @param flags the unsigned short value to print in verbose form
     * @return the verbose description
     */
    protected fun formatFlagsVerbose(availableFlags: Set<ClassFileFlag>, flags: Int, separator: String = " "): String {
        val matchingFlags = availableFlags.filter { (flags and it.flag) != 0 }
        val handledFlags = matchingFlags.fold(0) { value, accessFlag -> value or accessFlag.flag }
        return matchingFlags
                .filterNot { it.historical }
                .mapNotNull { it.verbose.ifEmpty { null } }
                .joinToString(separator = separator, postfix = if (flags != handledFlags) "?" else "")
    }

}
