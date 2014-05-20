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
 * Target info for a <tt>TypeAnnotation</tt> structure with local variable table links.
 */
public class LocalVarTargetInfo extends TargetInfo {

    private LocalVarTarget[] localVarTargets;

    public LocalVarTarget[] getLocalVarTargets() {
        return localVarTargets;
    }

    public void setLocalVarTargets(LocalVarTarget[] localVarTargets) {
        this.localVarTargets = localVarTargets;
    }

    @Override
    public void read(DataInput in) throws InvalidByteCodeException, IOException {
        super.read(in);
        int count = in.readUnsignedShort();
        localVarTargets = new LocalVarTarget[count];
        for (int i = 0; i < count; i++) {
            localVarTargets[i] = new LocalVarTarget();
            localVarTargets[i].read(in);
        }
    }

    @Override
    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);
        int count = getLength(localVarTargets);
        out.writeShort(count);
        for (int i = 0; i < count; i++) {
            localVarTargets[i].write(out);
        }
    }

    @Override
    public int getLength() {
        return 2 + localVarTargets.length * 6;
    }

    @Override
    public String getVerbose() {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < localVarTargets.length; i++) {
            LocalVarTarget target = localVarTargets[i];
            buffer.append("[").append(i).append("] start: ").append(target.startPc);
            buffer.append(", length: ").append(target.length);
            buffer.append(", <a href=\"L").append(target.index).append("\">local variable with index ").append(target.index).append("</a>");
            buffer.append("\n");
        }
        return buffer.toString();
    }
}
