/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures;

import java.io.*;

/**
    Base class for class members.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2002-02-27 16:47:43 $
*/
public abstract class ClassMember extends AbstractStructureWithAttributes
                                  implements AccessFlags {

    /** The access flags of this class member */
    protected int accessFlags;
    /** the constant pool index of the name of this class member */
    protected int nameIndex;
    /** the constant pool index of the descriptor of this class member */
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
        return classFile.getConstantPoolUtf8Entry(nameIndex).getString();
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
