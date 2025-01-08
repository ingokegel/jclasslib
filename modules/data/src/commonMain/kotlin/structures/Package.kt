/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

@file:JvmName("Package")

package org.gjt.jclasslib.structures

import kotlinx.io.*
import org.gjt.jclasslib.getSystemProperty
import org.gjt.jclasslib.io.CountingDataInput
import org.gjt.jclasslib.io.CountingDataOutput
import org.gjt.jclasslib.io.DataInput
import org.gjt.jclasslib.io.DataOutput
import kotlin.jvm.JvmName
import kotlin.reflect.KClass

/**
 * Set this JVM System property to true to switch on debugging for
 * reading and writing class files.
 */
const val SYSTEM_PROPERTY_DEBUG = "jclasslib.io.debug"

/**
 * Set this system property to skip reading all attributes
 */
const val SYSTEM_PROPERTY_SKIP_ATTRIBUTES = "jclasslib.io.skipAttributes"

internal var isDebug: Boolean = getSystemProperty(SYSTEM_PROPERTY_DEBUG) == "true"

internal fun warning(message: String) {
    print("[warning] ")
    println(message)
}

@OptIn(InternalIoApi::class)
internal fun debug(message: String, input: DataInput) {
    print("[debug] ")
    (input as? CountingDataInput)?.let {
        print("+" + it.bytesRead + " ")
    }
    println(message)
}

@OptIn(InternalIoApi::class)
internal fun debug(message: String, output: DataOutput) {
    print("[debug] ")
    (output as? CountingDataOutput)?.let {
        print("+" + it.bytesWritten + " ")
    }
    println(message)
}

internal val Int.hex: String
    get() = "0x${toString(16)}"

internal fun Int.paddedHex(length: Int): String = "0x" + this.toString(16).padStart(length, '0')

internal fun Source.readUnsignedShort(): Int = readUShort().toInt()
internal fun Sink.writeShort(value: Int) = writeUShort(value.toUShort())

internal fun Source.readUnsignedByte(): Int = readUByte().toInt()
internal fun Sink.writeByte(value: Int) = writeUByte(value.toUByte())

private val arraySingletons = hashMapOf<KClass<*>, Array<*>>()

@Suppress("UNCHECKED_CAST")
internal inline fun <reified T> emptyArraySingleton(): Array<T> = arraySingletons.getOrPut(T::class) {
    arrayOfNulls<T>(0)
} as Array<T>

