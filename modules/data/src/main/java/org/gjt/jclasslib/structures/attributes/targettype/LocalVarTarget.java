/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes.targettype;

import org.gjt.jclasslib.structures.AbstractStructure;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Local variable target for the <tt>LocalVarTargetInfo</tt> structure.
 */
public class LocalVarTarget extends AbstractStructure {

    public int startPc;
    public int length;
    public int index;

    public int getStartPc() {
        return startPc;
    }

    public void setStartPc(int startPc) {
        this.startPc = startPc;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void read(DataInput in) throws InvalidByteCodeException, IOException {
        super.read(in);
        startPc = in.readUnsignedShort();
        length = in.readUnsignedShort();
        index = in.readUnsignedShort();
    }

    @Override
    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);
        out.writeShort(startPc);
        out.writeShort(length);
        out.writeShort(index);
    }

    @Override
    protected String printAccessFlagsVerbose(int accessFlags) {
        return "";
    }

}
