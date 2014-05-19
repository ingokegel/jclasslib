/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.IOException;

/**
 * Describes an  <tt>LocalVariableTypeTable</tt> attribute structure.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class LocalVariableTypeTableAttribute extends LocalVariableCommonAttribute {

    /**
     * Name of the attribute as in the corresponding constant pool entry.
     */
    public static final String ATTRIBUTE_NAME = "LocalVariableTypeTable";

    /**
     * Get the list of local variable associations of the parent <tt>Code</tt>
     * structure as an array of <tt>LocalVariableTypeTableEntry</tt> structures.
     *
     * @return the array
     */
    public LocalVariableTypeTableEntry[] getLocalVariableTypeTable() {
        return (LocalVariableTypeTableEntry[])localVariableTable;
    }

    /**
     * Set the list of local variable associations of the parent <tt>Code</tt>
     * structure as an array of <tt>LocalVariableTypeTableEntry</tt> structures.
     *
     * @param localVariableTypeTable the array
     */
    public void setLocalVariableTypeTable(LocalVariableTypeTableEntry[] localVariableTypeTable) {
        this.localVariableTable = localVariableTypeTable;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {

        int localVariableTypeTableLength = in.readUnsignedShort();
        localVariableTable = new LocalVariableTypeTableEntry[localVariableTypeTableLength];
        for (int i = 0; i < localVariableTypeTableLength; i++) {
            localVariableTable[i] = LocalVariableTypeTableEntry.create(in, classFile);
        }

        if (debug) debug("read ");
    }

    public int getAttributeLength() {
        return INITIAL_LENGTH + getLength(localVariableTable) * LocalVariableTypeTableEntry.LENGTH;
    }

    protected void debug(String message) {
        super.debug(message + "LocalVariableTypeTable attribute with " +
                getLength(localVariableTable) + " entries");
    }
}
