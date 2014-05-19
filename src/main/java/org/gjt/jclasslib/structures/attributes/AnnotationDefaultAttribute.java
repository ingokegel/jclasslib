/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.gjt.jclasslib.structures.elementvalues.ElementValue;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Describes an  <tt>AnnotationDefault</tt> attribute structure.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class AnnotationDefaultAttribute extends AttributeInfo {
    /**
     * Name of the attribute as in the corresponding constant pool entry.
     */
    public static final String ATTRIBUTE_NAME = "AnnotationDefault";

    private ElementValue defaultValue;

    /**
     * Get the <tt>default_value</tt> of this attribute.
     *
     * @return the <tt>default_value</tt>
     */
    public ElementValue getDefaultValue() {
        return this.defaultValue;
    }

    /**
     * Set the <tt>default_value</tt> of this attribute.
     *
     * @param defaultValue the <tt>default_value</tt>
     */
    public void setDefaultValue(ElementValue defaultValue) {
        this.defaultValue = defaultValue;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {

        defaultValue = ElementValue.create(in, classFile);

        if (debug) debug("read ");
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);

        defaultValue.write(out);

        if (debug) debug("wrote ");
    }

    public int getAttributeLength() {
        return defaultValue.getLength();
    }

    protected void debug(String message) {
        super.debug(message + "AnnotationDefaultAttribute");
    }
}
