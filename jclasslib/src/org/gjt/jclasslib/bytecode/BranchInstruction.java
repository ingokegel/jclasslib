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
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.3 $ $Date: 2002-02-27 16:47:43 $
*/
public class BranchInstruction extends AbstractInstruction {

    private int branchOffset;
   
    public BranchInstruction(int opcode) {
        super(opcode); 
    }
    
    public BranchInstruction(int opcode, int branchOffset) {
        super(opcode); 
        this.branchOffset = branchOffset;
    }
    
    public int getSize() {
        return super.getSize() + 2;
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
