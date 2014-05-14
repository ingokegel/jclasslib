/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures;

/**
    Exception relating to errors in the class file format of internal state
    of the <tt>ClassFile</tt> structure and its substructures.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class InvalidByteCodeException extends Exception {

    /**
        Constructor.
     */
    public InvalidByteCodeException() {
        super();
    }

    /**
        Constructor.
        @param message the message
     */
    public InvalidByteCodeException(String message) {
        super(message);
    }
}
