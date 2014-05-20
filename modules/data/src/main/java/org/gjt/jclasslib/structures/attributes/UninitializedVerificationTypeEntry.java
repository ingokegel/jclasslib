/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

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


/**
 * Describes an entry of type <tt>VerificationType.UNINITIALIZED</tt> in a <tt>BootstrapMethods</tt> attribute structure.
 */
public class UninitializedVerificationTypeEntry extends VerificationTypeInfoEntry {

    public UninitializedVerificationTypeEntry() {
        super(VerificationType.UNINITIALIZED);
    }

    private int offset;

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    @Override
    protected void readExtra(DataInput in) throws InvalidByteCodeException, IOException {
        super.readExtra(in);
        offset = in.readUnsignedShort();
    }

    @Override
    public void writeExtra(DataOutput out) throws InvalidByteCodeException, IOException {
        super.writeExtra(out);
        out.writeShort(offset);
    }

    @Override
    public void appendTo(StringBuilder buffer) {
        super.appendTo(buffer);
        buffer.append(" (offset: ").append(offset).append(")");
    }

    @Override
    public int getLength() {
        return super.getLength() + 2;
    }
}
