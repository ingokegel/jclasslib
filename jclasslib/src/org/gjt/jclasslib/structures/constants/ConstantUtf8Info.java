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
    @version $Revision: 1.3 $ $Date: 2002-02-17 17:35:06 $
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
        return string.getBytes();
    }

    /**
        Get the string in this entry.
        @return the string
     */
    public String getString() {
        return string;
    }

    /**
        Set the string in this entry.
        @param string the string
     */
    public void setString(String string) {
        this.string = string;
    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {

        string = in.readUTF();

		if (debug) debug("read ");
    }

    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {

        out.writeByte(CONSTANT_UTF8);
        out.writeUTF(string);
        if (debug) debug("wrote ");
    }

    protected void debug(String message) {
        super.debug(message + getTagVerbose() + " with length " + getLength(bytes) +
                    " (\"" + string  + "\")");
    }

    public boolean equals(Object object) {
        if (!(object instanceof ConstantUtf8Info)) {
            return false;
        }
        ConstantUtf8Info constantUtf8Info = (ConstantUtf8Info)object;
        return super.equals(object) && constantUtf8Info.string.equals(string);
    }

    public int hashCode() {
        return super.hashCode() ^ string.hashCode();
    }
    

}
