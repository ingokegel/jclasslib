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
    Describes the <tt>invokeinterface</tt> instruction.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:16 $
*/
public class InvokeInterfaceInstruction extends ImmediateShortInstruction {

    private int count;
    
    public InvokeInterfaceInstruction(int opcode) {
        super(opcode); 
    }
    
    /**
        Get the argument count of this instruction 
        @return the argument count
     */
    public int getCount() {
        return count;
    }

    /**
        Set the argument count of this instruction 
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
