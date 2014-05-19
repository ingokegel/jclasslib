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
public class MethodParametersEntry extends AbstractStructure {


    private int nameIndex;
    private int accessFlags;


    /**
     * Factory method for creating <tt>StackMapFrameEntry</tt> structures.
     *
     * @param in        the <tt>DataInput</tt> from which to read the
     *                  <tt>StackMapFrameEntry</tt> structure
     * @param classFile the parent class file of the structure to be created
     * @return the new <tt>StackMapFrameEntry</tt> structure
     * @throws InvalidByteCodeException if the byte code is invalid
     * @throws IOException              if an exception occurs with the <tt>DataInput</tt>
     */
    public static MethodParametersEntry create(DataInput in, ClassFile classFile)
        throws InvalidByteCodeException, IOException {

        MethodParametersEntry bootStrapMethodsEntry = new MethodParametersEntry();
        bootStrapMethodsEntry.setClassFile(classFile);
        bootStrapMethodsEntry.read(in);

        return bootStrapMethodsEntry;
    }


    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {

        nameIndex = in.readUnsignedShort();
        accessFlags = in.readUnsignedShort();

        if (debug) {
            debug("read ");
        }
    }


    public int getNameIndex() {
        return nameIndex;
    }

    public int getAccessFlags() {
        return accessFlags;
    }

    public void write(DataOutput out) throws InvalidByteCodeException,
        IOException {

        super.write(out);
        out.writeShort(nameIndex);
        out.writeShort(accessFlags);

        if (debug) {
            debug("wrote ");
        }
    }


    protected void debug(String message) {
        super.debug(message + "MethodParams entry");
    }


    public int getLength() {
        return 4;
    }

    protected String printAccessFlagsVerbose(int accessFlags) {
        return "";
    }

}
