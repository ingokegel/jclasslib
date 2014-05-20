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
 * Describes an exception table entry in a <tt>Code</tt> attribute structure.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>, <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class ExceptionTableEntry extends AbstractStructure {

    /**
     * Length in bytes of an exception table entry.
     */
    public static final int LENGTH = 8;

    private int startPc;
    private int endPc;
    private int handlerPc;
    private int catchType;

    /**
     * Factory method for creating <tt>ExceptionTableEntry</tt> structures.
     *
     * @param in        the <tt>DataInput</tt> from which to read the
     *                  <tt>ExceptionTableEntry</tt> structure
     * @param classFile the parent class file of the structure to be created
     * @return the new <tt>ExceptionTableEntry</tt> structure
     * @throws InvalidByteCodeException if the byte code is invalid
     * @throws IOException              if an exception occurs with the <tt>DataInput</tt>
     */
    public static ExceptionTableEntry create(DataInput in, ClassFile classFile)
            throws InvalidByteCodeException, IOException {

        ExceptionTableEntry exceptionTableEntry = new ExceptionTableEntry();
        exceptionTableEntry.setClassFile(classFile);
        exceptionTableEntry.read(in);

        return exceptionTableEntry;
    }

    /**
     * Constructor.
     */
    public ExceptionTableEntry() {
    }

    /**
     * Constructor.
     *
     * @param startPc   the <tt>start_pc</tt>
     * @param endPc     the <tt>end_pc</tt>
     * @param handlerPc the <tt>handler_pc</tt>
     * @param catchType the constant pool index for the catch type of this exception table entry
     */
    public ExceptionTableEntry(int startPc, int endPc, int handlerPc, int catchType) {
        this.startPc = startPc;
        this.endPc = endPc;
        this.handlerPc = handlerPc;
        this.catchType = catchType;
    }

    /**
     * Get the <tt>start_pc</tt> of this exception table entry.
     *
     * @return the <tt>start_pc</tt>
     */
    public int getStartPc() {
        return startPc;
    }

    /**
     * Set the <tt>start_pc</tt> of this exception table entry.
     *
     * @param startPc the <tt>start_pc</tt>
     */
    public void setStartPc(int startPc) {
        this.startPc = startPc;
    }

    /**
     * Get the <tt>end_pc</tt> of this exception table entry.
     *
     * @return the <tt>end_pc</tt>
     */
    public int getEndPc() {
        return endPc;
    }

    /**
     * Set the <tt>end_pc</tt> of this exception table entry.
     *
     * @param endPc the <tt>end_pc</tt>
     */
    public void setEndPc(int endPc) {
        this.endPc = endPc;
    }

    /**
     * Get the <tt>handler_pc</tt> of this exception table entry.
     *
     * @return the <tt>handler_pc</tt>
     */
    public int getHandlerPc() {
        return handlerPc;
    }

    /**
     * Set the <tt>handler_pc</tt> of this exception table entry.
     *
     * @param handlerPc the <tt>handler_pc</tt>
     */
    public void setHandlerPc(int handlerPc) {
        this.handlerPc = handlerPc;
    }

    /**
     * Get the constant pool index for the catch type of this exception table entry.
     *
     * @return the index
     */
    public int getCatchType() {
        return catchType;
    }

    /**
     * Set the constant pool index for the catch type of this exception table entry.
     *
     * @param catchType the index
     */
    public void setCatchType(int catchType) {
        this.catchType = catchType;
    }

    public void read(DataInput in)
            throws InvalidByteCodeException, IOException {

        startPc = in.readUnsignedShort();
        endPc = in.readUnsignedShort();
        handlerPc = in.readUnsignedShort();
        catchType = in.readUnsignedShort();
        if (debug) debug("read ");
    }

    public void write(DataOutput out)
            throws InvalidByteCodeException, IOException {

        super.write(out);
        out.writeShort(startPc);
        out.writeShort(endPc);
        out.writeShort(handlerPc);
        out.writeShort(catchType);
        if (debug) debug("wrote ");
    }

    protected void debug(String message) {
        super.debug(message + "exception table entry with start_pc " + startPc +
                ", end_pc " + endPc + ", handler_pc " + handlerPc +
                ", catch_type index " + catchType);
    }

    protected String printAccessFlagsVerbose(int accessFlags) {
        if (accessFlags != 0)
            throw new RuntimeException("Access flags should be zero: " + Integer.toHexString(accessFlags));
        return "";
    }

}
