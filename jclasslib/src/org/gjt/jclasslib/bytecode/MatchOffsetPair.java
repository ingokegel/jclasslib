package org.gjt.jclasslib.bytecode;

/** Holds a single match-offset pair */
public class MatchOffsetPair {
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
