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

/**
    Converts code to a list of instructions as defined in the package
    <tt>org.gjt.jclasslib.code</tt>.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.8 $ $Date: 2010-07-26 14:00:11 $
*/
public class ByteCodeReader implements Opcodes {

    private ByteCodeReader() {
    }
    
    /**
        Converts the code to a list of instructions.
        @param code the code as an array of bytes from which to read the instructions
        @return the <tt>java.util.List</tt> with the instructions
        @throws IOException if an exception occurs with the code
     */
    public static ArrayList readByteCode(byte[] code) throws IOException {
        return readByteCode(code, null);
    }

    /**
        Converts the code to a list of instructions.
        @param code the code as an array of bytes from which to read the instructions
        @param prependInstructions an array of instructions that is prepended, may be <tt>null</tt>
        @return the <tt>java.util.List</tt> with the instructions
        @throws IOException if an exception occurs with the code
     */
    public static ArrayList readByteCode(byte[] code, AbstractInstruction[] prependInstructions)
        throws IOException {

        ByteCodeInputStream bcis = new ByteCodeInputStream(
                                        new ByteArrayInputStream(code)
                                    );
        
        ArrayList instructions = new ArrayList();
        if (prependInstructions != null) {
            for (int i = 0; i < prependInstructions.length; i++) {
                instructions.add(prependInstructions[i]);
            }
        }
        
        boolean wide = false;
        AbstractInstruction currentInstruction;
        while (bcis.getBytesRead() < code.length) {
            currentInstruction = readNextInstruction(bcis, wide);
            wide = (currentInstruction.getOpcode() == OPCODE_WIDE);
            instructions.add(currentInstruction);
        }
        
        return instructions;
    }
    
