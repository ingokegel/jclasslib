/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Common class for runtime parameter annotations.
 */
public class RuntimeParameterAnnotationsAttribute extends AttributeInfo {
    private static final int INITIAL_LENGTH = 1;

    private ParameterAnnotations[] parameterAnnotations;


    /**
     * Get the list of parameter annotations of the parent
     * structure as an array of <tt>ParameterAnnotations</tt> structures.
     *
     * @return the array
     */
    public ParameterAnnotations[] getParameterAnnotations() {
        return parameterAnnotations;
    }

    /**
     * Set the list of parameter annotations associations of the parent
     * structure as an array of <tt>ParameterAnnotations</tt> structures.
     *
     * @param parameterAnnotations the array
     */
    public void setParameterAnnotations(ParameterAnnotations[] parameterAnnotations) {
        this.parameterAnnotations = parameterAnnotations;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {

        int numParameters = in.readUnsignedByte();
        parameterAnnotations = new ParameterAnnotations[numParameters];

        for (int i = 0; i < numParameters; i++) {
            parameterAnnotations[i] = new ParameterAnnotations();
            parameterAnnotations[i].read(in);
        }

        if (debug) {
            debug("read ");
        }
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);

        int parameterAnnotationsLength = getLength(parameterAnnotations);
        out.writeByte(parameterAnnotationsLength);
        for (int i = 0; i < parameterAnnotationsLength; i++) {
            parameterAnnotations[i].write(out);
        }

        if (debug) {
            debug("wrote ");
        }
    }

    public int getAttributeLength() {
        int length = INITIAL_LENGTH;
        for (ParameterAnnotations parameterAnnotations : this.parameterAnnotations) {
            length += parameterAnnotations.getLength();
        }
        return length;
    }
}
