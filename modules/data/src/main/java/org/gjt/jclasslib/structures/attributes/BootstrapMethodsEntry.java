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
public class BootstrapMethodsEntry extends AbstractStructure {

    private int methodRefIndex;
    private int argumentIndices[];

    /**
     * Factory method for creating <tt>BootstrapMethodsEntry</tt> structures.
     *
     * @param in        the <tt>DataInput</tt> from which to read the
     *                  <tt>BootstrapMethodsEntry</tt> structure
     * @param classFile the parent class file of the structure to be created
     * @return the new <tt>BootstrapMethodsEntry</tt> structure
     * @throws InvalidByteCodeException if the byte code is invalid
     * @throws IOException              if an exception occurs with the <tt>DataInput</tt>
     */
    public static BootstrapMethodsEntry create(DataInput in, ClassFile classFile)
        throws InvalidByteCodeException, IOException {

        BootstrapMethodsEntry bootStrapMethodsEntry = new BootstrapMethodsEntry();
        bootStrapMethodsEntry.setClassFile(classFile);
        bootStrapMethodsEntry.read(in);

        return bootStrapMethodsEntry;
    }

    /**
     * Get the constant pool index of the <tt>CONSTANT_MethodRef_info</tt> structure
     * describing the bootstrap method of this <tt>BootstrapMethodsEntry</tt>.
     *
     * @return the index
     */
    public int getMethodRefIndex() {
        return methodRefIndex;
    }

    /**
     * Set the constant pool index of the <tt>CONSTANT_MethodRef_info</tt> structure
     * describing the bootstrap method of this <tt>BootstrapMethodsEntry</tt>.
     *
     * @param methodRefIndex the index
     */
    public void setMethodRefIndex(int methodRefIndex) {
        this.methodRefIndex = methodRefIndex;
    }


    /**
     * Get the array of argument references of this  <tt>BootstrapMethodsEntry</tt>.
     *
     * @return the argument references
     */
    public int[] getArgumentIndices() {
        return argumentIndices;
    }

    /**
     * Set the array of argument references of this  <tt>BootstrapMethodsEntry</tt>.
     *
     * @param argumentIndices the argument references
     */
    public void setArgumentIndices(int argumentIndices[]) {
        this.argumentIndices = argumentIndices;
    }


    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {

        methodRefIndex = in.readUnsignedShort();
        int argumentRefsCount = in.readUnsignedShort();
        argumentIndices = new int[argumentRefsCount];
        for (int i = 0; i < argumentRefsCount; i++) {
            argumentIndices[i] = in.readUnsignedShort();
        }
        if (debug) {
            debug("read ");
        }
    }

    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {

        super.write(out);
        out.writeShort(methodRefIndex);
        int argumentRefsCount = getLength(argumentIndices);
        out.writeShort(argumentRefsCount);
        for (int i = 0; i < argumentRefsCount; i++) {
            out.writeShort(argumentIndices[i]);
        }
        if (debug) {
            debug("wrote ");
        }
    }

    protected void debug(String message) {
        super.debug(message + "BootstrapMethods entry with bootstrap_method_index " + methodRefIndex +
            ", arguments (" + getVerbose() + ")");
    }

    public String getVerbose() {
        StringBuilder buffer = new StringBuilder();
        int argumentRefsCount = getLength(argumentIndices);
        for (int i = 0; i < argumentRefsCount; i++) {
            if (i > 0) {
                buffer.append("\n");
            }
            int argumentIndex = argumentIndices[i];
            buffer.append("<a href=\"").append(argumentIndex).append("\">cp_info #").append(argumentIndex).append("</a> &lt;").append(getVerboseIndex(argumentIndex)).append("&gt;");
        }
        return buffer.toString();
    }

    private String getVerboseIndex(int index)  {
        try {
            return getClassFile().getConstantPoolEntryName(index);
        } catch (InvalidByteCodeException e) {
            return "invalid constant pool index " + index;
        }
    }

    public int getLength() {
        return 4 + getLength(argumentIndices) * 2;
    }

    @Override
    protected String printAccessFlagsVerbose(int accessFlags) {
        return null;
    }


}
