/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.AbstractStructure;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Contains common attributes to a local variable table entry structure.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public abstract class LocalVariableCommonEntry extends AbstractStructure {
    /**
     * Length in bytes of a local variable association.
     */
    public static final int LENGTH = 10;

    protected int startPc;
    protected int length;
    protected int nameIndex;
    protected int descriptorOrSignatureIndex;
    protected int index;

    /**
     * Get the <tt>start_pc</tt> of this local variable association.
     *
     * @return the <tt>start_pc</tt>
     */
    final public int getStartPc() {
        return startPc;
    }

    /**
     * Set the <tt>start_pc</tt> of this local variable association.
     *
     * @param startPc the <tt>start_pc</tt>
     */
    final public void setStartPc(int startPc) {
        this.startPc = startPc;
    }

    /**
     * Get the length in bytes of this local variable association.
     *
     * @return the length
     */
    final public int getLength() {
        return length;
    }

    /**
     * Set the length in bytes of this local variable association.
     *
     * @param length the length
     */
    final public void setLength(int length) {
        this.length = length;
    }

    /**
     * Get the index of the constant pool entry containing the name of this
     * local variable.
     *
     * @return the index
     */
    final public int getNameIndex() {
        return nameIndex;
    }

    /**
     * Set the index of the constant pool entry containing the name of this
     * local variable.
     *
     * @param nameIndex the index
     */
    final public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }

    /**
     * Get the index of the constant pool entry containing the descriptor of this
     * local variable.
     *
     * @return the index
     */
    final public int getDescriptorOrSignatureIndex() {
        return descriptorOrSignatureIndex;
    }

    /**
     * Get the index of the constant pool entry containing the descriptor of this
     * local variable.
     *
     * @param descriptorIndex the index
     */
    final public void setDescriptorOrSignatureIndex(int descriptorIndex) {
        this.descriptorOrSignatureIndex = descriptorIndex;
    }

    /**
     * Get the index of this local variable.
     *
     * @return the index
     */
    final public int getIndex() {
        return index;
    }

    /**
     * Set the index of this local variable.
     * Set the index of this local variable.
     */
    final public void setIndex(int index) {
        this.index = index;
    }

    final public void read(DataInput in)
            throws InvalidByteCodeException, IOException {
        super.read(in);

        startPc = in.readUnsignedShort();
        length = in.readUnsignedShort();
        nameIndex = in.readUnsignedShort();
        descriptorOrSignatureIndex = in.readUnsignedShort();
        index = in.readUnsignedShort();

        if (debug) debug("read ");
    }

    final public void write(DataOutput out)
            throws InvalidByteCodeException, IOException {
        super.write(out);

        out.writeShort(startPc);
        out.writeShort(length);
        out.writeShort(nameIndex);
        out.writeShort(descriptorOrSignatureIndex);
        out.writeShort(index);

        if (debug) debug("wrote ");
    }

    protected String printAccessFlagsVerbose(int accessFlags) {
        if (accessFlags != 0)
            throw new RuntimeException("Access flags should be zero: " +
                    Integer.toHexString(accessFlags));
        return "";
    }
}
