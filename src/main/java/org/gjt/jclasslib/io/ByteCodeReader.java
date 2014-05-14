/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io;

import org.gjt.jclasslib.bytecode.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
    Converts code to a list of instructions as defined in the package
    <tt>org.gjt.jclasslib.code</tt>.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ByteCodeReader {

    private ByteCodeReader() {
    }
    
    /**
        Converts the code to a list of instructions.
        @param code the code as an array of bytes from which to read the instructions
        @return the <tt>java.util.List</tt> with the instructions
        @throws IOException if an exception occurs with the code
     */
    public static ArrayList<AbstractInstruction> readByteCode(byte[] code) throws IOException {
        return readByteCode(code, null);
    }

    /**
        Converts the code to a list of instructions.
        @param code the code as an array of bytes from which to read the instructions
        @param prependInstructions an array of instructions that is prepended, may be <tt>null</tt>
        @return the <tt>java.util.List</tt> with the instructions
        @throws IOException if an exception occurs with the code
     */
    public static ArrayList<AbstractInstruction> readByteCode(byte[] code, AbstractInstruction[] prependInstructions)
        throws IOException {

        ByteCodeInputStream bcis = new ByteCodeInputStream(
                                        new ByteArrayInputStream(code)
                                    );
        
        ArrayList<AbstractInstruction> instructions = new ArrayList<AbstractInstruction>();
        if (prependInstructions != null) {
            Collections.addAll(instructions, prependInstructions);
        }
        
        boolean wide = false;
        AbstractInstruction currentInstruction;
        while (bcis.getBytesRead() < code.length) {
            currentInstruction = readNextInstruction(bcis, wide);
            wide = (currentInstruction.getOpcode() == Opcode.WIDE);
            instructions.add(currentInstruction);
        }
        
        return instructions;
    }
    
    private static AbstractInstruction readNextInstruction(ByteCodeInputStream bcis, boolean wide)
        throws IOException
    {
        AbstractInstruction instruction;

        int bytecode = bcis.readUnsignedByte();
        Opcode opcode = Opcode.getFromBytecode(bytecode);
        if (opcode == null) {
            throw new IOException("invalid opcode 0x" + Integer.toHexString(bytecode));
        }

        switch (opcode) {
            
            case WIDE:
            case NOP:
            case ACONST_NULL:
            case ICONST_M1:
            case ICONST_0:
            case ICONST_1:
            case ICONST_2:
            case ICONST_3:
            case ICONST_4:
            case ICONST_5:
            case LCONST_0:
            case LCONST_1:
            case FCONST_0:
            case FCONST_1:
            case FCONST_2:
            case DCONST_0:
            case DCONST_1:
            case ILOAD_0:
            case ILOAD_1:
            case ILOAD_2:
            case ILOAD_3:
            case LLOAD_0:
            case LLOAD_1:
            case LLOAD_2:
            case LLOAD_3:
            case FLOAD_0:
            case FLOAD_1:
            case FLOAD_2:
            case FLOAD_3:
            case DLOAD_0:
            case DLOAD_1:
            case DLOAD_2:
            case DLOAD_3:
            case ALOAD_0:
            case ALOAD_1:
            case ALOAD_2:
            case ALOAD_3:
            case IALOAD:
            case LALOAD:
            case FALOAD:
            case DALOAD:
            case AALOAD:
            case BALOAD:
            case CALOAD:
            case SALOAD:
            case ISTORE_0:
            case ISTORE_1:
            case ISTORE_2:
            case ISTORE_3:
            case LSTORE_0:
            case LSTORE_1:
            case LSTORE_2:
            case LSTORE_3:
            case FSTORE_0:
            case FSTORE_1:
            case FSTORE_2:
            case FSTORE_3:
            case DSTORE_0:
            case DSTORE_1:
            case DSTORE_2:
            case DSTORE_3:
            case ASTORE_0:
            case ASTORE_1:
            case ASTORE_2:
            case ASTORE_3:
            case IASTORE:
            case LASTORE:
            case FASTORE:
            case DASTORE:
            case AASTORE:
            case BASTORE:
            case CASTORE:
            case SASTORE:
            case POP:
            case POP2:
            case DUP:
            case DUP_X1:
            case DUP_X2:
            case DUP2:
            case DUP2_X1:
            case DUP2_X2:
            case SWAP:
            case IADD:
            case LADD:
            case FADD:
            case DADD:
            case ISUB:
            case LSUB:
            case FSUB:
            case DSUB:
            case IMUL:
            case LMUL:
            case FMUL:
            case DMUL:
            case IDIV:
            case LDIV:
            case FDIV:
            case DDIV:
            case IREM:
            case LREM:
            case FREM:
            case DREM:
            case INEG:
            case LNEG:
            case FNEG:
            case DNEG:
            case ISHL:
            case LSHL:
            case ISHR:
            case LSHR:
            case IUSHR:
            case LUSHR:
            case IAND:
            case LAND:
            case IOR:
            case LOR:
            case IXOR:
            case LXOR:
            case I2L:
            case I2F:
            case I2D:
            case L2I:
            case L2F:
            case L2D:
            case F2I:
            case F2L:
            case F2D:
            case D2I:
            case D2L:
            case D2F:
            case I2B:
            case I2C:
            case I2S:
            case LCMP:
            case FCMPL:
            case FCMPG:
            case DCMPL:
            case DCMPG:
            case IRETURN:
            case LRETURN:
            case FRETURN:
            case DRETURN:
            case ARETURN:
            case RETURN:
            case ARRAYLENGTH:
            case ATHROW:
            case MONITORENTER:
            case MONITOREXIT:
            case BREAKPOINT:
            case IMPDEP1:
            case IMPDEP2:
                
                instruction = new SimpleInstruction(opcode);
                break;

            case BIPUSH:
            case LDC:
            case ILOAD:  // subject to wide
            case LLOAD:  // subject to wide
            case FLOAD:  // subject to wide
            case DLOAD:  // subject to wide
            case ALOAD:  // subject to wide
            case ISTORE: // subject to wide
            case LSTORE: // subject to wide
            case FSTORE: // subject to wide
            case DSTORE: // subject to wide
            case ASTORE: // subject to wide
            case RET:    // subject to wide
            case NEWARRAY:

                instruction = new ImmediateByteInstruction(opcode, wide);
                break;

            case LDC_W:
            case LDC2_W:
            case GETSTATIC:
            case PUTSTATIC:
            case GETFIELD:
            case PUTFIELD:
            case INVOKEVIRTUAL:
            case INVOKESPECIAL:
            case INVOKESTATIC:
            case NEW:
            case ANEWARRAY:
            case CHECKCAST:
            case INSTANCEOF:
            case SIPUSH: // the only immediate short instruction that does
                                // not have an immediate constant pool reference

                instruction = new ImmediateShortInstruction(opcode);
                break;

            case IFEQ:
            case IFNE:
            case IFLT:
            case IFGE:
            case IFGT:
            case IFLE:
            case IF_ICMPEQ:
            case IF_ICMPNE:
            case IF_ICMPLT:
            case IF_ICMPGE:
            case IF_ICMPGT:
            case IF_ICMPLE:
            case IF_ACMPEQ:
            case IF_ACMPNE:
            case GOTO:
            case JSR:
            case IFNULL:
            case IFNONNULL:

                instruction = new BranchInstruction(opcode);
                break;

            case GOTO_W:
            case JSR_W:

                instruction = new WideBranchInstruction(opcode);
                break;
                
            case IINC: // subject to wide

                instruction = new IncrementInstruction(opcode, wide);
                break;
                
            case TABLESWITCH:

                instruction = new TableSwitchInstruction(opcode);
                break;
                
            case LOOKUPSWITCH:

                instruction = new LookupSwitchInstruction(opcode);
                break;
                
            case INVOKEINTERFACE:

                instruction = new InvokeInterfaceInstruction(opcode);
                break;

            case INVOKEDYNAMIC:

                instruction = new InvokeDynamicInstruction(opcode);
                break;

            case MULTIANEWARRAY:
            
                instruction = new MultianewarrayInstruction(opcode);
                break;
                
            default:
                throw new IOException("unhandled opcode " + opcode);
        }
        
        instruction.read(bcis);
        return instruction;
    }
    
}
