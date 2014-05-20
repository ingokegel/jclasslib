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
    Describes an instruction that is followed by an immediate int.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class WideBranchInstruction extends AbstractBranchInstruction {

    /**
        Constructor.
        @param opcode the opcode.
     */
    public WideBranchInstruction(Opcode opcode) {
        super(opcode); 
    }

    /**
        Constructor.
        @param opcode the opcode.
        @param branchOffset the immediate int value.
     */
    public WideBranchInstruction(Opcode opcode, int branchOffset) {
        super(opcode, branchOffset);
    }
    
    public int getSize() {
        return super.getSize() + 4;
    }

    public void read(ByteCodeInput in) throws IOException {
        super.read(in);

        setBranchOffset(in.readInt());
    }

    public void write(ByteCodeOutput out) throws IOException {
        super.write(out);

        out.writeInt(getBranchOffset());
    }
    
}
