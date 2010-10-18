/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.*;

/**
    Describes a <tt>ConstantValue</tt> attribute structure.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.4 $ $Date: 2003-08-18 07:52:05 $
*/
public class ConstantValueAttribute extends AttributeInfo {

    /** Name of the attribute as in the corresponding constant pool entry. */
    public static final String ATTRIBUTE_NAME = "ConstantValue";

    private static final int LENGTH = 2;

    private int constantvalueIndex;
    
    /**
        Get the constant pool index of the constant value.
        @return the index
     */
    public int getConstantvalueIndex() {
        return constantvalueIndex;
    }

    /**
        Set the constant pool index of the constant value.
        @param constantvalueIndex the index
     */
    public void setConstantvalueIndex(int constantvalueIndex) {
        this.constantvalueIndex = constantvalueIndex;
    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {
            
        constantvalueIndex = in.readUnsignedShort();
        if (debug) debug("read ");
    }

    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {
        
        super.write(out);
        out.writeShort(constantvalueIndex);
        if (debug) debug("wrote ");
    }

    public int getAttributeLength() {
        return LENGTH;
    }

    protected void debug(String message) {
        super.debug(message + "ConstantValue attribute with constantvalue_index " + constantvalueIndex);
    }

}
