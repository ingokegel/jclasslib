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
    Describes a <tt>CONSTANT_MethodHandle_info</tt> constant pool data structure.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Hannes Kegel</a>
*/
public class ConstantMethodHandleInfo extends CPInfo {

    /** Length of the constant pool data structure in bytes. */
    public static final int SIZE = 3;

    public static final int TYPE_GET_FIELD = 1;
    public static final int TYPE_GET_STATIC = 2;
    public static final int TYPE_PUT_FIELD = 3;
    public static final int TYPE_PUT_STATIC = 4;
    public static final int TYPE_INVOKE_VIRTUAL = 5;
    public static final int TYPE_INVOKE_STATIC = 6;
    public static final int TYPE_INVOKE_SPECIAL = 7;
    public static final int TYPE_NEW_INVOKE_SPECIAL = 8;
    public static final int TYPE_INVOKE_INTERFACE = 9;

    private int referenceIndex;
    private int type;


    public byte getTag() {
        return CONSTANT_METHOD_HANDLE;
    }

    public String getTagVerbose() {
        return CONSTANT_METHOD_HANDLE_VERBOSE;
    }

    public String getVerbose() throws InvalidByteCodeException {
        return getName();
    }

    /**
        Get the index of the constant pool entry containing the reference.
        @return the index
     */
    public int getReferenceIndex() {
        return referenceIndex;
    }

    /**
        Set the index of the constant pool entry containing the reference.
        @param referenceIndex the index
     */
    public void setReferenceIndex(int referenceIndex) {
        this.referenceIndex = referenceIndex;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTypeVerbose() {
        switch (type) {
            case TYPE_GET_FIELD:
                return "REF_getField";
            case TYPE_GET_STATIC:
                return "REF_getStatic";
            case TYPE_PUT_FIELD:
                return "REF_putField";
            case TYPE_PUT_STATIC:
                return "REF_putStatic";
            case TYPE_INVOKE_VIRTUAL:
                return "REF_invokeVirtual";
            case TYPE_INVOKE_STATIC:
                return "REF_invokeStatic";
            case TYPE_INVOKE_SPECIAL:
                return "REF_invokeSpecial";
            case TYPE_NEW_INVOKE_SPECIAL:
                return "REF_newInvokeSpecial";
            case TYPE_INVOKE_INTERFACE:
                return "REF_invokeInterface";
            default:
                return "unknown value " + type;
        }
    }

    /**
        Get the descriptor.
        @return the descriptor
        @throws org.gjt.jclasslib.structures.InvalidByteCodeException if the byte code is invalid
     */
    public String getName() throws InvalidByteCodeException {
        return getTypeVerbose() + " " +
               classFile.getConstantPoolEntryName(referenceIndex);

    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {

        type = in.readByte();
        referenceIndex = in.readUnsignedShort();
        if (debug) debug("read ");
    }

    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {
        
        out.writeByte(CONSTANT_METHOD_HANDLE);
        out.write(type);
        out.writeShort(referenceIndex);
        if (debug) debug("wrote ");
    }
    
    public boolean equals(Object object) {
        if (!(object instanceof ConstantMethodHandleInfo)) {
            return false;
        }
        ConstantMethodHandleInfo constantMethodHandleInfo = (ConstantMethodHandleInfo)object;
        return super.equals(object) && constantMethodHandleInfo.referenceIndex == referenceIndex && constantMethodHandleInfo.type == type;
    }

    public int hashCode() {
        return super.hashCode() ^ referenceIndex;
    }
    
    protected void debug(String message) {
        super.debug(message + getTagVerbose() + " with reference_index " + referenceIndex + " and type " + type);
    }
    
}
