/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.bytecode;

import org.gjt.jclasslib.io.ByteCodeInput;
import org.gjt.jclasslib.io.ByteCodeOutput;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

/**
    Describes the <tt>iinc</tt> instruction.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class IncrementInstruction extends ImmediateByteInstruction {

    private int incrementConst;
    
    /**
        Constructor.
        @param opcode the opcode
        @param wide whether the instruction is a wide instruction.
     */
    public IncrementInstruction(Opcode opcode, boolean wide) {
        super(opcode, wide); 
    }

    /**
        Constructor.
        @param opcode the opcode
        @param wide whether the instruction is a wide instruction.
        @param immediateByte the immediate byte value.
        @param incrementConst the increment.
     */
    public IncrementInstruction(Opcode opcode, boolean wide, int immediateByte, int incrementConst) {
        super(opcode, wide, immediateByte); 
        this.incrementConst = incrementConst;
    }
    
    
    public int getSize() {
        return super.getSize() + (isWide() ? 2 : 1);
    }

    /**
        Get the increment of this instruction.
        @return the increment
     */
    public int getIncrementConst() {
        return incrementConst;
    }

    /**
        Set the increment of this instruction.
        @param incrementConst the increment
     */
    public void setIncrementConst(int incrementConst) {
        this.incrementConst = incrementConst;
    }
    
    public void read(@NotNull ByteCodeInput input) throws IOException{
        try {
            super.read(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isWide()) {
            incrementConst = input.readShort();
        } else {
            incrementConst = input.readByte();
        }
    }

    public void write(@NotNull ByteCodeOutput output) throws IOException{
        try {
            super.write(output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isWide()) {
            output.writeShort(incrementConst);
        } else {
            output.writeByte(incrementConst);
        }
    }

}
