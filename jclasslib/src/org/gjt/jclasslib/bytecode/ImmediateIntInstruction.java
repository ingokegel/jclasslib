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
    @version $Revision: 1.5 $ $Date: 2003-08-18 07:58:35 $
*/
public class ImmediateIntInstruction extends AbstractInstruction {

    private int immediateInt;
   
    /**
        Constructor.
        @param opcode the opcode.
     */
    public ImmediateIntInstruction(int opcode) {
        super(opcode); 
    }

    /**
        Constructor.
        @param opcode the opcode.
        @param immediateInt the immediate int value.
     */
    public ImmediateIntInstruction(int opcode, int immediateInt) {
        super(opcode); 
        this.immediateInt = immediateInt;
    }
    
    public int getSize() {
        return super.getSize() + 4;
    }

    /**
        Get the immediate int of this instruction.
        @return the int
     */
    public int getImmediateInt() {
        return immediateInt;
    }

    /**
        Set the immediate int of this instruction.
        @param immediateInt the int
     */
    public void setImmediateInt(int immediateInt) {
        this.immediateInt = immediateInt;
    }
    
    public void read(ByteCodeInput in) throws IOException {
        super.read(in);

        immediateInt = in.readInt();
    }

    public void write(ByteCodeOutput out) throws IOException {
        super.write(out);

        out.writeInt(immediateInt);
    }
    
}
