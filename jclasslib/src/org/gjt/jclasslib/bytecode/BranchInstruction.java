/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.bytecode;

import org.gjt.jclasslib.io.*;
import java.io.*;

/**
    Describes an instruction that branches to a different offset.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:17 $
*/
public class BranchInstruction extends AbstractInstruction {

    private int branchOffset;
   
    public BranchInstruction(int opcode) {
        super(opcode); 
    }
    
    /**
        Get the relative offset of the branch of this instruction.
        @return the offset
     */
    public int getBranchOffset() {
        return branchOffset;
    }

    /**
        Set the relative offset of the branch of this instruction.
        @param branchOffset the offset
     */
    public void setBranchOffset(int branchOffset) {
        this.branchOffset = branchOffset;
    }
    
    public void read(ByteCodeInput in) throws IOException {
        super.read(in);

        branchOffset = in.readShort();
    }

    public void write(ByteCodeOutput out) throws IOException {
        super.write(out);

        out.writeShort(branchOffset);
    }
    
}
