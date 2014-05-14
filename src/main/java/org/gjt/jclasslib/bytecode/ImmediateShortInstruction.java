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
    Describes an instruction that is followed by an immediate unsigned short.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ImmediateShortInstruction extends AbstractInstruction {

    private int immediateShort;
   
    public int getSize() {
        return super.getSize() + 2;
    }

    /**
        Constructor.
        @param opcode the opcode.
     */
    public ImmediateShortInstruction(Opcode opcode) {
        super(opcode); 
    }
    
    /**
        Constructor.
        @param opcode the opcode.
        @param immediateShort the immediate short value.
     */
    public ImmediateShortInstruction(Opcode opcode, int immediateShort) {
        super(opcode); 
        this.immediateShort = immediateShort;
    }
    
    /**
        Get the immediate unsigned short of this instruction.
        @return the short
     */
    public int getImmediateShort() {
        return immediateShort;
    }

    /**
        Set the immediate unsigned short of this instruction.
        @param immediateShort the short
     */
    public void setImmediateShort(int immediateShort) {
        this.immediateShort = immediateShort;
    }
    
    public void read(ByteCodeInput in) throws IOException {
        super.read(in);

        immediateShort = in.readUnsignedShort();
    }

    public void write(ByteCodeOutput out) throws IOException {
        super.write(out);

        out.writeShort(immediateShort);
    }
    
}
