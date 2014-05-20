/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io;

/**
    Utility class to log errors, warnings and debug messages.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class Log {

    private Log() {
    }

    /**
        Log an error message.
        @param message the message
     */
    public static void error(String message) {
        System.out.print("[error] ");
        System.out.println(message);
    }

    /**
        Log a warning message.
        @param message the message
     */
    public static void warning(String message) {
        System.out.print("[warning] ");
        System.out.println(message);
    }
    
    /**
        Log a debug message.
        @param message the message
     */
    public static void debug(String message) {
        System.out.print("[debug] ");
        System.out.println(message);
    }
    
}
