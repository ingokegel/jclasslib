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
 * Describes a <tt>BootstrapMethods</tt> attribute structure.
 */
public class BootstrapMethodsAttribute extends AttributeInfo {

    /**
     * Name of the attribute as in the corresponding constant pool entry.
     */
    public static final String ATTRIBUTE_NAME = "BootstrapMethods";

    private static final int INITIAL_LENGTH = 2;

    private BootstrapMethodsEntry[] methods;

    /**
     * Get the list of bootstrap method references in the <tt>BootstrapMethodsAttribute</tt> structure
     * as an array of <tt>BootstrapMethodsEntry</tt> structures.
     *
     * @return the array
     */
    public BootstrapMethodsEntry[] getMethods() {
        return methods;
    }

    /**
     * Set the list of bootstrap method references in the <tt>BootstrapMethodsAttribute</tt> structure
     * as an array of <tt>BootstrapMethodsEntry</tt> structures.
     *
     * @param methods the array
     */
    public void setMethods(BootstrapMethodsEntry[] methods) {
        this.methods = methods;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {
        int numberOfRefs = in.readUnsignedShort();
        methods = new BootstrapMethodsEntry[numberOfRefs];

        for (int i = 0; i < numberOfRefs; i++) {
            methods[i] = BootstrapMethodsEntry.create(in, classFile);
        }

        if (debug) {
            debug("read ");
        }
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);

        int numberOfRefs = getLength(methods);

        out.writeShort(numberOfRefs);
        for (int i = 0; i < numberOfRefs; i++) {
            methods[i].write(out);
        }
        if (debug) {
            debug("wrote ");
        }
    }

    public int getAttributeLength() {
        int size = INITIAL_LENGTH;
        for (BootstrapMethodsEntry method : methods) {
            size += method.getLength();
        }
        return size;
    }

    protected void debug(String message) {
        super.debug(message + "BootstrapMethods attribute with " + getLength(methods) + " references");
    }

}
