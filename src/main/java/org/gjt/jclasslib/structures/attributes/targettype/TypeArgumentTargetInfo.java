/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes.targettype;

import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Target info for a <tt>TypeAnnotation</tt> structure with an offset and an argument index.
 */
public class TypeArgumentTargetInfo extends TargetInfo {
    private int offset;
    private int typeArgumentIndex;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getTypeArgumentIndex() {
        return typeArgumentIndex;
    }

    public void setTypeArgumentIndex(int typeArgumentIndex) {
        this.typeArgumentIndex = typeArgumentIndex;
    }

    @Override
    public void read(DataInput in) throws InvalidByteCodeException, IOException {
        super.read(in);
        offset = in.readUnsignedShort();
        typeArgumentIndex = in.readByte();
    }

    @Override
    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);
        out.writeShort(offset);
        out.writeByte(typeArgumentIndex);
    }

    @Override
    public int getLength() {
        return 3;
    }

    @Override
    public String getVerbose() {
        return "offset " + offset + ", type argument index " + typeArgumentIndex;
    }
}
