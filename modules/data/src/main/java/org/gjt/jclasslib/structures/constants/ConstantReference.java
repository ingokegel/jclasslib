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
    Base class for constant pool data structures which reference class members.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public abstract class ConstantReference extends CPInfo {

    /** Length of the constant pool data structure in bytes. */
    public static final int SIZE = 4;

    /** <tt>class_index</tt> field. */
    protected int classIndex;
    /** <tt>name_and_type_index</tt> field. */
    protected int nameAndTypeIndex;
    
    public String getVerbose() throws InvalidByteCodeException {

        ConstantNameAndTypeInfo nameAndType = getNameAndTypeInfo();

        return classFile.getConstantPoolEntryName(classIndex) + "." +
               classFile.getConstantPoolEntryName(nameAndType.getNameIndex());
    }

    /**
        Get the index of the constant pool entry containing the
        <tt>CONSTANT_Class_info</tt> of this entry.
        @return the index
     */
    public int getClassIndex() {
        return classIndex;
    }
    
    /**
        Set the index of the constant pool entry containing the
        <tt>CONSTANT_Class_info</tt> of this entry.
        @param classIndex the index
     */
    public void setClassIndex(int classIndex) {
        this.classIndex = classIndex;
    }
    
    /**
        Get the index of the constant pool entry containing the
         <tt>CONSTANT_NameAndType_info</tt> of this entry.
        @return the index
     */
    public int getNameAndTypeIndex() {
        return nameAndTypeIndex;
    }

    /**
        Set the index of the constant pool entry containing the
         <tt>CONSTANT_NameAndType_info</tt> of this entry.
        @param nameAndTypeIndex the index
     */
    public void setNameAndTypeIndex(int nameAndTypeIndex) {
        this.nameAndTypeIndex = nameAndTypeIndex;
    }

    /**
        Get the class info for this reference.
        @return the class info.
        @throws InvalidByteCodeException
     */
    public ConstantClassInfo getClassInfo() throws InvalidByteCodeException {
        return (ConstantClassInfo)getClassFile().getConstantPoolEntry(classIndex, ConstantClassInfo.class);
    }

    /**
        Get the name and type info for this reference.
        @return the name and type info.
        @throws InvalidByteCodeException
     */
    public ConstantNameAndTypeInfo getNameAndTypeInfo() throws InvalidByteCodeException {
        return (ConstantNameAndTypeInfo)classFile.getConstantPoolEntry(
                    nameAndTypeIndex,
                    ConstantNameAndTypeInfo.class);
    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {
            
        classIndex = in.readUnsignedShort();
        nameAndTypeIndex = in.readUnsignedShort();
    }
    
    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {
        
        out.writeShort(classIndex);
        out.writeShort(nameAndTypeIndex);
    }
    
    public boolean equals(Object object) {
        if (!(object instanceof ConstantReference)) {
            return false;
        }
        ConstantReference constantReference = (ConstantReference)object;
        return super.equals(object) &&
               constantReference.classIndex == classIndex &&
               constantReference.nameAndTypeIndex == nameAndTypeIndex;
    }

    public int hashCode() {
        return super.hashCode() ^ classIndex ^ nameAndTypeIndex;
    }

}
