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
    Describes the <tt>invokeinterface</tt> instruction.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class InvokeInterfaceInstruction extends ImmediateShortInstruction {

    private int count;
    
    /**
        Constructor.
        @param opcode the opcode.
     */
    public InvokeInterfaceInstruction(Opcode opcode) {
        super(opcode); 
    }
    
    /**
        Constructor.
        @param opcode the opcode
        @param immediateShort the immediate short value.
        @param count the argument count.
     */
    public InvokeInterfaceInstruction(Opcode opcode, int immediateShort, int count) {
        super(opcode, immediateShort); 
        this.count = count;
    }
    
    
    public int getSize() {
        return super.getSize() + 2;
    }

    /**
        Get the argument count of this instruction.
        @return the argument count
     */
    public int getCount() {
        return count;
    }

    /**
        Set the argument count of this instruction.
        @param count the argument count
     */
    public void setCount(int count) {
        this.count = count;
    }

    public void read(ByteCodeInput in) throws IOException {
        super.read(in);

        count = in.readUnsignedByte();
        // Next byte is always 0 and thus discarded
        in.readByte();
    }

    public void write(ByteCodeOutput out) throws IOException {
        super.write(out);

        out.writeByte(count);
        out.writeByte(0);
    }
    
}
