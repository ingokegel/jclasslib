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
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:16 $
*/
public class PaddedInstruction extends AbstractInstruction {

    public PaddedInstruction(int opcode) {
        super(opcode); 
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
