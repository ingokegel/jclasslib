/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.bytecode;

import org.gjt.jclasslib.io.*;
import java.io.*;
import java.util.*;

/**
    Describes the <tt>lookupswitch</tt> instruction.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:17 $
*/
public class LookupSwitchInstruction extends PaddedInstruction {

    private int defaultOffset;
    private List matchOffsetPairs = new ArrayList();
   
    public LookupSwitchInstruction(int opcode) {
        super(opcode); 
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
        a <tt>java.util.List</tt> of <tt>LookupSwitchInstruction.MatchOffsetPair</tt>
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

    /** Holds a single match-offset pair */
    public static class MatchOffsetPair {
        private int match;
        private int offset;
        
        public MatchOffsetPair(int match, int offset) {
            this.match = match;
            this.offset = offset;
        }
        
        /**
            Get the match value of this match-offset pair.
            @return the value
         */
        public int getMatch() {
            return match;
        }

        /**
            Set the match value of this match-offset pair.
            @param match the value
         */
        public void setMatch(int match) {
            this.match = match;
        }

        /**
            Get the offset of the branch for this match-offset pair.
            @return the offset
         */
        public int getOffset() {
            return offset;
        }

        /**
            Set the offset of the branch for this match-offset pair.
            @param offset the offset
         */
        public void setOffset(int offset) {
            this.offset = offset;
        }
        
    }
    
}
