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
    Describes a <tt>CONSTANT_NameAndType_info</tt> constant pool data structure.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ConstantNameAndTypeInfo extends CPInfo {

    /** Length of the constant pool data structure in bytes. */
    public static final int SIZE = 4;
    
    private int nameIndex;
    private int descriptorIndex;
    
    public byte getTag() {
        return CONSTANT_NAME_AND_TYPE;
    }

    public String getTagVerbose() {
        return CONSTANT_NAME_AND_TYPE_VERBOSE;
    }
    
    public String getVerbose() throws InvalidByteCodeException {
        return getName() + getDescriptor();
    }

    /**
        Get the index of the constant pool entry containing the name of this entry.
        @return the index
     */
    public int getNameIndex() {
        return nameIndex;
    }

    /**
        Set the index of the constant pool entry containing the name of this entry.
        @param nameIndex the index
     */
    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    /**
        Get the index of the constant pool entry containing the descriptor of this entry.
        @return the index
     */
    public int getDescriptorIndex() {
        return descriptorIndex;
    }

    /**
        Set the index of the constant pool entry containing the descriptor of this entry.
        @param descriptorIndex the index
     */
    public void setDescriptorIndex(int descriptorIndex) {
        this.descriptorIndex = descriptorIndex;
    }

    /**
        Get the name.
        @return the name.
        @throws InvalidByteCodeException
     */
    public String getName() throws InvalidByteCodeException {
        return classFile.getConstantPoolEntryName(nameIndex);
    }

    /**
        Get the descriptor string.
        @return the string.
        @throws InvalidByteCodeException
     */
    public String getDescriptor() throws InvalidByteCodeException {
        return classFile.getConstantPoolEntryName(descriptorIndex);
    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {
            
        nameIndex = in.readUnsignedShort();
        descriptorIndex = in.readUnsignedShort();
        
        if (debug) debug("read ");
    }
    
    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {

        out.writeByte(CONSTANT_NAME_AND_TYPE);
        out.writeShort(nameIndex);
        out.writeShort(descriptorIndex);
        if (debug) debug("wrote ");
    }

    protected void debug(String message) {
        super.debug(message + getTagVerbose() + " with name_index " + nameIndex + 
              " and descriptor_index " + descriptorIndex);
    }

    public boolean equals(Object object) {
        if (!(object instanceof ConstantNameAndTypeInfo)) {
            return false;
        }
        ConstantNameAndTypeInfo constantNameAndTypeInfo = (ConstantNameAndTypeInfo)object;
        return super.equals(object) &&
               constantNameAndTypeInfo.nameIndex == nameIndex &&
               constantNameAndTypeInfo.descriptorIndex == descriptorIndex;
    }

    public int hashCode() {
        return super.hashCode() ^ nameIndex ^ descriptorIndex;
    }
    
}
