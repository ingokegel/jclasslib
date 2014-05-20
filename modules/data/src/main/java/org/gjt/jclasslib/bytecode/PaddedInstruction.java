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
    Base class for instructions which need a four byte padding relative
    to the start of the enclosing code of the parent <tt>Code</tt>
    attribute before reading immediate arguments.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class PaddedInstruction extends AbstractInstruction {

    /**
        Constructor.
        @param opcode the opcode.
     */
    public PaddedInstruction(Opcode opcode) {
        super(opcode); 
    }

    /**
        Get the padded size in bytes of this instruction.
        @param offset the offset at which this instruction is found.
        @return the padded size in bytes
     */
    public int getPaddedSize(int offset) {
        return getSize() + paddingBytes(offset + 1);
    }

    public void read(ByteCodeInput in) throws IOException {
        super.read(in);
        
        int bytesToRead = paddingBytes(in.getBytesRead());
        for (int i = 0; i < bytesToRead; i++) {
            in.readByte();
        }
    }

    public void write(ByteCodeOutput out) throws IOException {
        super.write(out);
        
        int bytesToWrite = paddingBytes(out.getBytesWritten());
        for (int i = 0; i < bytesToWrite; i++) {
            out.writeByte(0);
        }
    }
    
    private int paddingBytes(int bytesCount) {
        
        int bytesToPad = 4 - bytesCount % 4;
        return (bytesToPad == 4) ? 0 : bytesToPad;
    }
}
