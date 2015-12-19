package org.gjt.jclasslib.bytecode

abstract class AbstractBranchInstruction(opcode: Opcode) : AbstractInstruction(opcode) {

    constructor(opcode: Opcode, branchOffset: Int) : this(opcode) {
        this.branchOffset = branchOffset
    }

    /**
     * Relative offset of the branch of this instruction.
     */
    var branchOffset: Int = 0

}
