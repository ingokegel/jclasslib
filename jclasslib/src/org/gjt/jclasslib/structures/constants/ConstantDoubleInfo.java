/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.constants;

import org.gjt.jclasslib.structures.*;

import java.io.*;

/**
    Describes a <tt>CONSTANT_Double_info</tt> constant pool data structure.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:19 $
*/
public class ConstantDoubleInfo extends ConstantLargeNumeric {

    public byte getTag() {
        return CONSTANT_DOUBLE;
    }

    public String getTagVerbose() {
        return CONSTANT_DOUBLE_VERBOSE;
    }

    public String getVerbose() throws InvalidByteCodeException {
        return String.valueOf(getDouble());
    }
    
    /**
        Get the double value of this constant pool entry.
        @return the value
     */
    public double getDouble() {
        long longBits = ((long)highBytes << 32) | ((long)lowBytes & 0x7FFFFFFF);
        return Double.longBitsToDouble(longBits);
    }

    /**
        Set the double value of this constant pool entry.
        @param number the value
     */
    public void setDouble(double number) {
        long longBits = Double.doubleToLongBits(number);
        highBytes = (int)(longBits >>> 32);
        lowBytes = (int)(longBits & 0x0000FFFF);
    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {
        
        super.read(in);
        if (debug) debug("read ");
    }

    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {
        
        out.writeByte(CONSTANT_DOUBLE);
        super.write(out);
        if (debug) debug("wrote ");
    }
    
    protected void debug(String message) {
        super.debug(message + getTagVerbose() + " with high_bytes " + highBytes + 
              " and low_bytes " + lowBytes);
    }
}
