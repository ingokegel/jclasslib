/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.constants;

import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
    Describes a <tt>CONSTANT_Fieldref_info</tt> constant pool data structure.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ConstantFieldrefInfo extends ConstantReference {
    
    public byte getTag() {
        return CONSTANT_FIELDREF;
    }

    public String getTagVerbose() {
        return CONSTANT_FIELDREF_VERBOSE;
    }

    public void read(DataInput in)
        throws InvalidByteCodeException, IOException {
        
        super.read(in);
        if (debug) debug("read ");
    }

    public void write(DataOutput out)
        throws InvalidByteCodeException, IOException {
        
        out.writeByte(CONSTANT_FIELDREF);
        super.write(out);
        if (debug) debug("wrote ");
    }
    
    protected void debug(String message) {
        super.debug(message + getTagVerbose() + " with class_index " + classIndex +
              " and name_and_type_index " + nameAndTypeIndex);
    }

}
