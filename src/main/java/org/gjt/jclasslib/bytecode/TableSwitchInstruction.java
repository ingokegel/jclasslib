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
    Describes the <tt>tableswitch</tt> instruction.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class TableSwitchInstruction extends PaddedInstruction {

    private int defaultOffset;
    private int lowByte;
    private int highByte;
    private int[] jumpOffsets;
   
    /**
        Constructor.
        @param opcode the opcode.
     */
    public TableSwitchInstruction(Opcode opcode) {
        super(opcode); 
    }
    
    public int getSize() {
        return super.getSize() + 12 + 4 * jumpOffsets.length;
    }

    /**
        Get the default offset of the branch of this instruction.
        @return the offset
     */
    public int getDefaultOffset() {
        return defaultOffset;
    }

    /**
        Set the default offset of the branch of this instruction.
        @param defaultOffset the offset
     */
    public void setDefaultOffset(int defaultOffset) {
        this.defaultOffset = defaultOffset;
    }
    
    /**
        Get the lower bound for the table switch.
        @return the lower bound
     */
    public int getLowByte() {
        return lowByte;
    }

    /**
        Set the lower bound for the table switch.
        @param lowByte the lower bound
     */
    public void setLowByte(int lowByte) {
        this.lowByte = lowByte;
    }
    
    /**
        Get the upper bound for the table switch.
        @return the upper bound
     */
    public int getHighByte() {
        return highByte;
    }

    /**
        Set the upper bound for the table switch.
        @param highByte the upper bound
     */
    public void setHighByte(int highByte) {
        this.highByte = highByte;
    }
    
    /**
        Get the array of relative jump offsets for the table switch.
        @return the array
     */
    public int[] getJumpOffsets() {
        return jumpOffsets;
    }

    /**
        Set the array of relative jump offsets for the table switch.
        @param jumpOffsets the array
     */
    public void setJumpOffsets(int[] jumpOffsets) {
        this.jumpOffsets = jumpOffsets;
    }
    
    public void read(ByteCodeInput in) throws IOException {
        super.read(in);

        defaultOffset = in.readInt();
        lowByte = in.readInt();
        highByte = in.readInt();

        int numberOfOffsets = highByte - lowByte + 1;
        jumpOffsets = new int[numberOfOffsets];
        
        for (int i = 0; i < numberOfOffsets; i++) {
            jumpOffsets[i] = in.readInt();
        }
        
    }

    public void write(ByteCodeOutput out) throws IOException {
        super.write(out);

        out.writeInt(defaultOffset);
        out.writeInt(lowByte);
        out.writeInt(highByte);

        for (int jumpOffset : jumpOffsets) {
            out.writeInt(jumpOffset);
        }
    }

}
