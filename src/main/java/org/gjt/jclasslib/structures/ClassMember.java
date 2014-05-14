/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures;

import org.gjt.jclasslib.structures.constants.ConstantUtf8Info;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
    Base class for class members.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public abstract class ClassMember extends AbstractStructureWithAttributes {

    /** The access flags of this class member. */
    protected int accessFlags;
    /** the constant pool index of the name of this class member. */
    protected int nameIndex;
    /** the constant pool index of the descriptor of this class member. */
    protected int descriptorIndex;

    /**
        Get the access flags of this class member.
        @return the access flags
     */
    public int getAccessFlags() {
        return accessFlags;
    }

    /**
        Set the access flags of this class member.
        @param accessFlags the access flags
     */
    public void setAccessFlags(int accessFlags) {
        this.accessFlags = accessFlags;
    }

    /**
        Get the constant pool index of the name of this class member.
        @return the index
     */
    public int getNameIndex() {
        return nameIndex;
    }

    /**
        Set the constant pool index of the name of this class member.
        @param nameIndex the index
     */
    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    /**
        Get the constant pool index of the descriptor of this class member.
        @return the index
     */
    public int getDescriptorIndex() {
        return descriptorIndex;
    }

    /**
        Set the constant pool index of the descriptor of this class member.
        @param descriptorIndex the index
     */
    public void setDescriptorIndex(int descriptorIndex) {
        this.descriptorIndex = descriptorIndex;
    }

    /**
        Get the name of the class member.
        @return the name
        @throws InvalidByteCodeException if the entry is invalid
     */
    public String getName() throws InvalidByteCodeException {
        ConstantUtf8Info cpinfo = classFile.getConstantPoolUtf8Entry(nameIndex);
        if (cpinfo == null) {
            return "invalid constant pool index";
        } else {
            return cpinfo.getString();
        }
    }

    /**
        Get the verbose descriptor of the class member.
        @return the descriptor
        @throws InvalidByteCodeException if the entry is invalid
     */
    public String getDescriptor() throws InvalidByteCodeException {
        ConstantUtf8Info cpinfo = classFile.getConstantPoolUtf8Entry(descriptorIndex);
        if (cpinfo == null) {
            return "invalid constant pool index";
        } else {
            return cpinfo.getString();
        }
    }

    /**
        Get the the access flags of this class as a hex string.
        @return the hex string
     */
    public String getFormattedAccessFlags() {
        return printAccessFlags(accessFlags);
    }

    /**
        Get the verbose description of the access flags of this class.
        @return the description
     */
    public String getAccessFlagsVerbose() {
        return printAccessFlagsVerbose(accessFlags);
    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {

        accessFlags = in.readUnsignedShort();
        nameIndex = in.readUnsignedShort();
        descriptorIndex = in.readUnsignedShort();

        readAttributes(in);

    }

    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {

        out.writeShort(accessFlags);
        out.writeShort(nameIndex);
        out.writeShort(descriptorIndex);

        writeAttributes(out);
    }

}
