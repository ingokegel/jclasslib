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
    Describes a <tt>CONSTANT_Float_info</tt> constant pool data structure.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ConstantFloatInfo extends ConstantNumeric {

    public byte getTag() {
        return CONSTANT_FLOAT;
    }

    public String getTagVerbose() {
        return CONSTANT_FLOAT_VERBOSE;
    }

    public String getVerbose() throws InvalidByteCodeException {
        return String.valueOf(getFloat());
    }
    
    /**
        Get the float value of this constant pool entry.
        @return the value
     */
    public float getFloat() {
        return Float.intBitsToFloat(bytes);
    }

    /**
        Set the float value of this constant pool entry.
        @param number the value
     */
    public void setFloat(float number) {
        bytes = Float.floatToIntBits(number);
    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {
        
        super.read(in);
        if (debug) debug("read ");
    }
    
     public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {
        
        out.writeByte(CONSTANT_FLOAT);
        super.write(out);
        if (debug) debug("wrote ");
    }
    
    protected void debug(String message) {
        super.debug(message + getTagVerbose() + " with bytes " + bytes);
    }

}
