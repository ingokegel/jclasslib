/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

@file:JvmName("Package")
package org.gjt.jclasslib.structures

/**
 * Set this JVM System property to true to switch on debugging for
 * reading and writing class files.
 */
const val SYSTEM_PROPERTY_DEBUG = "jclasslib.io.debug"

/**
 * Set this system property to skip reading all attributes
 */
const val SYSTEM_PROPERTY_SKIP_ATTRIBUTES = "jclasslib.io.skipAttributes"

/**
 * Flag for debugging while reading and writing class files.
 */
val isDebug: Boolean = java.lang.Boolean.getBoolean(SYSTEM_PROPERTY_DEBUG)

/**
 * Log a warning message.
 * @param message the message
 */
fun warning(message: String) {
    print("[warning] ")
    println(message)
}

/**
 * Log a debug message.
 * @param message the message
 */
fun debug(message: String) {
    print("[debug] ")
    println(message)
}

/**
 * Format the int value in hexadecimal notation
 */
val Int.hex: String
    get() = "0x${Integer.toHexString(this)}"

/**
 * Format the int value in hexadecimal notation and pad with zeros
 * @param length the number of digits in the output
 */
fun Int.paddedHex(length: Int): String {
    return "0x" + this.hex.padStart(length, '0')
}
