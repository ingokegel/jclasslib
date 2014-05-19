/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class UninitializedVerificationTypeEntry extends VerificationTypeInfoEntry {

    private int cpIndex;

    public int getCpIndex() {
        return cpIndex;
    }

    public void setCpIndex(int cpIndex) {
        this.cpIndex = cpIndex;
    }


    public void read(DataInput in) throws InvalidByteCodeException, IOException {
        super.read(in);
        cpIndex = in.readUnsignedShort();
    }

    @Override
    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);
        out.writeShort(cpIndex);
    }
}
