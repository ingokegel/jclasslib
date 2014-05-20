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
import org.gjt.jclasslib.structures.attributes.targettype.TargetInfo;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Describes an entry in a <tt>RuntimeVisibleTypeAnnotations</tt> or <tt>RuntimeInvisibleTypeAnnotations</tt>
 * attribute structure.
 */
public class TypeAnnotation extends AbstractStructure {

    private static final int INITIAL_LENGTH = 2;

    private TypeAnnotationTargetType targetType;
    private TargetInfo targetInfo;
    private TypePathEntry typePathEntries[];
    private Annotation annotation;

    public TypeAnnotationTargetType getTargetType() {
        return targetType;
    }

    public void setTargetType(TypeAnnotationTargetType targetType) {
        this.targetType = targetType;
    }

    public TargetInfo getTargetInfo() {
        return targetInfo;
    }

    public void setTargetInfo(TargetInfo targetInfo) {
        this.targetInfo = targetInfo;
    }

    public TypePathEntry[] getTypePathEntries() {
        return typePathEntries;
    }

    public void setTypePathEntries(TypePathEntry[] typePathEntries) {
        this.typePathEntries = typePathEntries;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {
        super.read(in);

        targetType = TypeAnnotationTargetType.getFromTag(in.readUnsignedByte());
        targetInfo = targetType.createTargetInfo();
        targetInfo.read(in);

        int typePathLength = in.readUnsignedByte();
        typePathEntries = new TypePathEntry[typePathLength];
        for (int i = 0; i < typePathLength; i++) {
            typePathEntries[i] = new TypePathEntry();
            typePathEntries[i].read(in);
        }
        annotation = new Annotation();
        annotation.read(in);
        if (debug) {
            debug("read ");
        }
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);

        out.writeByte(targetType.getTag());
        targetInfo.write(out);
        int typePathLength = getLength(typePathEntries);
        out.writeByte(typePathLength);
        for (int i = 0; i < typePathLength; i++) {
            typePathEntries[i].write(out);
        }
        annotation.write(out);
        if (debug) {
            debug("wrote ");
        }
    }

    protected void debug(String message) {
        super.debug(message + "TypeAnnotation entry");
    }

    public int getLength() {
        return INITIAL_LENGTH +
            targetInfo.getLength() +
            typePathEntries.length * 2 +
            annotation.getLength();
    }

    protected String printAccessFlagsVerbose(int accessFlags) {
        return "";
    }

}
