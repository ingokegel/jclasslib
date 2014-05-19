/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
    Describes a <tt>Deprecated</tt> attribute structure.

 @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class DeprecatedAttribute extends AttributeInfo {

    /** Name of the attribute as in the corresponding constant pool entry. */
    public static final String ATTRIBUTE_NAME = "Deprecated";

    private static final int LENGTH = 0;
    
    public void read(DataInput in) throws InvalidByteCodeException, IOException {
        if (debug) debug("read ");
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);
        if (debug) debug("wrote ");
    }

    public int getAttributeLength() {
        return LENGTH;
    }

    protected void debug(String message) {
        super.debug(message + "Deprecated attribute");
    }

}
