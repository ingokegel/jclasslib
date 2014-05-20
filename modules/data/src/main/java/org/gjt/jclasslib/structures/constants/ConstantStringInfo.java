/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.constants;

import org.gjt.jclasslib.structures.CPInfo;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
    Describes a <tt>CONSTANT_String_info</tt> constant pool data structure.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ConstantStringInfo extends CPInfo {

    /** Length of the constant pool data structure in bytes. */
    public static final int SIZE = 2;
    
    private int stringIndex;
    
    public byte getTag() {
        return CONSTANT_STRING;
    }

    public String getTagVerbose() {
        return CONSTANT_STRING_VERBOSE;
    }
    
    public String getVerbose() throws InvalidByteCodeException {
        return classFile.getConstantPoolEntryName(stringIndex);
    }

    /**
        Get the index of the constant pool entry containing the
        string of this entry.
        @return the index
     */
    public int getStringIndex() {
        return stringIndex;
    }

    /**
        Set the index of the constant pool entry containing the
        string of this entry.
        @param stringIndex the index
     */
    public void setStringIndex(int stringIndex) {
        this.stringIndex = stringIndex;
    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {
            
        stringIndex = in.readUnsignedShort();
        if (debug) debug("read ");
    }
    
    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {

        out.writeByte(CONSTANT_STRING);
        out.writeShort(stringIndex);
        if (debug) debug("wrote ");
    }

    protected void debug(String message) {
        super.debug(message + getTagVerbose() + " with string_index " + stringIndex);
    }

    public boolean equals(Object object) {
        if (!(object instanceof ConstantStringInfo)) {
            return false;
        }
        ConstantStringInfo constantStringInfo = (ConstantStringInfo)object;
        return super.equals(object) && constantStringInfo.stringIndex == stringIndex;
    }

    public int hashCode() {
        return super.hashCode() ^ stringIndex;
    }
    
}
