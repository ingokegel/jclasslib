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
    Base class for numeric constant pool data structures.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public abstract class ConstantNumeric extends CPInfo {

    /** Length of the constant pool data structure in bytes. */
    public static final int SIZE = 4;
    
    /** <tt>bytes</tt> field. */
    protected int bytes;
    
    /**
        Get the <tt>bytes</tt> field of this constant pool entry.
        @return the <tt>bytes</tt> field
     */
    public int getBytes() {
        return bytes;
    }

    /**
        Set the <tt>bytes</tt> field of this constant pool entry.
        @param bytes the <tt>bytes</tt> field
     */
    public void setBytes(int bytes) {
        this.bytes = bytes;
    }

    /**
        Get the the <tt>bytes</tt> field of this constant pool
        entry as a hex string.
        @return the hex string
     */
    public String getFormattedBytes() {
        return printBytes(bytes);
    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {
            
        bytes = in.readInt();
    }
    
    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {
        
        out.writeInt(bytes);
    }
    
    public boolean equals(Object object) {
        if (!(object instanceof ConstantNumeric)) {
            return false;
        }
        ConstantNumeric constantNumeric = (ConstantNumeric)object;
        return super.equals(object) && constantNumeric.bytes == bytes;
    }

    public int hashCode() {
        return super.hashCode() ^ bytes;
    }
    
}
