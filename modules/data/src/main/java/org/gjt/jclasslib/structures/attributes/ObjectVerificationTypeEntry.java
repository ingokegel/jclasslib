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
 * Describes an entry of type <tt>VerificationType.OBJECT</tt> in a <tt>BootstrapMethods</tt> attribute structure.
 */
public class ObjectVerificationTypeEntry extends VerificationTypeInfoEntry {

    private int cpIndex;

    public ObjectVerificationTypeEntry() {
        super(VerificationType.OBJECT);
    }

    public int getCpIndex() {
        return cpIndex;
    }

    public void setCpIndex(int cpIndex) {
        this.cpIndex = cpIndex;
    }

    public void readExtra(DataInput in) throws InvalidByteCodeException, IOException {
        super.readExtra(in);
        cpIndex = in.readUnsignedShort();
    }

    @Override
    public void writeExtra(DataOutput out) throws InvalidByteCodeException, IOException {
        super.writeExtra(out);
        out.writeShort(cpIndex);
    }

    @Override
    public void appendTo(StringBuilder buffer) {
        super.appendTo(buffer);
        buffer.append(" <a href=\"").append(cpIndex).append("\">cp_info #").append(cpIndex).append("</a> &lt;").append(getVerboseIndex()).append("&gt;");
    }

    private String getVerboseIndex()  {
        try {
            return getClassFile().getConstantPoolEntryName(cpIndex);
        } catch (InvalidByteCodeException e) {
            return "invalid constant pool index " + cpIndex;
        }
    }

    @Override
    public int getLength() {
        return super.getLength() + 2;
    }
}
