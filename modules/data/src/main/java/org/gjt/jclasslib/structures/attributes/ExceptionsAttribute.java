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
    Describes an <tt>Exceptions</tt> attribute structure.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ExceptionsAttribute extends AttributeInfo {

    /** Name of the attribute as in the corresponding constant pool entry. */
    public static final String ATTRIBUTE_NAME = "Exceptions";

    private static final int INITIAL_LENGTH = 2;
    
    private int[] exceptionIndexTable;
    
    /**
        Get the list of exceptions thrown by the parent <tt>Code</tt> attribute
        as an array of indices into the constant pool.
        @return the array
     */
    public int[] getExceptionIndexTable() {
        return exceptionIndexTable;
    }

    /**
        Set the list of exceptions thrown by the parent <tt>Code</tt> attribute
        as an array of indices into the constant pool.
        @param exceptionIndexTable the array
     */
    public void setExceptionIndexTable(int[] exceptionIndexTable) {
        this.exceptionIndexTable = exceptionIndexTable;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {
            
        int numberOfExceptions = in.readUnsignedShort();
        exceptionIndexTable = new int[numberOfExceptions];
        for (int i = 0 ; i < numberOfExceptions; i++) {
            exceptionIndexTable[i] = in.readUnsignedShort();
        }
        
        if (debug) debug("read ");
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);

        int numberOfExceptions = getLength(exceptionIndexTable);

        out.writeShort(numberOfExceptions);
        for (int i = 0 ; i < numberOfExceptions; i++) {
            out.writeShort(exceptionIndexTable[i]);
        }
        if (debug) debug("wrote ");
    }

    public int getAttributeLength() {
        return INITIAL_LENGTH + 2 * getLength(exceptionIndexTable);
    }

    protected void debug(String message) {
        super.debug(message + "Exception attribute with " + getLength(exceptionIndexTable) + " exceptions");
    }

}
