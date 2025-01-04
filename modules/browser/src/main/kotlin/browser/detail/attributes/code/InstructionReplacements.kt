/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.bytecode.*

fun getReplacementOpcodes(instruction: Instruction): List<Opcode> = when (instruction) {
    is SimpleInstruction -> simpleInstructions
    is SimpleImmediateByteInstruction -> simpleImmediateBytecodeInstructions
    is SimpleImmediateShortInstruction -> simpleImmediateShortInstructions
    is BranchInstruction -> branchInstructions
    is WideBranchInstruction -> wideBranchInstructions
    else -> emptyList()
}.minus(instruction.opcode)

fun getStackCompatibleReplacementOpcodes(instruction: Instruction): List<Opcode> {
    val stackChanges = getStackChanges(instruction.opcode)
    return if (stackChanges != null) {
        getReplacementOpcodes(instruction).filter { opcode ->
            getStackChanges(opcode)?.isReplacementCompatibleWith(stackChanges) == true
        }
    } else {
        emptyList()
    }
}

private val simpleInstructions = listOf(
        Opcode.WIDE,
        Opcode.NOP,
        Opcode.ACONST_NULL,
        Opcode.ICONST_M1,
        Opcode.ICONST_0,
        Opcode.ICONST_1,
        Opcode.ICONST_2,
        Opcode.ICONST_3,
        Opcode.ICONST_4,
        Opcode.ICONST_5,
        Opcode.LCONST_0,
        Opcode.LCONST_1,
        Opcode.FCONST_0,
        Opcode.FCONST_1,
        Opcode.FCONST_2,
        Opcode.DCONST_0,
        Opcode.DCONST_1,
        Opcode.ILOAD_0,
        Opcode.ILOAD_1,
        Opcode.ILOAD_2,
        Opcode.ILOAD_3,
        Opcode.LLOAD_0,
        Opcode.LLOAD_1,
        Opcode.LLOAD_2,
        Opcode.LLOAD_3,
        Opcode.FLOAD_0,
        Opcode.FLOAD_1,
        Opcode.FLOAD_2,
        Opcode.FLOAD_3,
        Opcode.DLOAD_0,
        Opcode.DLOAD_1,
        Opcode.DLOAD_2,
        Opcode.DLOAD_3,
        Opcode.ALOAD_0,
        Opcode.ALOAD_1,
        Opcode.ALOAD_2,
        Opcode.ALOAD_3,
        Opcode.IALOAD,
        Opcode.LALOAD,
        Opcode.FALOAD,
        Opcode.DALOAD,
        Opcode.AALOAD,
        Opcode.BALOAD,
        Opcode.CALOAD,
        Opcode.SALOAD,
        Opcode.ISTORE_0,
        Opcode.ISTORE_1,
        Opcode.ISTORE_2,
        Opcode.ISTORE_3,
        Opcode.LSTORE_0,
        Opcode.LSTORE_1,
        Opcode.LSTORE_2,
        Opcode.LSTORE_3,
        Opcode.FSTORE_0,
        Opcode.FSTORE_1,
        Opcode.FSTORE_2,
        Opcode.FSTORE_3,
        Opcode.DSTORE_0,
        Opcode.DSTORE_1,
        Opcode.DSTORE_2,
        Opcode.DSTORE_3,
        Opcode.ASTORE_0,
        Opcode.ASTORE_1,
        Opcode.ASTORE_2,
        Opcode.ASTORE_3,
        Opcode.IASTORE,
        Opcode.LASTORE,
        Opcode.FASTORE,
        Opcode.DASTORE,
        Opcode.AASTORE,
        Opcode.BASTORE,
        Opcode.CASTORE,
        Opcode.SASTORE,
        Opcode.POP,
        Opcode.POP2,
        Opcode.DUP,
        Opcode.DUP_X1,
        Opcode.DUP_X2,
        Opcode.DUP2,
        Opcode.DUP2_X1,
        Opcode.DUP2_X2,
        Opcode.SWAP,
        Opcode.IADD,
        Opcode.LADD,
        Opcode.FADD,
        Opcode.DADD,
        Opcode.ISUB,
        Opcode.LSUB,
        Opcode.FSUB,
        Opcode.DSUB,
        Opcode.IMUL,
        Opcode.LMUL,
        Opcode.FMUL,
        Opcode.DMUL,
        Opcode.IDIV,
        Opcode.LDIV,
        Opcode.FDIV,
        Opcode.DDIV,
        Opcode.IREM,
        Opcode.LREM,
        Opcode.FREM,
        Opcode.DREM,
        Opcode.INEG,
        Opcode.LNEG,
        Opcode.FNEG,
        Opcode.DNEG,
        Opcode.ISHL,
        Opcode.LSHL,
        Opcode.ISHR,
        Opcode.LSHR,
        Opcode.IUSHR,
        Opcode.LUSHR,
        Opcode.IAND,
        Opcode.LAND,
        Opcode.IOR,
        Opcode.LOR,
        Opcode.IXOR,
        Opcode.LXOR,
        Opcode.I2L,
        Opcode.I2F,
        Opcode.I2D,
        Opcode.L2I,
        Opcode.L2F,
        Opcode.L2D,
        Opcode.F2I,
        Opcode.F2L,
        Opcode.F2D,
        Opcode.D2I,
        Opcode.D2L,
        Opcode.D2F,
        Opcode.I2B,
        Opcode.I2C,
        Opcode.I2S,
        Opcode.LCMP,
        Opcode.FCMPL,
        Opcode.FCMPG,
        Opcode.DCMPL,
        Opcode.DCMPG,
        Opcode.IRETURN,
        Opcode.LRETURN,
        Opcode.FRETURN,
        Opcode.DRETURN,
        Opcode.ARETURN,
        Opcode.RETURN,
        Opcode.ARRAYLENGTH,
        Opcode.ATHROW,
        Opcode.MONITORENTER,
        Opcode.MONITOREXIT,
        Opcode.BREAKPOINT,
        Opcode.IMPDEP1,
        Opcode.IMPDEP2
)

