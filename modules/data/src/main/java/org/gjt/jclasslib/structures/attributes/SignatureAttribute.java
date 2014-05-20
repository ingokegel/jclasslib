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
 * Describes an  <tt>Signature</tt> attribute structure.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class SignatureAttribute extends AttributeInfo {
    /**
     * Name of the attribute as in the corresponding constant pool entry.
     */
    public static final String ATTRIBUTE_NAME = "Signature";

    private static final int LENGTH = 2;

    private int signatureIndex;

    /**
     * Get the constant pool index of the <tt>CONSTANT_Utf8_info</tt>
     * structure representing the signature.
     *
     * @return the index
     */
    public int getSignatureIndex() {
        return signatureIndex;
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {

        signatureIndex = in.readUnsignedShort();

        if (debug) debug("read ");
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);

        out.writeShort(signatureIndex);

        if (debug) debug("wrote ");
    }

    public int getAttributeLength() {
        return LENGTH;
    }

    protected void debug(String message) {
        super.debug(message +
                "Signature attribute with signature index " + signatureIndex);
    }
}
