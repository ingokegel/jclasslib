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
    Base class for constant pool data structures which reference class members.

    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:19 $
*/
public abstract class ConstantReference extends CPInfo {

    /** Length of the constant pool data structure in bytes */
    public static final int SIZE = 4;

    /** <tt>class_index</tt> field */
    protected int classIndex;
    /** <tt>name_and_type_index</tt> field */
    protected int nameAndTypeIndex;
    
    public String getVerbose() throws InvalidByteCodeException {
        ConstantNameAndTypeInfo nameAndType = 
            (ConstantNameAndTypeInfo)classFile.getConstantPoolEntry(nameAndTypeIndex,
                                                                    ConstantNameAndTypeInfo.class);
        
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
    
}
