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
    Describes a <tt>CONSTANT_InvokeDynamic_info</tt> constant pool data structure.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ConstantInvokeDynamicInfo extends CPInfo {

    /** Length of the constant pool data structure in bytes. */
    public static final int SIZE = 4;

    private int bootstrapMethodAttributeIndex;
    private int nameAndTypeIndex;

    public byte getTag() {
        return CONSTANT_INVOKE_DYNAMIC;
    }

    public String getTagVerbose() {
        return CONSTANT_INVOKE_DYNAMIC_VERBOSE;
    }

    public String getVerbose() throws InvalidByteCodeException {
        ConstantNameAndTypeInfo nameAndType = getNameAndTypeInfo();

        return nameAndType.getName() + ", BootstrapMethods #" + bootstrapMethodAttributeIndex;
    }

    public int getBootstrapMethodAttributeIndex() {
        return bootstrapMethodAttributeIndex;
    }

    public void setBootstrapMethodAttributeIndex(int bootstrapMethodAttributeIndex) {
        this.bootstrapMethodAttributeIndex = bootstrapMethodAttributeIndex;
    }

    public int getNameAndTypeIndex() {
        return nameAndTypeIndex;
    }

    public void setNameAndTypeIndex(int nameAndTypeIndex) {
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    public ConstantNameAndTypeInfo getNameAndTypeInfo() throws InvalidByteCodeException {
        return (ConstantNameAndTypeInfo)classFile.getConstantPoolEntry(
                nameAndTypeIndex,
                ConstantNameAndTypeInfo.class);
    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {
            
        bootstrapMethodAttributeIndex = in.readUnsignedShort();
        nameAndTypeIndex = in.readUnsignedShort();
        
        if (debug) debug("read ");
    }
    
    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {

        out.writeByte(CONSTANT_INVOKE_DYNAMIC);
        out.writeShort(bootstrapMethodAttributeIndex);
        out.writeShort(nameAndTypeIndex);
        if (debug) debug("wrote ");
    }

    protected void debug(String message) {
        super.debug(message + getTagVerbose() + " with bootstrap method attr index " + bootstrapMethodAttributeIndex +
              " and name and type index " + nameAndTypeIndex);
    }

    public boolean equals(Object object) {
        if (!(object instanceof ConstantInvokeDynamicInfo)) {
            return false;
        }
        ConstantInvokeDynamicInfo constantNameAndTypeInfo = (ConstantInvokeDynamicInfo)object;
        return super.equals(object) &&
               constantNameAndTypeInfo.bootstrapMethodAttributeIndex == bootstrapMethodAttributeIndex &&
               constantNameAndTypeInfo.nameAndTypeIndex == nameAndTypeIndex;
    }

    public int hashCode() {
        return super.hashCode() ^ bootstrapMethodAttributeIndex ^ nameAndTypeIndex;
    }
    
}
