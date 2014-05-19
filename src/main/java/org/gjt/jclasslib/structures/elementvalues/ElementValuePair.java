/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.elementvalues;

import org.gjt.jclasslib.structures.AbstractStructure;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Describes an  <tt>ElementValuePair</tt> attribute structure.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class ElementValuePair extends AbstractStructure {

    public final static String ENTRY_NAME = "ElementValuePair";

    private static final int INITIAL_LENGTH = 2;

    private int elementNameIndex;
    private ElementValue elementValue;


    /**
     * Factory for creating <tt>ElementValuePair</tt> structures.
     *
     * @param in        the <tt>DataInput</tt> from which to read the
     *                  <tt>ElementValuePair</tt> structure
     * @param classFile the parent class file of the structure to be created
     * @return the new <tt>ElementValue</tt> structure
     * @throws org.gjt.jclasslib.structures.InvalidByteCodeException
     *                             if the byte code is invalid
     * @throws java.io.IOException if an exception occurs with the <tt>DataInput</tt>
     */
    public static ElementValuePair create(DataInput in, ClassFile classFile) throws InvalidByteCodeException, IOException {

        ElementValuePair elementValuePairEntry = new ElementValuePair();
        elementValuePairEntry.setClassFile(classFile);
        elementValuePairEntry.read(in);

        return elementValuePairEntry;
    }


    /**
     * Get the <tt>element_value</tt> of this element value pair.
     *
     * @return the <tt>element_value</tt>
     */
    public ElementValue getElementValue() {
        return this.elementValue;
    }

    /**
     * Set the <tt>element_value</tt> of this element value pair.
     *
     * @param elementValue the <tt>element_value</tt>
     */
    public void setElementValue(ElementValue elementValue) {
        this.elementValue = elementValue;
    }

    /**
     * Get the <tt>element_name_index</tt> of this element value pair.
     *
     * @return the <tt>element_name_index</tt>
     */
    public int getElementNameIndex() {
        return elementNameIndex;
    }

    /**
     * Set the <tt>element_name_index</tt> of this element value pair.
     *
     * @param elementNameIndex the <tt>element_name_index</tt>
     */
    public void setElementNameIndex(int elementNameIndex) {
        this.elementNameIndex = elementNameIndex;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {
        super.read(in);

        elementNameIndex = in.readUnsignedShort();
        elementValue = ElementValue.create(in, classFile);

        if (debug) debug("read ");
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);

        out.writeShort(elementNameIndex);
        elementValue.write(out);

        if (debug) debug("wrote ");
    }


    protected String printAccessFlagsVerbose(int accessFlags) {
        if (accessFlags != 0)
            throw new RuntimeException("Access flags should be zero: " +
                    Integer.toHexString(accessFlags));
        return "";
    }

    public int getLength() {
        return INITIAL_LENGTH + elementValue.getLength();
    }

    public String getEntryName() {
        return ENTRY_NAME;
    }
}
