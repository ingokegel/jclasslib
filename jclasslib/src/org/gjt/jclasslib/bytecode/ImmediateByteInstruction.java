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
    Describes an instruction that is followed by an immediate unsigned byte.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.3 $ $Date: 2002-02-27 16:47:43 $
*/
public class ImmediateByteInstruction extends AbstractInstruction {

    private int immediateByte;
    /** Indicates whether the instuction is subject to a wide instruction or not */
    protected boolean wide;
    
    public ImmediateByteInstruction(int opcode, boolean wide) {
        super(opcode); 
        this.wide = wide;
    }
    
    public ImmediateByteInstruction(int opcode, boolean wide, int immediateByte) {
        this(opcode, wide); 
        this.immediateByte = immediateByte;
    }
    
    public int getSize() {
        return super.getSize() + (wide ? 2 : 1);
    }

    /**
        Get the immediate unsigned byte of this instruction.
        @return the byte
     */
    public int getImmediateByte() {
        return immediateByte;
    }

    /**
        Set the immediate unsigned byte of this instruction.
        @param immediateByte the byte
     */
     public void setImmediateByte(int immediateByte) {
        this.immediateByte = immediateByte;
    }
    
    /**
        Check whether the instuction is subject to a wide instruction or not.
        @return wide or not
     */
    public boolean isWide() {
        return wide;
    }
    
    /**
        Set whether the instuction is subject to a wide instruction or not.
        @param wide wide or not
     */
    public void setWide(boolean wide) {
        this.wide = wide;
    }
    
    public void read(ByteCodeInput in) throws IOException {
        super.read(in);

        if (wide) {
            immediateByte = in.readUnsignedShort();
        } else {
            immediateByte = in.readUnsignedByte();
        }
    }

    public void write(ByteCodeOutput out) throws IOException {
        super.write(out);

        if (wide) {
            out.writeShort(immediateByte);
        } else {
            out.writeByte(immediateByte);
        }
    }
    
}
