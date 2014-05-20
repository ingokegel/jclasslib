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
 * Describes an entry in a <tt>LineNumberTable</tt> attribute structure.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>, <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class LineNumberTableEntry extends AbstractStructure {

    /**
     * Length in bytes of a line number association.
     */
    public static final int LENGTH = 4;

    private int startPc;
    private int lineNumber;

    /**
     * Factory method for creating <tt>LineNumberTableEntry</tt> structures.
     *
     * @param in        the <tt>DataInput</tt> from which to read the
     *                  <tt>LineNumberTableEntry</tt> structure
     * @param classFile the parent class file of the structure to be created
     * @return the new <tt>LineNumberTableEntry</tt> structure
     * @throws InvalidByteCodeException if the byte code is invalid
     * @throws IOException              if an exception occurs with the <tt>DataInput</tt>
     */
    public static LineNumberTableEntry create(DataInput in, ClassFile classFile)
            throws InvalidByteCodeException, IOException {

        LineNumberTableEntry lineNumberTableEntry = new LineNumberTableEntry();
        lineNumberTableEntry.setClassFile(classFile);
        lineNumberTableEntry.read(in);

        return lineNumberTableEntry;
    }

    /**
     * Get the <tt>start_pc</tt> of this line number association.
     *
     * @return the <tt>start_pc</tt>
     */
    public int getStartPc() {
        return startPc;
    }

    /**
     * Set the <tt>start_pc</tt> of this line number association.
     *
     * @param startPc the <tt>start_pc</tt>
     */
    public void setStartPc(int startPc) {
        this.startPc = startPc;
    }

    /**
     * Get the line number of this line number association.
     *
     * @return the line number
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Set the line number of this line number association.
     *
     * @param lineNumber the line number
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public void read(DataInput in)
            throws InvalidByteCodeException, IOException {

        startPc = in.readUnsignedShort();
        lineNumber = in.readUnsignedShort();

        if (debug) debug("read ");
    }

    public void write(DataOutput out)
            throws InvalidByteCodeException, IOException {

        super.write(out);
        out.writeShort(startPc);
        out.writeShort(lineNumber);
        if (debug) debug("wrote ");
    }

    protected void debug(String message) {
        super.debug(message + "LineNumberTable entry with start_pc " + startPc +
                ", line_number " + lineNumber);
    }

    protected String printAccessFlagsVerbose(int accessFlags) {
        if (accessFlags != 0)
            throw new RuntimeException("Access flags should be zero: " + Integer.toHexString(accessFlags));
        return "";
    }

}
