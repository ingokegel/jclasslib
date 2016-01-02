/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io

/**
 * Utility class to log errors, warnings and debug messages.
 */
object Log {

    /**
     * Log an error message.
     * @param message the message
     */
    fun error(message: String) {
        print("[error] ")
        println(message)
    }

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

}
