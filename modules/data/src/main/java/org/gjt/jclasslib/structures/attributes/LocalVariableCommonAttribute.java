/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Contains common attributes to a local variable table attribute structure.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public abstract class LocalVariableCommonAttribute extends AttributeInfo {
    protected static final int INITIAL_LENGTH = 2;

    protected LocalVariableCommonEntry[] localVariableTable;

    /**
     * Get the list of local variable associations of the parent <tt>Code</tt>
     * structure as an array of <tt>LocalVariableCommonEntry</tt> structures.
     *
     * @return the array
     */
    public LocalVariableCommonEntry[] getLocalVariableEntries() {
        return localVariableTable;
    }

    /**
     * Set the list of local variable associations of the parent <tt>Code</tt>
     * structure as an array of <tt>LocalVariableCommonEntry</tt> structures.
     *
     * @param localVariableEntries the array
     */
    public void setLocalVariableEntries(LocalVariableCommonEntry[] localVariableEntries) {
        this.localVariableTable = localVariableEntries;
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);

        int localVariableTableLength = getLength(localVariableTable);
        out.writeShort(localVariableTableLength);
        for (int i = 0; i < localVariableTableLength; i++) {
            localVariableTable[i].write(out);
        }

        if (debug) debug("wrote ");
    }
}
