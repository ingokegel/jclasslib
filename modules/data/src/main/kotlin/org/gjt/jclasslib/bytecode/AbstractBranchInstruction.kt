package org.gjt.jclasslib.bytecode

/**
 * Base class for instructions with a branch offset.
 * @property branchOffset Relative offset of the branch of this instruction.
 */
abstract class AbstractBranchInstruction(opcode: Opcode, var branchOffset: Int = 0) : Instruction(opcode)
