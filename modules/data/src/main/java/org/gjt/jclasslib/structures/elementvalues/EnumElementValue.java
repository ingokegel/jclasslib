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
 * Describes an  <tt>EnumElementValue</tt> attribute structure.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class EnumElementValue extends ElementValue {

    public final static String ENTRY_NAME = "EnumElement";

    private static final int LENGTH = 4;
    private int typeNameIndex;
    private int constNameIndex;

    protected EnumElementValue() {
        super(ENUM_TAG);
    }

    /**
     * Get the <tt>type_name_index</tt> of this element value entry.
     *
     * @return the <tt>type_name_index</tt>
     */
    public int getTypeNameIndex() {
        return this.typeNameIndex;
    }

    /**
     * Set the <tt>type_name_index</tt> of this element value entry.
     *
     * @param typeNameIndex the <tt>type_name_index</tt>
     */
    public void setTypeNameIndex(int typeNameIndex) {
        this.typeNameIndex = typeNameIndex;
    }

    /**
     * Get the <tt>const_name_index</tt> of this element value entry.
     *
     * @return the <tt>const_name_index</tt>
     */
    public int getConstNameIndex() {
        return this.constNameIndex;
    }

    /**
     * Set the <tt>const_name_index</tt> of this element value entry.
     *
     * @param constNameIndex the <tt>const_name_index</tt>
     */
    public void setConstNameIndex(int constNameIndex) {
        this.constNameIndex = constNameIndex;
    }


    protected int getSpecificLength() {
        return LENGTH;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {
        super.read(in);
        typeNameIndex = in.readUnsignedShort();
        constNameIndex = in.readUnsignedShort();

        if (debug) debug("read ");
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);
        out.writeShort(typeNameIndex);
        out.writeShort(constNameIndex);

        if (debug) debug("wrote ");
    }

    protected void debug(String message) {
        super.debug(message +
                "EnumElementValue with type_name_index " +
                typeNameIndex + ", const_name_index " + constNameIndex);

    }

    public String getEntryName() {
        return ENTRY_NAME;
    }

}
