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
import java.util.ArrayList;
import java.util.List;

/**
    Describes the <tt>lookupswitch</tt> instruction.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.7 $ $Date: 2003-08-18 07:58:35 $
*/
public class LookupSwitchInstruction extends PaddedInstruction {

    private int defaultOffset;
    private List matchOffsetPairs = new ArrayList();
   
    /**
        Constructor.
        @param opcode the opcode.
     */
    public LookupSwitchInstruction(int opcode) {
        super(opcode); 
    }
    
    public int getSize() {
        return super.getSize() + 8 + 8 * matchOffsetPairs.size();
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
        Get the match-offset pairs of the branch of this instruction as
        a <tt>java.util.List</tt> of <tt>MatchOffsetPair</tt>
        elements.
        @return the list
     */
    public List getMatchOffsetPairs() {
        return matchOffsetPairs;
    }
    
    /**
        Set the match-offset pairs of the branch of this instruction as
        a <tt>java.util.List</tt> of <tt>LookupSwitchInstruction.MatchOffsetPair</tt>
        elements.
        @param matchOffsetPairs the list
     */
    public void setMatchOffsetPairs(List matchOffsetPairs) {
        this.matchOffsetPairs = matchOffsetPairs;
    }

    public void read(ByteCodeInput in) throws IOException {
        super.read(in);

        matchOffsetPairs.clear();
        
        defaultOffset = in.readInt();
        int numberOfPairs = in.readInt();
        
        int match, offset;
        for (int i = 0; i < numberOfPairs; i++) {
            match = in.readInt();
            offset = in.readInt();
            
            matchOffsetPairs.add(new MatchOffsetPair(match, offset));
        }
        
    }

    public void write(ByteCodeOutput out) throws IOException {
        super.write(out);

        out.writeInt(defaultOffset);

        int numberOfPairs = matchOffsetPairs.size();
        out.writeInt(numberOfPairs);
        
        MatchOffsetPair currentMatchOffsetPair;
        for (int i = 0; i < numberOfPairs; i++) {
            currentMatchOffsetPair = (MatchOffsetPair)matchOffsetPairs.get(i);
            out.writeInt(currentMatchOffsetPair.getMatch());
            out.writeInt(currentMatchOffsetPair.getOffset());
        }
    }

    
}
