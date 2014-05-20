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
 * Describes a method in a <tt>ClassFile</tt> structure.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>, <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class MethodInfo extends ClassMember {

    /**
     * Factory method for creating <tt>MethodInfo</tt> structures from a <tt>DataInput</tt>.
     *
     * @param in        the <tt>DataInput</tt> from which to read the <tt>MethodInfo</tt> structure
     * @param classFile the parent class file of the structure to be created
     * @return the new <tt>MethodInfo</tt> structure
     * @throws InvalidByteCodeException if the byte code is invalid
     * @throws IOException              if an exception occurs with the <tt>DataInput</tt>
     */
    public static MethodInfo create(DataInput in, ClassFile classFile)
            throws InvalidByteCodeException, IOException {

        MethodInfo methodInfo = new MethodInfo();
        methodInfo.setClassFile(classFile);
        methodInfo.read(in);

        return methodInfo;
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
        super.debug(message + "method with access flags " + printAccessFlags(accessFlags) +
                ", name_index " + nameIndex + ", descriptor_index " + descriptorIndex +
                ", " + getLength(attributes) + " attributes");
    }

    protected String printAccessFlagsVerbose(int accessFlags) {
        return printAccessFlagsVerbose(AccessFlag.METHOD_ACCESS_FLAGS, accessFlags);
    }

}