private val simpleImmediateBytecodeInstructions = listOf(
        Opcode.BIPUSH,
        Opcode.LDC,
        Opcode.ILOAD,
        Opcode.LLOAD,
        Opcode.FLOAD,
        Opcode.DLOAD,
        Opcode.ALOAD,
        Opcode.ISTORE,
        Opcode.LSTORE,
        Opcode.FSTORE,
        Opcode.DSTORE,
        Opcode.ASTORE,
        Opcode.RET,
        Opcode.NEWARRAY
)

private val simpleImmediateShortInstructions = listOf(
        Opcode.LDC_W,
        Opcode.LDC2_W,
        Opcode.GETSTATIC,
        Opcode.PUTSTATIC,
        Opcode.GETFIELD,
        Opcode.PUTFIELD,
        Opcode.INVOKEVIRTUAL,
        Opcode.INVOKESPECIAL,
        Opcode.INVOKESTATIC,
        Opcode.NEW,
        Opcode.ANEWARRAY,
        Opcode.CHECKCAST,
        Opcode.INSTANCEOF,
        Opcode.SIPUSH
)

private val branchInstructions = listOf(
        Opcode.IFEQ,
        Opcode.IFNE,
        Opcode.IFLT,
        Opcode.IFGE,
        Opcode.IFGT,
        Opcode.IFLE,
        Opcode.IF_ICMPEQ,
        Opcode.IF_ICMPNE,
        Opcode.IF_ICMPLT,
        Opcode.IF_ICMPGE,
        Opcode.IF_ICMPGT,
        Opcode.IF_ICMPLE,
        Opcode.IF_ACMPEQ,
        Opcode.IF_ACMPNE,
        Opcode.GOTO,
        Opcode.JSR,
        Opcode.IFNULL,
        Opcode.IFNONNULL
)

private val wideBranchInstructions = listOf(
        Opcode.GOTO_W,
        Opcode.JSR_W
)