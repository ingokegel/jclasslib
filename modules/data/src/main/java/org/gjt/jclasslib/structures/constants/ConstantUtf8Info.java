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
 * Describes a <tt>CONSTANT_Utf8_info</tt> constant pool data structure.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
 *
 */
public class ConstantUtf8Info extends CPInfo {

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
     * Get the byte array of the string in this entry.
     *
     * @return the array
     */
    public byte[] getBytes() {
        return string.getBytes();
    }

    /**
     * Get the string in this entry.
     *
     * @return the string
     */
    public String getString() {
        return string;
    }

    /**
     * Set the byte array of the string in this entry.
     *
     * @param bytes the array
     * @deprecated use <tt>setString</tt> instead
     */
    public void setBytes(byte[] bytes) {
        string = new String(bytes);
    }

    /**
     * Set the string in this entry.
     *
     * @param string the string
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
        super.debug(message + getTagVerbose() + " with length " + string.length() +
                " (\"" + string + "\")");
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
