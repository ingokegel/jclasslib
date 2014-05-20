/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Describes an  <tt>EnclosingMethod</tt> attribute structure.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class EnclosingMethodAttribute extends AttributeInfo {
    /**
     * Name of the attribute as in the corresponding constant pool entry.
     */
    public static final String ATTRIBUTE_NAME = "EnclosingMethod";

    private static final int LENGTH = 4;

    private int classInfoIndex;
    private int methodInfoIndex;

    /**
     * Get the constant pool index of the <tt>CONSTANT_Class_info</tt>
     * structure representing the innermost class that encloses the
     * declaration of the current class.
     *
     * @return the index
     */
    public int getClassInfoIndex() {
        return classInfoIndex;
    }

    /**
     * Get the constant pool index of the <tt>CONSTANT_NameAndType_info</tt>
     * structure representing the name and type of a method in the class
     * referenced by the class info index above.
     *
     * @return the index
     */
    public int getMethodInfoIndex() {
        return methodInfoIndex;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {

        classInfoIndex = in.readUnsignedShort();
        methodInfoIndex = in.readUnsignedShort();

        if (debug) debug("read ");
    }

    public void write(DataOutput out)
            throws InvalidByteCodeException, IOException {
        super.write(out);

        out.writeShort(classInfoIndex);
        out.writeShort(methodInfoIndex);

        if (debug) debug("wrote ");
    }

    public int getAttributeLength() {
        return LENGTH;
    }

    protected void debug(String message) {
        super.debug(message + "EnclosingMethod attribute with class index " +
                classInfoIndex + " and method index " + methodInfoIndex);
    }
}
