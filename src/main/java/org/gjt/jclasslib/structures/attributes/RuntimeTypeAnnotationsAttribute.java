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

import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Common class for runtime type annotations.
 *
 */
public class RuntimeTypeAnnotationsAttribute extends AttributeInfo implements AnnotationHolder {

    private static final int INITIAL_LENGTH = 2;

    protected TypeAnnotation[] runtimeTypeAnnotations;


    /**
     * Get the list of runtime annotations associations of the parent
     * structure as an array of <tt>Annotation</tt> structures.
     *
     * @return the array
     */
    public TypeAnnotation[] getRuntimeAnnotations() {
        return runtimeTypeAnnotations;
    }

    /**
     * Set the list of runtime annotations associations of the parent
     * structure as an array of <tt>Annotation</tt> structures.
     *
     * @param runtimeAnnotations the array
     */
    public void setRuntimeAnnotations(TypeAnnotation[] runtimeAnnotations) {
        this.runtimeTypeAnnotations = runtimeAnnotations;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {

        int runtimeVisibleAnnotationsLength = in.readUnsignedShort();
        runtimeTypeAnnotations = new TypeAnnotation[runtimeVisibleAnnotationsLength];
        for (int i = 0; i < runtimeVisibleAnnotationsLength; i++) {
            runtimeTypeAnnotations[i] = new TypeAnnotation();
            runtimeTypeAnnotations[i].setClassFile(classFile);
            runtimeTypeAnnotations[i].read(in);
        }

        if (debug) debug("read ");
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);

        int runtimeVisibleAnnotationsLength = getLength(runtimeTypeAnnotations);
        out.writeShort(runtimeVisibleAnnotationsLength);
        for (int i = 0; i < runtimeVisibleAnnotationsLength; i++) {
            runtimeTypeAnnotations[i].write(out);
        }

        if (debug) debug("wrote ");
    }

    public int getAttributeLength() {
        int length = INITIAL_LENGTH;
        for (TypeAnnotation runtimeTypeAnnotation : runtimeTypeAnnotations) {
            length += runtimeTypeAnnotation.getLength();
        }
        return length;
    }
    
	public int getNumberOfAnnotations() {
		return runtimeTypeAnnotations.length;
	}
}
