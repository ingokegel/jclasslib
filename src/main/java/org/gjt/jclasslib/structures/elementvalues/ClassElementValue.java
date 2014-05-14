/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.elementvalues;

import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Describes an  <tt>ClassElementValue</tt> attribute structure.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class ClassElementValue extends ElementValue {

    public final static String ENTRY_NAME = "ClassElement";

    private static final int LENGTH = 2;
    private int classInfoIndex;

    protected ClassElementValue() {
        super(CLASS_TAG);
    }

    /**
     * Get the <tt>class_info_index</tt> of this element value entry.
     *
     * @return the <tt>class_info_index</tt>
     */
    public int getClassInfoIndex() {
        return this.classInfoIndex;
    }

    /**
     * Set the <tt>class_info_index</tt> of this element value entry.
     *
     * @param classInfoIndex the <tt>class_info_index</tt>
     */
    public void setClassInfoIndex(int classInfoIndex) {
        this.classInfoIndex = classInfoIndex;
    }

    protected int getSpecificLength() {
        return LENGTH;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {
        super.read(in);

        classInfoIndex = in.readUnsignedShort();

        if (debug) debug("read ");
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);

        out.writeShort(classInfoIndex);

        if (debug) debug("wrote ");
    }

    protected void debug(String message) {
        super.debug(message +
                "ClassElementValue with class_info_index " +
                classInfoIndex);
    }

    public String getEntryName() {
        return ENTRY_NAME;
    }

}
