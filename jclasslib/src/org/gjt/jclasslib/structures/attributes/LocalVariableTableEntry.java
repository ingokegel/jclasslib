/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.*;

import java.io.*;

/**
    Describes an entry in a <tt>LocalVariableTableEntry</tt> attribute structure.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2002-02-27 16:47:43 $
*/
public class LocalVariableTableEntry extends AbstractStructure {

    /** Length in bytes of a local variable association */
    public static final int LENGTH = 10;
                                   
    private int startPc;
    private int length;
    private int nameIndex;
    private int descriptorIndex;
    private int index;
    
    /**
        Factory method for creating <tt>LocalVariableTableEntry</tt> structures.
     
        @param in the <tt>DataInput</tt> from which to read the
                  <tt>LocalVariableTableEntry</tt> structure
        @param classFile the parent class file of the structure to be created
        @return the new <tt>LocalVariableTableEntry</tt> structure
        @throws InvalidByteCodeException if the byte code is invalid
        @throws IOException if an exception occurs with the <tt>DataInput</tt>
    */
    public static LocalVariableTableEntry create(DataInput in, ClassFile classFile)
        throws InvalidByteCodeException, IOException {
    
        LocalVariableTableEntry localVariableTableEntry = new LocalVariableTableEntry();
        localVariableTableEntry.setClassFile(classFile);
        localVariableTableEntry.read(in);

        return localVariableTableEntry;
    }
            
    /**
        Get the <tt>start_pc</tt> of this local variable association.
        @return the <tt>start_pc</tt>
     */
    public int getStartPc() {
        return startPc;
    }
    
    /**
        Set the <tt>start_pc</tt> of this local variable association.
        @param startPc the <tt>start_pc</tt>
     */
    public void setStartPc(int startPc) {
        this.startPc = startPc;
    }

    /**
        Get the length in bytes of this local variable association.
        @return the length
     */
    public int getLength() {
        return length;
    }
    
    /**
        Set the length in bytes of this local variable association.
        @param length the length
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
        Get the index of the constant pool entry containing the name of this
        local variable.
        @return the index
     */
    public int getNameIndex() {
        return nameIndex;
    }
    
    /**
        Set the index of the constant pool entry containing the name of this
        local variable.
        @param nameIndex the index
     */
    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    /**
        Get the index of the constant pool entry containing the descriptor of this
        local variable.
        @return the index
     */
    public int getDescriptorIndex() {
        return descriptorIndex;
    }
    
    /**
        Get the index of the constant pool entry containing the descriptor of this
        local variable.
        @param descriptorIndex the index
     */
    public void setDescriptorIndex(int descriptorIndex) {
        this.descriptorIndex = descriptorIndex;
    }

    /**
        Get the index of this local variable.
        @return the index
     */
    public int getIndex() {
        return index;
    }

    /**
        Set the index of this local variable.
        @param index the index
     */
    public void setIndex(int index) {
        this.index = index;
    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {
            
        startPc = in.readUnsignedShort();
        length = in.readUnsignedShort();
        nameIndex = in.readUnsignedShort();
        descriptorIndex = in.readUnsignedShort();
        index = in.readUnsignedShort();
        if (debug) debug("read ");
    }

    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {
        
        super.write(out);
        out.writeShort(startPc);
        out.writeShort(length);
        out.writeShort(nameIndex);
        out.writeShort(descriptorIndex);
        out.writeShort(index);
        if (debug) debug("wrote ");
    }

    protected void debug(String message) {
        super.debug(message + "LocalVariableTable entry with start_pc " + startPc + 
              ", length " + length + ", name_index " + nameIndex + 
              ", descriptor_index " + descriptorIndex + ", index " + index);
    }

}
