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
    Describes a <tt>CONSTANT_Utf8_info</tt> constant pool data structure.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:19 $
*/
public class ConstantUtf8Info extends CPInfo {

    private byte[] bytes;
    private String string;
    
    public byte getTag() {
        return CONSTANT_UTF8;
    }

    public String getTagVerbose() {
        return CONSTANT_UTF8_VERBOSE;
    }
    
    public String getVerbose() throws InvalidByteCodeException {
        return string;
    }

    /**
        Get the byte array of the string in this entry.
        @return the array
     */
    public byte[] getBytes() {
        return bytes;
    }
    
    /**
        Set the byte array of the string in this entry.
        @param bytes the array
     */
    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    /**
        Get the string in this entry.
        @return the string
     */
    public String getString() {
        return string;
    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {
            
        int length = in.readUnsignedShort();
        bytes = new byte[length];
        in.readFully(bytes);
        string = new String(bytes, "UTF-8");
        if (debug) debug("read ");
    }
    
    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {

        out.writeByte(CONSTANT_UTF8);
        out.writeShort(getLength(bytes));
        out.write(bytes);
        if (debug) debug("wrote ");
    }

    protected void debug(String message) {
        super.debug(message + getTagVerbose() + " with length " + getLength(bytes) +
                    " (\"" + string  + "\")");
    }

}
