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
    Describes a <tt>CONSTANT_Class_info</tt> constant pool data structure.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ConstantClassInfo extends CPInfo {

    /** Length of the constant pool data structure in bytes. */
    public static final int SIZE = 2;
    
    private int nameIndex;
    
    public byte getTag() {
        return CONSTANT_CLASS;
    }

    public String getTagVerbose() {
        return CONSTANT_CLASS_VERBOSE;
    }
    
    public String getVerbose() throws InvalidByteCodeException {
        return getName();
    }
    
    /**
        Get the index of the constant pool entry containing the name of the class.
        @return the index
     */
    public int getNameIndex() {
        return nameIndex;
    }

    /**
        Set the index of the constant pool entry containing the name of the class.
        @param nameIndex the index
     */
    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }
    
    /**
        Get the name of the class.
        @return the tag
        @throws InvalidByteCodeException if the byte code is invalid
     */
    public String getName() throws InvalidByteCodeException {
        return classFile.getConstantPoolUtf8Entry(nameIndex).getString();
    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {
            
        nameIndex = in.readUnsignedShort();
        if (debug) debug("read ");
    }

    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {
        
        out.writeByte(CONSTANT_CLASS);
        out.writeShort(nameIndex);
        if (debug) debug("wrote ");
    }
    
    public boolean equals(Object object) {
        if (!(object instanceof ConstantClassInfo)) {
            return false;
        }
        ConstantClassInfo constantClassInfo = (ConstantClassInfo)object;
        return super.equals(object) && constantClassInfo.nameIndex == nameIndex;
    }

    public int hashCode() {
        return super.hashCode() ^ nameIndex;
    }
    
    protected void debug(String message) {
        super.debug(message + getTagVerbose() + " with name_index " + nameIndex);
    }
    
}
