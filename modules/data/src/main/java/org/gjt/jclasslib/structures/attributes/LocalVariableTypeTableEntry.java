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
 * Describes an  <tt>LocalVariableTypeTableEntry</tt> attribute structure.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class LocalVariableTypeTableEntry extends LocalVariableCommonEntry {
    /**
     * Factory method for creating <tt>LocalVariableTypeTableEntry</tt> structures.
     *
     * @param in        the <tt>DataInput</tt> from which to read the
     *                  <tt>LocalVariableTypeTableEntry</tt> structure
     * @param classFile the parent class file of the structure to be created
     * @return the new <tt>LocalVariableTypeTableEntry</tt> structure
     * @throws org.gjt.jclasslib.structures.InvalidByteCodeException
     *                             if the byte code is invalid
     * @throws java.io.IOException if an exception occurs with the <tt>DataInput</tt>
     */
    public static LocalVariableTypeTableEntry create(DataInput in, ClassFile classFile)
            throws InvalidByteCodeException, IOException {

        LocalVariableTypeTableEntry localVariableTypeTableEntry = new LocalVariableTypeTableEntry();
        localVariableTypeTableEntry.setClassFile(classFile);
        localVariableTypeTableEntry.read(in);

        return localVariableTypeTableEntry;
    }

    /**
     * Get the index of the constant pool entry containing the signature of
     * this local variable.
     *
     * @return the index
     */
    public int getSignatureIndex() {
        return descriptorOrSignatureIndex;
    }

    /**
     * Get the index of the constant pool entry containing the signature of
     * this local variable.
     *
     * @param signatureIndex the index
     */
    public void setSignatureIndex(int signatureIndex) {
        this.descriptorOrSignatureIndex = signatureIndex;
    }

    protected void debug(String message) {
        super.debug(message +
                "LocalVariableTypeTable entry with start_pc " + startPc +
                ", length " + length + ", name_index " + nameIndex +
                ", signature_index " + descriptorOrSignatureIndex +
                ", index " + index);
    }
}
