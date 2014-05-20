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
 * Target info for a <tt>TypeAnnotation</tt> structure with a super class target.
 */
public class SupertypeTargetInfo extends TargetInfo {

    private int supertypeIndex;

    public int getSupertypeIndex() {
        return supertypeIndex;
    }

    public void setSupertypeIndex(int supertypeIndex) {
        this.supertypeIndex = supertypeIndex;
    }

    @Override
    public void read(DataInput in) throws InvalidByteCodeException, IOException {
        super.read(in);
        supertypeIndex = in.readUnsignedShort();
    }

    @Override
    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);
        out.writeShort(supertypeIndex);
    }

    @Override
    public int getLength() {
        return 2;
    }

    @Override
    public String getVerbose() {
        if (supertypeIndex == 65535) {
            return "Super class (" + supertypeIndex + ")";
        } else {
            return "<a href=\"I" + supertypeIndex + "\">interface index " + supertypeIndex + "</a>";
        }
    }
}
