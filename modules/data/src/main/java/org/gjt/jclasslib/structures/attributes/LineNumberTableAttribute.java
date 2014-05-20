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
    Describes an <tt>LineNumberTable</tt> attribute structure.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class LineNumberTableAttribute extends AttributeInfo {

    /** Name of the attribute as in the corresponding constant pool entry. */
    public static final String ATTRIBUTE_NAME = "LineNumberTable";

    private static final int INITIAL_LENGTH = 2;
    
    private LineNumberTableEntry[] lineNumberTable;
    
    /**
        Get the list of line number associations of the parent
        <tt>Code</tt> structure as an array of <tt>LineNumberTableEntry</tt> structures.
        @return the array
     */
    public LineNumberTableEntry[] getLineNumberTable() {
        return lineNumberTable;
    }
    
    /**
        Set the list of line number associations of the parent
        <tt>Code</tt> structure as an array of <tt>LineNumberTableEntry</tt> structures.
        @param lineNumberTable the index
     */
    public void setLineNumberTable(LineNumberTableEntry[] lineNumberTable) {
        this.lineNumberTable = lineNumberTable;
    }
    
    public void read(DataInput in) throws InvalidByteCodeException, IOException {
            
        int lineNumberTableLength = in.readUnsignedShort();
        lineNumberTable = new LineNumberTableEntry[lineNumberTableLength];
        for (int i = 0 ; i < lineNumberTableLength; i++) {
            lineNumberTable[i] = LineNumberTableEntry.create(in, classFile);
        }
        
        if (debug) debug("read ");
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);

        int lineNumberTableLength = getLength(lineNumberTable);
        
        out.writeShort(lineNumberTableLength);
        for (int i = 0 ; i < lineNumberTableLength; i++) {
            lineNumberTable[i].write(out);
        }
        if (debug) debug("wrote ");
    }

    public int getAttributeLength() {
        return INITIAL_LENGTH + getLength(lineNumberTable) * LineNumberTableEntry.LENGTH;
    }

    protected void debug(String message) {
        super.debug(message + "LineNumberTable attribute with " + getLength(lineNumberTable) + " entries");
    }

}
