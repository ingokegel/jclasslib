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
    Describes a <tt>SourceFile</tt> attribute structure.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class SourceFileAttribute extends AttributeInfo {

    /** Name of the attribute as in the corresponding constant pool entry. */
    public static final String ATTRIBUTE_NAME = "SourceFile";

    private static final int LENGTH = 2;
    
    private int sourceFileIndex;
    
    /**
        Get the constant pool index of the name of the source file.
        @return the index
     */
    public int getSourceFileIndex() {
        return sourceFileIndex;
    }

    /**
        Set the constant pool index of the name of the source file.
        @param sourceFileIndex the index
     */
    public void setSourceFileIndex(int sourceFileIndex) {
        this.sourceFileIndex = sourceFileIndex;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {
            
        sourceFileIndex = in.readUnsignedShort();
        if (debug) debug("read ");
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);
        out.writeShort(sourceFileIndex);
        if (debug) debug("wrote ");
    }

    public int getAttributeLength() {
        return LENGTH;
    }

    protected void debug(String message) {
        super.debug(message + "SourceFile attribute with sourcefile_index " + sourceFileIndex);
    }

}
