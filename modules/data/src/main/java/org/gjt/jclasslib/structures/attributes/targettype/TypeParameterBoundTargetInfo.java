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
 * Target info for a <tt>TypeAnnotation</tt> structure with a parameter index an a bound index.
 */
public class TypeParameterBoundTargetInfo extends TargetInfo {

    private int typeParameterIndex;
    private int boundIndex;

    public int getTypeParameterIndex() {
        return typeParameterIndex;
    }

    public void setTypeParameterIndex(int typeParameterIndex) {
        this.typeParameterIndex = typeParameterIndex;
    }

    public int getBoundIndex() {
        return boundIndex;
    }

    public void setBoundIndex(int boundIndex) {
        this.boundIndex = boundIndex;
    }

    @Override
    public void read(DataInput in) throws InvalidByteCodeException, IOException {
        super.read(in);
        typeParameterIndex = in.readUnsignedByte();
        boundIndex = in.readUnsignedByte();
    }

    @Override
    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);
        out.writeByte(typeParameterIndex);
        out.writeByte(boundIndex);
    }

    @Override
    public int getLength() {
        return 2;
    }

    @Override
    public String getVerbose() {
        return "parameter index " + typeParameterIndex + ", bound index " + boundIndex;
    }
}
