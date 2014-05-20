/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.elementvalues;

import org.gjt.jclasslib.structures.AnnotationData;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Describes an  <tt>Annotation</tt> attribute structure.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class AnnotationElementValue extends ElementValue implements AnnotationData {

    public final static String ENTRY_NAME = "Annotation";

    private static final int INITIAL_LENGTH = 4;

    private int typeIndex;
    private ElementValuePair[] elementValuePairEntries;


    public AnnotationElementValue() {
        super(ANNOTATION_TAG);
    }

    public String getEntryName() {
        return ENTRY_NAME;
    }

    public ElementValuePair[] getElementValuePairEntries() {
        return elementValuePairEntries;
    }

    /**
     * Set the list of element value pair  associations of the parent
     * structure as an array of <tt>ElementValuePair</tt> structures.
     *
     * @param elementValuePairEntries the array
     */
    public void setElementValuePairEntries(ElementValuePair[] elementValuePairEntries) {
        this.elementValuePairEntries = elementValuePairEntries;
    }

    public int getTypeIndex() {
        return typeIndex;
    }

    /**
     * Set the <tt>type_index</tt> of this annotation.
     *
     * @param typeIndex the <tt>type_index</tt>
     */
    public void setTypeIndex(int typeIndex) {
        this.typeIndex = typeIndex;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {
        super.read(in);

        typeIndex = in.readUnsignedShort();
        int elementValuePairEntriesLength = in.readUnsignedShort();

        elementValuePairEntries = new ElementValuePair[elementValuePairEntriesLength];

        for (int i = 0; i < elementValuePairEntriesLength; i++) {
            elementValuePairEntries[i] = ElementValuePair.create(in, classFile);
        }

        if (debug) debug("read ");
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);

        out.writeShort(typeIndex);
        int elementValuePairEntriesLength = getLength(elementValuePairEntries);

        out.writeShort(elementValuePairEntriesLength);
        for (int i = 0; i < elementValuePairEntriesLength; i++) {
            elementValuePairEntries[i].write(out);
        }

        if (debug) debug("wrote ");
    }

    protected int getSpecificLength() {
        int length = INITIAL_LENGTH;
        for (ElementValuePair elementValuePairEntry : elementValuePairEntries) {
            length += elementValuePairEntry.getLength();
        }
        return length;
    }

    protected void debug(String message) {
        super.debug(message + "Annotation with " +
                getLength(elementValuePairEntries) + " value pair elements");
    }
}
