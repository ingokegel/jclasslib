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
    Describes the <tt>iinc</tt> instruction.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2002-02-17 17:32:37 $
*/
public class IncrementInstruction extends ImmediateByteInstruction {

    private int incrementConst;
    
    public IncrementInstruction(int opcode, boolean wide) {
        super(opcode, wide); 
    }

    public IncrementInstruction(int opcode, boolean wide, int immediateByte, int incrementConst) {
        super(opcode, wide, immediateByte); 
        this.incrementConst = incrementConst;
    }
    
    
    public int getSize() {
        return super.getSize() + (wide ? 2 : 1);
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
    
    public void read(ByteCodeInput in) throws IOException {
        super.read(in);

        if (wide) {
            incrementConst = in.readUnsignedShort();
        } else {
            incrementConst = in.readUnsignedByte();
        }
    }

    public void write(ByteCodeOutput out) throws IOException {
        super.write(out);

        if (wide) {
            out.writeShort(incrementConst);
        } else {
            out.writeByte(incrementConst);
        }
    }
    
}