    private static AbstractInstruction readNextInstruction(ByteCodeInputStream bcis, boolean wide)
        throws IOException
    {
        AbstractInstruction instruction;

        int opcode = bcis.readUnsignedByte();

        switch (opcode) {
            
            case OPCODE_WIDE:
            case OPCODE_NOP:
            case OPCODE_ACONST_NULL:
            case OPCODE_ICONST_M1:
            case OPCODE_ICONST_0:
            case OPCODE_ICONST_1:
            case OPCODE_ICONST_2:
            case OPCODE_ICONST_3:
            case OPCODE_ICONST_4:
            case OPCODE_ICONST_5:
            case OPCODE_LCONST_0:
            case OPCODE_LCONST_1:
            case OPCODE_FCONST_0:
            case OPCODE_FCONST_1:
            case OPCODE_FCONST_2:
            case OPCODE_DCONST_0:
            case OPCODE_DCONST_1:
            case OPCODE_ILOAD_0:
            case OPCODE_ILOAD_1:
            case OPCODE_ILOAD_2:
            case OPCODE_ILOAD_3:
            case OPCODE_LLOAD_0:
            case OPCODE_LLOAD_1:
            case OPCODE_LLOAD_2:
            case OPCODE_LLOAD_3:
            case OPCODE_FLOAD_0:
            case OPCODE_FLOAD_1:
            case OPCODE_FLOAD_2:
            case OPCODE_FLOAD_3:
            case OPCODE_DLOAD_0:
            case OPCODE_DLOAD_1:
            case OPCODE_DLOAD_2:
            case OPCODE_DLOAD_3:
            case OPCODE_ALOAD_0:
            case OPCODE_ALOAD_1:
            case OPCODE_ALOAD_2:
            case OPCODE_ALOAD_3:
            case OPCODE_IALOAD:
            case OPCODE_LALOAD:
            case OPCODE_FALOAD:
            case OPCODE_DALOAD:
            case OPCODE_AALOAD:
            case OPCODE_BALOAD:
            case OPCODE_CALOAD:
            case OPCODE_SALOAD:
            case OPCODE_ISTORE_0:
            case OPCODE_ISTORE_1:
            case OPCODE_ISTORE_2:
            case OPCODE_ISTORE_3:
            case OPCODE_LSTORE_0:
            case OPCODE_LSTORE_1:
            case OPCODE_LSTORE_2:
            case OPCODE_LSTORE_3:
            case OPCODE_FSTORE_0:
            case OPCODE_FSTORE_1:
            case OPCODE_FSTORE_2:
            case OPCODE_FSTORE_3:
            case OPCODE_DSTORE_0:
            case OPCODE_DSTORE_1:
            case OPCODE_DSTORE_2:
            case OPCODE_DSTORE_3:
            case OPCODE_ASTORE_0:
            case OPCODE_ASTORE_1:
            case OPCODE_ASTORE_2:
            case OPCODE_ASTORE_3:
            case OPCODE_IASTORE:
            case OPCODE_LASTORE:
            case OPCODE_FASTORE:
            case OPCODE_DASTORE:
            case OPCODE_AASTORE:
            case OPCODE_BASTORE:
            case OPCODE_CASTORE:
            case OPCODE_SASTORE:
            case OPCODE_POP:
            case OPCODE_POP2:
            case OPCODE_DUP:
            case OPCODE_DUP_X1:
            case OPCODE_DUP_X2:
            case OPCODE_DUP2:
            case OPCODE_DUP2_X1:
            case OPCODE_DUP2_X2:
            case OPCODE_SWAP:
            case OPCODE_IADD:
            case OPCODE_LADD:
            case OPCODE_FADD:
            case OPCODE_DADD:
            case OPCODE_ISUB:
            case OPCODE_LSUB:
            case OPCODE_FSUB:
            case OPCODE_DSUB:
            case OPCODE_IMUL:
            case OPCODE_LMUL:
            case OPCODE_FMUL:
            case OPCODE_DMUL:
            case OPCODE_IDIV:
            case OPCODE_LDIV:
            case OPCODE_FDIV:
            case OPCODE_DDIV:
            case OPCODE_IREM:
            case OPCODE_LREM:
            case OPCODE_FREM:
            case OPCODE_DREM:
            case OPCODE_INEG:
            case OPCODE_LNEG:
            case OPCODE_FNEG:
            case OPCODE_DNEG:
            case OPCODE_ISHL:
            case OPCODE_LSHL:
            case OPCODE_ISHR:
            case OPCODE_LSHR:
            case OPCODE_IUSHR:
            case OPCODE_LUSHR:
            case OPCODE_IAND:
            case OPCODE_LAND:
            case OPCODE_IOR:
            case OPCODE_LOR:
            case OPCODE_IXOR:
            case OPCODE_LXOR:
            case OPCODE_I2L:
            case OPCODE_I2F:
            case OPCODE_I2D:
            case OPCODE_L2I:
            case OPCODE_L2F:
            case OPCODE_L2D:
            case OPCODE_F2I:
            case OPCODE_F2L:
            case OPCODE_F2D:
            case OPCODE_D2I:
            case OPCODE_D2L:
            case OPCODE_D2F:
            case OPCODE_I2B:
            case OPCODE_I2C:
            case OPCODE_I2S:
            case OPCODE_LCMP:
            case OPCODE_FCMPL:
            case OPCODE_FCMPG:
            case OPCODE_DCMPL:
            case OPCODE_DCMPG:
            case OPCODE_IRETURN:
            case OPCODE_LRETURN:
            case OPCODE_FRETURN:
            case OPCODE_DRETURN:
            case OPCODE_ARETURN:
            case OPCODE_RETURN:
            case OPCODE_ARRAYLENGTH:
            case OPCODE_ATHROW:
            case OPCODE_MONITORENTER:
            case OPCODE_MONITOREXIT:
            case OPCODE_BREAKPOINT:
            case OPCODE_IMPDEP1:
            case OPCODE_IMPDEP2:
                
                instruction = new SimpleInstruction(opcode);
                break;

            case OPCODE_BIPUSH:
            case OPCODE_LDC:
            case OPCODE_ILOAD:  // subject to wide
            case OPCODE_LLOAD:  // subject to wide
            case OPCODE_FLOAD:  // subject to wide
            case OPCODE_DLOAD:  // subject to wide
            case OPCODE_ALOAD:  // subject to wide
            case OPCODE_ISTORE: // subject to wide
            case OPCODE_LSTORE: // subject to wide
            case OPCODE_FSTORE: // subject to wide
            case OPCODE_DSTORE: // subject to wide
            case OPCODE_ASTORE: // subject to wide
            case OPCODE_RET:    // subject to wide
            case OPCODE_NEWARRAY:

                instruction = new ImmediateByteInstruction(opcode, wide);
                break;

            case OPCODE_LDC_W:
            case OPCODE_LDC2_W:
            case OPCODE_GETSTATIC:
            case OPCODE_PUTSTATIC:
            case OPCODE_GETFIELD:
            case OPCODE_PUTFIELD:
            case OPCODE_INVOKEVIRTUAL:
            case OPCODE_INVOKESPECIAL:
            case OPCODE_INVOKESTATIC:
            case OPCODE_NEW:
            case OPCODE_ANEWARRAY:
            case OPCODE_CHECKCAST:
            case OPCODE_INSTANCEOF:
            case OPCODE_SIPUSH: // the only immediate short instruction that does
                                // not have an immediate constant pool reference

                instruction = new ImmediateShortInstruction(opcode);
                break;

            case OPCODE_IFEQ:
            case OPCODE_IFNE:
            case OPCODE_IFLT:
            case OPCODE_IFGE:
            case OPCODE_IFGT:
            case OPCODE_IFLE:
            case OPCODE_IF_ICMPEQ:
            case OPCODE_IF_ICMPNE:
            case OPCODE_IF_ICMPLT:
            case OPCODE_IF_ICMPGE:
            case OPCODE_IF_ICMPGT:
            case OPCODE_IF_ICMPLE:
            case OPCODE_IF_ACMPEQ:
            case OPCODE_IF_ACMPNE:
            case OPCODE_GOTO:
            case OPCODE_JSR:
            case OPCODE_IFNULL:
            case OPCODE_IFNONNULL:

                instruction = new BranchInstruction(opcode);
                break;

            case OPCODE_GOTO_W:
            case OPCODE_JSR_W:

                instruction = new WideBranchInstruction(opcode);
                break;
                
            case OPCODE_IINC: // subject to wide

                instruction = new IncrementInstruction(opcode, wide);
                break;
                
            case OPCODE_TABLESWITCH:

                instruction = new TableSwitchInstruction(opcode);
                break;
                
            case OPCODE_LOOKUPSWITCH:

                instruction = new LookupSwitchInstruction(opcode);
                break;
                
            case OPCODE_INVOKEINTERFACE:

                instruction = new InvokeInterfaceInstruction(opcode);
                break;

            case OPCODE_INVOKEDYNAMIC:

                instruction = new InvokeDynamicInstruction(opcode);
                break;

            case OPCODE_MULTIANEWARRAY:
            
                instruction = new MultianewarrayInstruction(opcode);
                break;
                
            default:
                throw new IOException("invalid opcode 0x" + Integer.toHexString(opcode));
        }
        
        instruction.read(bcis);
        return instruction;
    }
    
}
