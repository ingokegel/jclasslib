/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.IOException;

/**
 * Describes an entry in a <tt>LocalVariableTableEntry</tt> attribute structure.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>, <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class LocalVariableTableEntry extends LocalVariableCommonEntry {
    /**
     * Factory method for creating <tt>LocalVariableTableEntry</tt> structures.
     *
     * @param in        the <tt>DataInput</tt> from which to read the
     *                  <tt>LocalVariableTableEntry</tt> structure
     * @param classFile the parent class file of the structure to be created
     * @return the new <tt>LocalVariableTableEntry</tt> structure
     * @throws InvalidByteCodeException if the byte code is invalid
     * @throws IOException              if an exception occurs with the <tt>DataInput</tt>
     */
    public static LocalVariableTableEntry create(DataInput in, ClassFile classFile)
            throws InvalidByteCodeException, IOException {

        LocalVariableTableEntry localVariableTableEntry = new LocalVariableTableEntry();
        localVariableTableEntry.setClassFile(classFile);
        localVariableTableEntry.read(in);

        return localVariableTableEntry;
    }


    /**
     * Get the index of the constant pool entry containing the descriptor of this
     * local variable.
     *
     * @return the index
     */
    public int getDescriptorIndex() {
        return descriptorOrSignatureIndex;
    }

    /**
     * Get the index of the constant pool entry containing the descriptor of this
     * local variable.
     *
     * @param descriptorIndex the index
     */
    public void setDescriptorIndex(int descriptorIndex) {
        setDescriptorOrSignatureIndex(descriptorIndex);
    }


    protected void debug(String message) {
        super.debug(message + "LocalVariableTable entry with start_pc " +
                startPc + ", length " + length + ", name_index " + nameIndex +
                ", descriptor_index " + descriptorOrSignatureIndex + ", index " + index);
    }
}
