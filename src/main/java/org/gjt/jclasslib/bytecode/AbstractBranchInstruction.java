package org.gjt.jclasslib.bytecode;

public abstract class AbstractBranchInstruction extends AbstractInstruction {

    private int branchOffset;

    /**
        Constructor.
        @param opcode the opcode.
     */
    protected AbstractBranchInstruction(Opcode opcode) {
        super(opcode);
    }

    /**
        Constructor.
        @param opcode the opcode.
        @param branchOffset the branch offset.
     */
    protected AbstractBranchInstruction(Opcode opcode, int branchOffset) {
        super(opcode);
        this.branchOffset = branchOffset;
    }

    /**
        Get the relative offset of the branch of this instruction.
        @return the offset
     */
    public int getBranchOffset() {
        return branchOffset;
    }

    /**
        Set the relative offset of the branch of this instruction.
        @param branchOffset the offset
     */
    public void setBranchOffset(int branchOffset) {
        this.branchOffset = branchOffset;
    }


}
