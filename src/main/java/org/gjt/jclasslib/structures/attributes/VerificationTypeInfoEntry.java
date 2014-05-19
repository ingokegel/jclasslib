/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.AbstractStructure;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Describes an entry in a <tt>BootstrapMethods</tt> attribute structure.
 */
public class VerificationTypeInfoEntry extends AbstractStructure {


    /**
     * Factory method for creating <tt>VerificationTypeInfoEntry</tt> structures.
     *
     * @param in        the <tt>DataInput</tt> from which to read the
     *                  <tt>VerificationTypeInfoEntry</tt> structure
     * @param classFile the parent class file of the structure to be created
     * @return the new <tt>VerificationTypeInfoEntry</tt> structure
     * @throws InvalidByteCodeException if the byte code is invalid
     * @throws IOException              if an exception occurs with the <tt>DataInput</tt>
     */
    public static VerificationTypeInfoEntry create(DataInput in, ClassFile classFile) throws InvalidByteCodeException, IOException {

        int tag = in.readUnsignedByte();

        VerificationType verificationType = VerificationType.getFromTag(tag);
        VerificationTypeInfoEntry entry = verificationType.createEntry();
        entry.setClassFile(classFile);
        entry.read(in);

        return entry;
    }

    private VerificationType type;

    public VerificationTypeInfoEntry(VerificationType type) {
        this.type = type;
    }

    /**
     * Returns the verification type
     */
    public VerificationType getType() {
        return type;
    }

    public final void read(DataInput in) throws InvalidByteCodeException, IOException {
        super.read(in);
        readExtra(in);
        if (debug) {
            debug("read ");
        }
    }

    /**
     * Read extra data in derived classes.
     */
    protected void readExtra(DataInput in) throws InvalidByteCodeException, IOException {

    }

    @Override
    public final void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);
        out.writeByte(type.getTag());
        writeExtra(out);
        if (debug) debug("wrote ");
    }

    /**
     * Write extra data in derived classes.
     */
    protected void writeExtra(DataOutput out) throws InvalidByteCodeException, IOException {

    }

    protected void debug(String message) {
        super.debug(message + "VerificationTypeInfo entry of type " + type);
    }


    /**
     * Returns the bytecode length of the entry.
     */
    public int getLength() {
        return 1;
    }

    /**
     * Append the verbose representation to a string builder.
     */
    public void appendTo(StringBuilder buffer) {
        buffer.append(type);
    }

    @Override
    protected String printAccessFlagsVerbose(int accessFlags) {
        return null;
    }


}
