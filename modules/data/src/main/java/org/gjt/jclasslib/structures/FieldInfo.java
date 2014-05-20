/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Describes a field in a <tt>ClassFile</tt> structure.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>, <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class FieldInfo extends ClassMember {

    /**
     * Factory method for creating <tt>FieldInfo</tt> structures from a <tt>DataInput</tt>.
     *
     * @param in        the <tt>DataInput</tt> from which to read the <tt>FieldInfo</tt> structure
     * @param classFile the parent class file of the structure to be created
     * @return the new <tt>FieldInfo</tt> structure
     * @throws InvalidByteCodeException if the byte code is invalid
     * @throws IOException              if an exception occurs with the <tt>DataInput</tt>
     */
    public static FieldInfo create(DataInput in, ClassFile classFile)
            throws InvalidByteCodeException, IOException {

        FieldInfo fieldInfo = new FieldInfo();
        fieldInfo.setClassFile(classFile);
        fieldInfo.read(in);

        return fieldInfo;
    }

    public void read(DataInput in)
            throws InvalidByteCodeException, IOException {

        super.read(in);

        if (debug) debug("read ");
    }

    public void write(DataOutput out)
            throws InvalidByteCodeException, IOException {

        super.write(out);
        if (debug) debug("wrote ");
    }

    protected void debug(String message) {
        super.debug(message + "field with access flags " + printAccessFlags(accessFlags) +
                ", name_index " + nameIndex + ", descriptor_index " + descriptorIndex +
                ", " + getLength(attributes) + " attributes");
    }

    protected String printAccessFlagsVerbose(int accessFlags) {
        return printAccessFlagsVerbose(AccessFlag.FIELD_ACCESS_FLAGS, accessFlags);
    }

}
