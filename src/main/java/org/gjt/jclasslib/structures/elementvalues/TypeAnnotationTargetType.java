/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.elementvalues;

import org.gjt.jclasslib.structures.InvalidByteCodeException;

public enum TypeAnnotationTargetType {

    TYPE_PARAMETER_TARGET__CLASS_INTERFACE(0x00),
    TYPE_PARAMETER_TARGET__METHOD(0x01),
    SUPERTYPE_TARGET(0x10),
    TYPE_PARAMETER_BOUND_TARGET__CLASS_INTERFACE(0x11),
    TYPE_PARAMETER_BOUND_TARGET__METHOD(0x12),
    EMPTY_TARGET__FIELD(0x13),
    EMPTY_TARGET__METHODRETURN_OBJECT(0x14),
    EMPTY_TARGET__METHOD_CONSTRUCTOR(0x15),
    METHOD_FORMAL_PARAMETER_TARGET(0x16),
    THROWS_TARGET(0x17),
    LOCALVAR_TARGET__LOCAL(0x40),
    LOCALVAR_TARGET__RESOURCE(0x41),
    CATCH_TARGET(0x42),
    OFFSET_TARGET__INSTANCE(0x43),
    OFFSET_TARGET__NEW(0x44),
    OFFSET_TARGET__METHODREF_NEW(0x45),
    OFFSET_TARGET__METHODREF_IDENTIFIER(0x46),
    TYPE_ARGUMENT_TARGET__CAST(0x47),
    TYPE_ARGUMENT_TARGET__CONSTRUCTOR(0x48),
    TYPE_ARGUMENT_TARGET__GENERIC(0x49),
    TYPE_ARGUMENT_TARGET__METHODREF_NEW(0x4A),
    TYPE_ARGUMENT_TARGET__METHODREF_IDENTIFIER(0x4B);

    static TypeAnnotationTargetType[] targetTypes = new TypeAnnotationTargetType[values()[values().length - 1].getTag() + 1];
    static {
        for (TypeAnnotationTargetType typeAnnotationTargetType : values()) {
            targetTypes[typeAnnotationTargetType.getTag()] = typeAnnotationTargetType;
        }
    }

    public static TypeAnnotationTargetType getFromTag(int tag) throws InvalidByteCodeException {
        if (tag < 0 || tag >= targetTypes.length) {
            throw new InvalidByteCodeException("Invalid type annotation target type: " + tag);
        }
        return targetTypes[tag];
    }

    private int tag;

    TypeAnnotationTargetType(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }
}
