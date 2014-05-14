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
 * Describes an  <tt>ConstElementValue</tt> attribute structure.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class ConstElementValue extends ElementValue {

    public final static String ENTRY_NAME = "ConstElement";

    private static final int LENGTH = 2;
    private int constValueIndex;

    protected ConstElementValue(int tag) {
        super(tag);
    }

    /**
     * Get the <tt>const_value_index</tt> of this element value entry.
     *
     * @return the <tt>const_value_index</tt>
     */
    public int getConstValueIndex() {
        return this.constValueIndex;
    }

    /**
     * Set the <tt>const_value_index</tt> of this element value entry.
     *
     * @param constValueIndex the <tt>const_value_index</tt>
     */
    public void setConstValueIndex(int constValueIndex) {
        this.constValueIndex = constValueIndex;
    }

    protected int getSpecificLength() {
        return LENGTH;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {
        super.read(in);

        constValueIndex = in.readUnsignedShort();

        if (debug) debug("read ");
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);

        out.writeShort(constValueIndex);

        if (debug) debug("wrote ");
    }

    protected void debug(String message) {
        super.debug(message +
                "ConstElementValue with const_value_index " +
                constValueIndex);
    }

    public String getEntryName() {
        return ENTRY_NAME;
    }
}
