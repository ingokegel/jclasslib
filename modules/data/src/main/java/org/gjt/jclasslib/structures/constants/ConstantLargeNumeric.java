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
    Base class for large numeric constant pool data structures.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public abstract class ConstantLargeNumeric extends CPInfo {

    /** Length of the constant pool data structure in bytes. */
    public static final int SIZE = 8;
    
    /** <tt>high_bytes</tt> field. */
    protected int highBytes;
    /** <tt>low_bytes</tt> field. */
    protected int lowBytes;
    
    /**
        Get the <tt>high_bytes</tt> field of this constant pool entry.
        @return the <tt>high_bytes</tt> field
     */
    public int getHighBytes() {
        return highBytes;
    }

    /**
        Set the <tt>high_bytes</tt> field of this constant pool entry.
        @param highBytes the <tt>high_bytes</tt> field
     */
    public void setHighBytes(int highBytes) {
        this.highBytes = highBytes;
    }

    /**
        Get the <tt>low_bytes</tt> field of this constant pool entry.
        @return the <tt>low_bytes</tt> field
     */
    public int getLowBytes() {
        return lowBytes;
    }

    /**
        Set the <tt>low_bytes</tt> field of this constant pool entry.
        @param lowBytes the <tt>low_bytes</tt> field
     */
    public void setLowBytes(int lowBytes) {
        this.lowBytes = lowBytes;
    }
    
    /**
        Get the the <tt>high_bytes</tt> field of this constant pool
        entry as a hex string.
        @return the hex string
     */
    public String getFormattedHighBytes() {
        return printBytes(highBytes);
    }

    /**
        Get the the <tt>low_bytes</tt> field of this constant pool
        entry as a hex string.
        @return the hex string
     */
    public String getFormattedLowBytes() {
        return printBytes(lowBytes);
    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {
            
        highBytes = in.readInt();
        lowBytes = in.readInt();
    }

    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {
        
        out.writeInt(highBytes);
        out.writeInt(lowBytes);
    }
    
    public boolean equals(Object object) {
        if (!(object instanceof ConstantLargeNumeric)) {
            return false;
        }
        ConstantLargeNumeric constantLargeNumeric = (ConstantLargeNumeric)object;
        return super.equals(object) &&
               constantLargeNumeric.highBytes == highBytes &&
               constantLargeNumeric.lowBytes == lowBytes;
    }

    public int hashCode() {
        return super.hashCode() ^ highBytes ^ lowBytes;
    }
    
}
