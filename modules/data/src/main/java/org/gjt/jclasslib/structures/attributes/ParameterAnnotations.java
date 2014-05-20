/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.AbstractStructure;
import org.gjt.jclasslib.structures.Annotation;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Common class for runtime parameter annotations.
 */
public class ParameterAnnotations extends AbstractStructure {

    private static final int INITIAL_LENGTH = 2;

    protected Annotation[] runtimeAnnotations;


    /**
     * Get the list of runtime annotations associations of the parent
     * structure as an array of <tt>Annotation</tt> structures.
     *
     * @return the array
     */
    public Annotation[] getRuntimeAnnotations() {
        return runtimeAnnotations;
    }

    /**
     * Set the list of runtime annotations associations of the parent
     * structure as an array of <tt>Annotation</tt> structures.
     *
     * @param runtimeAnnotations the array
     */
    public void setRuntimeAnnotations(Annotation[] runtimeAnnotations) {
        this.runtimeAnnotations = runtimeAnnotations;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {

        int runtimeVisibleAnnotationsLength = in.readUnsignedShort();

        runtimeAnnotations = new Annotation[runtimeVisibleAnnotationsLength];
        for (int i = 0; i < runtimeVisibleAnnotationsLength; i++) {
            runtimeAnnotations[i] = new Annotation();
            runtimeAnnotations[i].setClassFile(classFile);
            runtimeAnnotations[i].read(in);
        }

        if (debug) {
            debug("read ");
        }
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);

        int runtimeVisibleAnnotationsLength = getLength(runtimeAnnotations);

        out.writeShort(runtimeVisibleAnnotationsLength);
        for (int i = 0; i < runtimeVisibleAnnotationsLength; i++) {
            runtimeAnnotations[i].write(out);
        }

        if (debug) {
            debug("wrote ");
        }
    }

    public int getLength() {
        int length = INITIAL_LENGTH;
        for (Annotation runtimeAnnotation : runtimeAnnotations) {
            length += runtimeAnnotation.getLength();
        }
        return length;
    }

    protected String printAccessFlagsVerbose(int accessFlags) {
        if (accessFlags != 0)
            throw new RuntimeException("Access flags should be zero: " +
                    Integer.toHexString(accessFlags));
        return "";
    }
}
