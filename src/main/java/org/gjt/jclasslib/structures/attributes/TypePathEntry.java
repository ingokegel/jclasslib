/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.AbstractStructure;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class TypePathEntry extends AbstractStructure {

    private int typePathKind;
    private int typeArgumentIndex;

    public int getTypePathKind() {
        return typePathKind;
    }

    public void setTypePathKind(int typePathKind) {
        this.typePathKind = typePathKind;
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
        typePathKind = in.readUnsignedByte();
        typeArgumentIndex = in.readUnsignedByte();
    }

    @Override
    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);
        out.writeByte(typePathKind);
        out.writeByte(typeArgumentIndex);
    }

    @Override
    protected String printAccessFlagsVerbose(int accessFlags) {
        return "";
    }
}
