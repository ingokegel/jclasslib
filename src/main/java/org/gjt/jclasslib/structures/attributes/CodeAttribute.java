/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
    Describes a <tt>Code</tt> attribute structure.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class CodeAttribute extends AttributeInfo {

    /** Name of the attribute as in the corresponding constant pool entry. */
    public static final String ATTRIBUTE_NAME = "Code";

    private static final int INITIAL_LENGTH = 12;
    
    private int maxStack;
    private int maxLocals;
    private byte[] code;
    private ExceptionTableEntry[] exceptionTable;

    /**
        Get the maximum stack depth of this code attribute.
        @return the stack depth
     */
    public int getMaxStack() {
        return maxStack;
    }

    /**
        Set the maximum stack depth of this code attribute.
        @param maxStack the stack depth
     */
    public void setMaxStack(int maxStack) {
        this.maxStack = maxStack;
    }

    /**
        Get the maximum number of local variables of this code attribute.
        @return the maximum number
     */
    public int getMaxLocals() {
        return maxLocals;
    }

    /**
        Set the maximum number of local variables of this code attribute.
        @param maxLocals the maximum number
     */
    public void setMaxLocals(int maxLocals) {
        this.maxLocals = maxLocals;
    }

    /**
        Get the code of this code attribute as an array of bytes .
        @return the array
     */
    public byte[] getCode() {
        return code;
    }

    /**
        Set the code of this code attribute as an array of bytes .
        @param code the array
     */
    public void setCode(byte[] code) {
        this.code = code;
    }

    /**
        Get the exception table of this code attribute as an array of
        <tt>ExceptionTableEntry</tt> elements.
        @return the array
     */
    public ExceptionTableEntry[] getExceptionTable() {
        return exceptionTable;
    }
    
    /**
        Set the exception table of this code attribute as an array of
        <tt>ExceptionTableEntry</tt> elements.
        @param exceptionTable the array
     */
    public void setExceptionTable(ExceptionTableEntry[] exceptionTable) {
        this.exceptionTable = exceptionTable;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {

        maxStack = in.readUnsignedShort();
        maxLocals = in.readUnsignedShort();
        int codeLength = in.readInt();
        code = new byte[codeLength];
        in.readFully(code);
        
        readExceptionTable(in);
        readAttributes(in);
        if (debug) debug("read ");
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);
        out.writeShort(maxStack);
        out.writeShort(maxLocals);
        out.writeInt(getLength(code));
        out.write(code);
        
        writeExceptionTable(out);
        writeAttributes(out);
      
        if (debug) debug("wrote ");
    }

    private void readExceptionTable(DataInput in)
        throws InvalidByteCodeException, IOException {
            
        int exceptionTableLength = in.readUnsignedShort();
        exceptionTable = new ExceptionTableEntry[exceptionTableLength];
        for (int i = 0; i < exceptionTableLength; i++) {
            exceptionTable[i] = ExceptionTableEntry.create(in, classFile);
        }
    
    }
    
    private void writeExceptionTable(DataOutput out)
        throws InvalidByteCodeException, IOException {
            
        int exceptionTableLength = getLength(exceptionTable);

        out.writeShort(exceptionTableLength);
        for (int i = 0; i < exceptionTableLength; i++) {
            exceptionTable[i].write(out);
        }
    
    }

    public int getAttributeLength() {
        return INITIAL_LENGTH + getLength(code) + 
               getLength(exceptionTable) * ExceptionTableEntry.LENGTH +
               6 * getLength(attributes) + 
               getTotalAttributesLength() ;
    }

    protected void debug(String message) {
        super.debug(message + "Code attribute with max_stack " + maxStack + 
              ", max_locals " + maxLocals + ", code_length " + getLength(code));
    }

}
