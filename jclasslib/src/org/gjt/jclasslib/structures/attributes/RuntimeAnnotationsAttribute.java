/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.elementvalues.AnnotationElementValue;

import java.io.*;

/**
 * Common class for runtime annotations.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 * @version $Revision: 1.1 $ $Date: 2004-12-28 13:04:32 $
 */
public class RuntimeAnnotationsAttribute extends AttributeInfo {
    private static final int INITIAL_LENGTH = 2;

    protected AnnotationElementValue[] runtimeAnnotations;


    /**
     * Get the list of runtime annotations associations of the parent
     * structure as an array of <tt>Annotation</tt> structures.
     *
     * @return the array
     */
    public AnnotationElementValue[] getRuntimeAnnotations() {
        return runtimeAnnotations;
    }

    /**
     * Set the list of runtime annotations associations of the parent
     * structure as an array of <tt>Annotation</tt> structures.
     *
     * @param runtimeAnnotations the array
     */
    public void setRuntimeAnnotations(AnnotationElementValue[] runtimeAnnotations) {
        this.runtimeAnnotations = runtimeAnnotations;
    }

    public void read(DataInput in)
            throws InvalidByteCodeException, IOException {

        super.read(in);

        int runtimeVisibleAnnotationsLength = in.readUnsignedShort();
        runtimeAnnotations = new AnnotationElementValue[runtimeVisibleAnnotationsLength];
        for (int i = 0; i < runtimeVisibleAnnotationsLength; i++) {
            runtimeAnnotations[i] = new AnnotationElementValue();
            runtimeAnnotations[i].setClassFile(classFile);
            runtimeAnnotations[i].read(in);
        }

        if (debug) debug("read ");
    }

    public void write(DataOutput out)
            throws InvalidByteCodeException, IOException {

        super.write(out);

        int runtimeVisibleAnnotationsLength = getLength(runtimeAnnotations);

        out.writeShort(runtimeVisibleAnnotationsLength);
        for (int i = 0; i < runtimeVisibleAnnotationsLength; i++) {
            runtimeAnnotations[i].write(out);
        }

        if (debug) debug("wrote ");
    }

    public int getAttributeLength() {
        int length = INITIAL_LENGTH;
        for (int i = 0; i < runtimeAnnotations.length; i++) {
            length += runtimeAnnotations[i].getLength();
        }
        return length;
    }
}
