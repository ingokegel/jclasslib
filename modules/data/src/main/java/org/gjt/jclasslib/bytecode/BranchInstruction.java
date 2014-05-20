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
    Describes an instruction that branches to a different offset.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class BranchInstruction extends AbstractBranchInstruction {

    /**
        Constructor.
        @param opcode the opcode.
     */
    public BranchInstruction(Opcode opcode) {
        super(opcode); 
    }

    /**
        Constructor.
        @param opcode the opcode.
        @param branchOffset the branch offset.
     */
    public BranchInstruction(Opcode opcode, int branchOffset) {
        super(opcode, branchOffset);
    }
    
    public int getSize() {
        return super.getSize() + 2;
    }

    public void read(ByteCodeInput in) throws IOException {
        super.read(in);

        setBranchOffset(in.readShort());
    }

    public void write(ByteCodeOutput out) throws IOException {
        super.write(out);

        out.writeShort(getBranchOffset());
    }
    
}
