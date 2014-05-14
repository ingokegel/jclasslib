/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.constants;

import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
    Describes a <tt>CONSTANT_Long_info</tt> constant pool data structure.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ConstantLongInfo extends ConstantLargeNumeric {

    public byte getTag() {
        return CONSTANT_LONG;
    }

    public String getTagVerbose() {
        return CONSTANT_LONG_VERBOSE;
    }

    public String getVerbose() throws InvalidByteCodeException {
        return String.valueOf(getLong());
    }

    /**
        Get the long value of this constant pool entry.
        @return the value
     */
    public long getLong() {
        return (long)highBytes << 32 | ((long)lowBytes & 0x7FFFFFFF);
    }

    /**
        Set the long value of this constant pool entry.
        @param number the value
     */
    public void setLong(long number) {
        highBytes = (int)(number >>> 32);
        lowBytes = (int)(number & 0x0000FFFF);
    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {
        
        super.read(in);
        if (debug) debug("read ");
    }
    
    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {
        
        out.writeByte(CONSTANT_LONG);
        super.write(out);
        if (debug) debug("wrote ");
    }
    
    protected void debug(String message) {
        super.debug(message + getTagVerbose() + " with high_bytes " + highBytes + 
              " and low_bytes " + lowBytes);
    }

}
