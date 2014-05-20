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
 * Target info for a <tt>TypeAnnotation</tt> structure with a parameter target.
 */
public class ParameterTargetInfo extends TargetInfo {

    private int typeParameterIndex;

    public int getTypeParameterIndex() {
        return typeParameterIndex;
    }

    public void setTypeParameterIndex(int typeParameterIndex) {
        this.typeParameterIndex = typeParameterIndex;
    }

    @Override
    public void read(DataInput in) throws InvalidByteCodeException, IOException {
        super.read(in);
        typeParameterIndex = in.readUnsignedByte();
    }

    @Override
    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);
        out.writeByte(typeParameterIndex);
    }

    @Override
    public int getLength() {
        return 1;
    }

    @Override
    public String getVerbose() {
        return "parameter index " + typeParameterIndex;
    }
}
