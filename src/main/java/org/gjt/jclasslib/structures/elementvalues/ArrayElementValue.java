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
 * Describes an  <tt>ArrayElementValue</tt> attribute structure.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class ArrayElementValue extends ElementValue {

    public final static String ENTRY_NAME = "ArrayElement";

    private static final int INITIAL_LENGTH = 2;
    private ElementValue[] elementValueEntries;


    protected ArrayElementValue() {
        super(ARRAY_TAG);
    }

    /**
     * Get the list of element values associations of the this array
     * element value entry.
     *
     * @return the array
     */
    public ElementValue[] getElementValueEntries() {
        return this.elementValueEntries;
    }

    /**
     * Set the list of element values associations of this array
     * element value entry.
     *
     * @param elementValueEntries the array
     */
    public void setConstValueIndex(ElementValue[] elementValueEntries) {
        this.elementValueEntries = elementValueEntries;
    }

    protected int getSpecificLength() {
        int length = INITIAL_LENGTH;
        for (ElementValue elementValueEntry : elementValueEntries) {
            length += elementValueEntry.getLength();
        }
        return length;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {
        super.read(in);

        int elementValueEntriesLength = in.readUnsignedShort();
        elementValueEntries = new ElementValue[elementValueEntriesLength];
        for (int i = 0; i < elementValueEntries.length; i++) {
            elementValueEntries[i] = ElementValue.create(in, classFile);
        }

        if (debug) debug("read ");
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);

        int elementValueEntriesLength = getLength(elementValueEntries);

        out.writeShort(elementValueEntriesLength);
        for (int i = 0; i < elementValueEntriesLength; i++) {
            elementValueEntries[i].write(out);
        }

        if (debug) debug("wrote ");
    }

    protected void debug(String message) {
        super.debug(message +
                "ArrayElementValue with " +
                getLength(elementValueEntries) + " entries");
    }

    public String getEntryName() {
        return ENTRY_NAME;
    }

}
