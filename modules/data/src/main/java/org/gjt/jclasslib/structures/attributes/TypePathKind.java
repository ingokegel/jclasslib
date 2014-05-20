/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.InvalidByteCodeException;

/**
 * Describes the kind of a type path in a <tt>TypePathEntry</tt> structure.
 */
public enum TypePathKind {
    DEEPER_IN_ARRAY_TYPE(0),
    DEEPER_IN_NESTED_TYPE(1),
    WILDCARD_BOUND(2),
    TYPE_ARGUMENT(3);

    private static TypePathKind[] typePathKinds = new TypePathKind[values().length];
    static {
        for (int i = 0; i < values().length; i++) {
            typePathKinds[i] = values()[i];
        }
    }

    public static TypePathKind getFromTag(int tag) throws InvalidByteCodeException {
        if (tag < 0 || tag >= typePathKinds.length) {
            throw new InvalidByteCodeException("Invalid type path kind: " + tag);
        }
        return typePathKinds[tag];
    }

    private int tag;

    TypePathKind(int tag) {
        this.tag = tag;
    }

    public int getTag() {
        return tag;
    }
}
