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
    Describes the <tt>multianewarray</tt> instruction.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2002-02-17 17:32:37 $
*/
public class MultianewarrayInstruction extends ImmediateShortInstruction {

    private int dimensions;
    
    public MultianewarrayInstruction(int opcode) {
        super(opcode); 
    }
    
    public int getSize() {
        return super.getSize() + 1;
    }

    /**
        Get the number of dimensions for the new array.
        @return the number of dimensions
     */
    public int getDimensions() {
        return dimensions;
    }

    /**
        Set the number of dimensions for the new array.
        @param dimensions the number of dimensions
     */
    public void setDimensions(int dimensions) {
        this.dimensions = dimensions;
    }

    public void read(ByteCodeInput in) throws IOException {
        super.read(in);

        dimensions = in.readUnsignedByte();
    }

    public void write(ByteCodeOutput out) throws IOException {
        super.write(out);

        out.writeByte(dimensions);
    }
    
}
