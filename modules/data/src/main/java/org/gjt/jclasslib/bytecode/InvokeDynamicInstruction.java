/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.bytecode;

import org.gjt.jclasslib.io.ByteCodeInput;
import org.gjt.jclasslib.io.ByteCodeOutput;

import java.io.IOException;

/**
    Describes the <tt>invokedynamic</tt> instruction.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Hannes Kegel</a>
*/
public class InvokeDynamicInstruction extends ImmediateShortInstruction {

    /**
        Constructor.
        @param opcode the opcode.
     */
    public InvokeDynamicInstruction(Opcode opcode) {
        super(opcode);
    }

    /**
        Constructor.
        @param opcode the opcode
        @param immediateShort the immediate short value.
     */
    public InvokeDynamicInstruction(Opcode opcode, int immediateShort) {
        super(opcode, immediateShort); 
    }
    
    
    public int getSize() {
        return super.getSize() + 2;
    }

    public void read(ByteCodeInput in) throws IOException {
        super.read(in);

        // Next two bytes are always 0 and thus discarded
        in.readUnsignedShort();
    }

    public void write(ByteCodeOutput out) throws IOException {
        super.write(out);

        out.writeShort(0);
    }
    
}
