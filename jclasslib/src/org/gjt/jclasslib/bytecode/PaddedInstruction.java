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
    Base class for intstructions which need a four byte padding relative
    to the start of the enclosing bytecode of the parent <tt>Code</tt>
    attribute before reading immediate arguments.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2002-02-17 17:32:37 $
*/
public class PaddedInstruction extends AbstractInstruction {

    public PaddedInstruction(int opcode) {
        super(opcode); 
    }

    /**
        Get the padded size in bytes of this instruction.
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
