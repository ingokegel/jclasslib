/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.bytecode;

/**
    Defines utility methods for dealing with opcodes.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2002-02-27 16:47:43 $
*/
public class OpcodesUtil implements Opcodes {

    private static String[] opcodeVerbose;
    
    static {
        
        opcodeVerbose = new String[256];
        
        opcodeVerbose[OPCODE_NOP] = "nop";
        opcodeVerbose[OPCODE_ACONST_NULL] = "aconst_null";
        opcodeVerbose[OPCODE_ICONST_M1] = "iconst_m1";
        opcodeVerbose[OPCODE_ICONST_0] = "iconst_0";
        opcodeVerbose[OPCODE_ICONST_1] = "iconst_1";
        opcodeVerbose[OPCODE_ICONST_2] = "iconst_2";
        opcodeVerbose[OPCODE_ICONST_3] = "iconst_3";
        opcodeVerbose[OPCODE_ICONST_4] = "iconst_4";
        opcodeVerbose[OPCODE_ICONST_5] = "iconst_5";
        opcodeVerbose[OPCODE_LCONST_0] = "lconst_0";
        opcodeVerbose[OPCODE_LCONST_1] = "lconst_1";
        opcodeVerbose[OPCODE_FCONST_0] = "fconst_0";
        opcodeVerbose[OPCODE_FCONST_1] = "fconst_1";
        opcodeVerbose[OPCODE_FCONST_2] = "fconst_2";
        opcodeVerbose[OPCODE_DCONST_0] = "dconst_0";
        opcodeVerbose[OPCODE_DCONST_1] = "dconst_1";
        opcodeVerbose[OPCODE_BIPUSH] = "bipush";
        opcodeVerbose[OPCODE_SIPUSH] = "sipush";
        opcodeVerbose[OPCODE_LDC] = "ldc";
        opcodeVerbose[OPCODE_LDC_W] = "ldc_w";
        opcodeVerbose[OPCODE_LDC2_W] = "ldc2_w";
        opcodeVerbose[OPCODE_ILOAD] = "iload";
        opcodeVerbose[OPCODE_LLOAD] = "lload";
        opcodeVerbose[OPCODE_FLOAD] = "fload";
        opcodeVerbose[OPCODE_DLOAD] = "dload";
        opcodeVerbose[OPCODE_ALOAD] = "aload";
        opcodeVerbose[OPCODE_ILOAD_0] = "iload_0";
        opcodeVerbose[OPCODE_ILOAD_1] = "iload_1";
        opcodeVerbose[OPCODE_ILOAD_2] = "iload_2";
        opcodeVerbose[OPCODE_ILOAD_3] = "iload_3";
        opcodeVerbose[OPCODE_LLOAD_0] = "lload_0";
        opcodeVerbose[OPCODE_LLOAD_1] = "lload_1";
        opcodeVerbose[OPCODE_LLOAD_2] = "lload_2";
        opcodeVerbose[OPCODE_LLOAD_3] = "lload_3";
        opcodeVerbose[OPCODE_FLOAD_0] = "fload_0";
        opcodeVerbose[OPCODE_FLOAD_1] = "fload_1";
        opcodeVerbose[OPCODE_FLOAD_2] = "fload_2";
        opcodeVerbose[OPCODE_FLOAD_3] = "fload_3";
        opcodeVerbose[OPCODE_DLOAD_0] = "dload_0";
        opcodeVerbose[OPCODE_DLOAD_1] = "dload_1";
        opcodeVerbose[OPCODE_DLOAD_2] = "dload_2";
        opcodeVerbose[OPCODE_DLOAD_3] = "dload_3";
        opcodeVerbose[OPCODE_ALOAD_0] = "aload_0";
        opcodeVerbose[OPCODE_ALOAD_1] = "aload_1";
        opcodeVerbose[OPCODE_ALOAD_2] = "aload_2";
        opcodeVerbose[OPCODE_ALOAD_3] = "aload_3";
        opcodeVerbose[OPCODE_IALOAD] = "iaload";
        opcodeVerbose[OPCODE_LALOAD] = "laload";
        opcodeVerbose[OPCODE_FALOAD] = "faload";
        opcodeVerbose[OPCODE_DALOAD] = "daload";
        opcodeVerbose[OPCODE_AALOAD] = "aaload";
        opcodeVerbose[OPCODE_BALOAD] = "baload";
        opcodeVerbose[OPCODE_CALOAD] = "caload";
        opcodeVerbose[OPCODE_SALOAD] = "saload";
        opcodeVerbose[OPCODE_ISTORE] = "istore";
        opcodeVerbose[OPCODE_LSTORE] = "lstore";
        opcodeVerbose[OPCODE_FSTORE] = "fstore";
        opcodeVerbose[OPCODE_DSTORE] = "dstore";
        opcodeVerbose[OPCODE_ASTORE] = "astore";
        opcodeVerbose[OPCODE_ISTORE_0] = "istore_0";
        opcodeVerbose[OPCODE_ISTORE_1] = "istore_1";
        opcodeVerbose[OPCODE_ISTORE_2] = "istore_2";
        opcodeVerbose[OPCODE_ISTORE_3] = "istore_3";
        opcodeVerbose[OPCODE_LSTORE_0] = "lstore_0";
        opcodeVerbose[OPCODE_LSTORE_1] = "lstore_1";
        opcodeVerbose[OPCODE_LSTORE_2] = "lstore_2";
        opcodeVerbose[OPCODE_LSTORE_3] = "lstore_3";
        opcodeVerbose[OPCODE_FSTORE_0] = "fstore_0";
        opcodeVerbose[OPCODE_FSTORE_1] = "fstore_1";
        opcodeVerbose[OPCODE_FSTORE_2] = "fstore_2";
        opcodeVerbose[OPCODE_FSTORE_3] = "fstore_3";
        opcodeVerbose[OPCODE_DSTORE_0] = "dstore_0";
        opcodeVerbose[OPCODE_DSTORE_1] = "dstore_1";
        opcodeVerbose[OPCODE_DSTORE_2] = "dstore_2";
        opcodeVerbose[OPCODE_DSTORE_3] = "dstore_3";
        opcodeVerbose[OPCODE_ASTORE_0] = "astore_0";
        opcodeVerbose[OPCODE_ASTORE_1] = "astore_1";
        opcodeVerbose[OPCODE_ASTORE_2] = "astore_2";
        opcodeVerbose[OPCODE_ASTORE_3] = "astore_3";
        opcodeVerbose[OPCODE_IASTORE] = "iastore";
        opcodeVerbose[OPCODE_LASTORE] = "lastore";
        opcodeVerbose[OPCODE_FASTORE] = "fastore";
        opcodeVerbose[OPCODE_DASTORE] = "dastore";
        opcodeVerbose[OPCODE_AASTORE] = "aastore";
        opcodeVerbose[OPCODE_BASTORE] = "bastore";
        opcodeVerbose[OPCODE_CASTORE] = "castore";
        opcodeVerbose[OPCODE_SASTORE] = "sastore";
        opcodeVerbose[OPCODE_POP] = "pop";
        opcodeVerbose[OPCODE_POP2] = "pop2";
        opcodeVerbose[OPCODE_DUP] = "dup";
        opcodeVerbose[OPCODE_DUP_X1] = "dup_x1";
        opcodeVerbose[OPCODE_DUP_X2] = "dup_x2";
        opcodeVerbose[OPCODE_DUP2] = "dup2";
        opcodeVerbose[OPCODE_DUP2_X1] = "dup2_x1";
        opcodeVerbose[OPCODE_DUP2_X2] = "dup2_x2";
        opcodeVerbose[OPCODE_SWAP] = "swap";
        opcodeVerbose[OPCODE_IADD] = "iadd";
        opcodeVerbose[OPCODE_LADD] = "ladd";
        opcodeVerbose[OPCODE_FADD] = "fadd";
        opcodeVerbose[OPCODE_DADD] = "dadd";
        opcodeVerbose[OPCODE_ISUB] = "isub";
        opcodeVerbose[OPCODE_LSUB] = "lsub";
        opcodeVerbose[OPCODE_FSUB] = "fsub";
        opcodeVerbose[OPCODE_DSUB] = "dsub";
        opcodeVerbose[OPCODE_IMUL] = "imul";
        opcodeVerbose[OPCODE_LMUL] = "lmul";
        opcodeVerbose[OPCODE_FMUL] = "fmul";
        opcodeVerbose[OPCODE_DMUL] = "dmul";
        opcodeVerbose[OPCODE_IDIV] = "idiv";
        opcodeVerbose[OPCODE_LDIV] = "ldiv";
        opcodeVerbose[OPCODE_FDIV] = "fdiv";
        opcodeVerbose[OPCODE_DDIV] = "ddiv";
        opcodeVerbose[OPCODE_IREM] = "irem";
        opcodeVerbose[OPCODE_LREM] = "lrem";
        opcodeVerbose[OPCODE_FREM] = "frem";
        opcodeVerbose[OPCODE_DREM] = "drem";
        opcodeVerbose[OPCODE_INEG] = "ineg";
        opcodeVerbose[OPCODE_LNEG] = "lneg";
        opcodeVerbose[OPCODE_FNEG] = "fneg";
        opcodeVerbose[OPCODE_DNEG] = "dneg";
        opcodeVerbose[OPCODE_ISHL] = "ishl";
        opcodeVerbose[OPCODE_LSHL] = "lshl";
        opcodeVerbose[OPCODE_ISHR] = "ishr";
        opcodeVerbose[OPCODE_LSHR] = "lshr";
        opcodeVerbose[OPCODE_IUSHR] = "iushr";
        opcodeVerbose[OPCODE_LUSHR] = "lushr";
        opcodeVerbose[OPCODE_IAND] = "iand";
        opcodeVerbose[OPCODE_LAND] = "land";
        opcodeVerbose[OPCODE_IOR] = "ior";
        opcodeVerbose[OPCODE_LOR] = "lor";
        opcodeVerbose[OPCODE_IXOR] = "ixor";
        opcodeVerbose[OPCODE_LXOR] = "lxor";
        opcodeVerbose[OPCODE_IINC] = "iinc";
        opcodeVerbose[OPCODE_I2L] = "i2l";
        opcodeVerbose[OPCODE_I2F] = "i2f";
        opcodeVerbose[OPCODE_I2D] = "i2d";
        opcodeVerbose[OPCODE_L2I] = "l2i";
        opcodeVerbose[OPCODE_L2F] = "l2f";
        opcodeVerbose[OPCODE_L2D] = "l2d";
        opcodeVerbose[OPCODE_F2I] = "f2i";
        opcodeVerbose[OPCODE_F2L] = "f2l";
        opcodeVerbose[OPCODE_F2D] = "f2d";
        opcodeVerbose[OPCODE_D2I] = "d2i";
        opcodeVerbose[OPCODE_D2L] = "d2l";
        opcodeVerbose[OPCODE_D2F] = "d2f";
        opcodeVerbose[OPCODE_I2B] = "i2b";
        opcodeVerbose[OPCODE_I2C] = "i2c";
        opcodeVerbose[OPCODE_I2S] = "i2s";
        opcodeVerbose[OPCODE_LCMP] = "lcmp";
        opcodeVerbose[OPCODE_FCMPL] = "fcmpl";
        opcodeVerbose[OPCODE_FCMPG] = "fcmpg";
        opcodeVerbose[OPCODE_DCMPL] = "dcmpl";
        opcodeVerbose[OPCODE_DCMPG] = "dcmpg";
        opcodeVerbose[OPCODE_IFEQ] = "ifeq";
        opcodeVerbose[OPCODE_IFNE] = "ifne";
        opcodeVerbose[OPCODE_IFLT] = "iflt";
        opcodeVerbose[OPCODE_IFGE] = "ifge";
        opcodeVerbose[OPCODE_IFGT] = "ifgt";
        opcodeVerbose[OPCODE_IFLE] = "ifle";
        opcodeVerbose[OPCODE_IF_ICMPEQ] = "if_icmpeq";
        opcodeVerbose[OPCODE_IF_ICMPNE] = "if_icmpne";
        opcodeVerbose[OPCODE_IF_ICMPLT] = "if_icmplt";
        opcodeVerbose[OPCODE_IF_ICMPGE] = "if_icmpge";
        opcodeVerbose[OPCODE_IF_ICMPGT] = "if_icmpgt";
        opcodeVerbose[OPCODE_IF_ICMPLE] = "if_icmple";
        opcodeVerbose[OPCODE_IF_ACMPEQ] = "if_acmpeq";
        opcodeVerbose[OPCODE_IF_ACMPNE] = "if_acmpne";
        opcodeVerbose[OPCODE_GOTO] = "goto";
        opcodeVerbose[OPCODE_JSR] = "jsr";
        opcodeVerbose[OPCODE_RET] = "ret";
        opcodeVerbose[OPCODE_TABLESWITCH] = "tableswitch";
        opcodeVerbose[OPCODE_LOOKUPSWITCH] = "lookupswitch";
        opcodeVerbose[OPCODE_IRETURN] = "ireturn";
        opcodeVerbose[OPCODE_LRETURN] = "lreturn";
        opcodeVerbose[OPCODE_FRETURN] = "freturn";
        opcodeVerbose[OPCODE_DRETURN] = "dreturn";
        opcodeVerbose[OPCODE_ARETURN] = "areturn";
        opcodeVerbose[OPCODE_RETURN] = "return";
        opcodeVerbose[OPCODE_GETSTATIC] = "getstatic";
        opcodeVerbose[OPCODE_PUTSTATIC] = "putstatic";
        opcodeVerbose[OPCODE_GETFIELD] = "getfield";
        opcodeVerbose[OPCODE_PUTFIELD] = "putfield";
        opcodeVerbose[OPCODE_INVOKEVIRTUAL] = "invokevirtual";
        opcodeVerbose[OPCODE_INVOKESPECIAL] = "invokespecial";
        opcodeVerbose[OPCODE_INVOKESTATIC] = "invokestatic";
        opcodeVerbose[OPCODE_INVOKEINTERFACE] = "invokeinterface";
        opcodeVerbose[OPCODE_XXXUNUSEDXXX] = "xxxunusedxxx";
        opcodeVerbose[OPCODE_NEW] = "new";
        opcodeVerbose[OPCODE_NEWARRAY] = "newarray";
        opcodeVerbose[OPCODE_ANEWARRAY] = "anewarray";
        opcodeVerbose[OPCODE_ARRAYLENGTH] = "arraylength";
        opcodeVerbose[OPCODE_ATHROW] = "athrow";
        opcodeVerbose[OPCODE_CHECKCAST] = "checkcast";
        opcodeVerbose[OPCODE_INSTANCEOF] = "instanceof";
        opcodeVerbose[OPCODE_MONITORENTER] = "monitorenter";
        opcodeVerbose[OPCODE_MONITOREXIT] = "monitorexit";
        opcodeVerbose[OPCODE_WIDE] = "wide";
        opcodeVerbose[OPCODE_MULTIANEWARRAY] = "multianewarray";
        opcodeVerbose[OPCODE_IFNULL] = "ifnull";
        opcodeVerbose[OPCODE_IFNONNULL] = "ifnonnull";
        opcodeVerbose[OPCODE_GOTO_W] = "goto_w";
        opcodeVerbose[OPCODE_JSR_W] = "jsr_w";
        opcodeVerbose[OPCODE_BREAKPOINT] = "breakpoint";
        opcodeVerbose[OPCODE_IMPDEP1] = "impdep1";
        opcodeVerbose[OPCODE_IMPDEP2] = "impdep2";

    }
    
    
    /**
        Get the verbose description for a numeric opcode.
        @param opcode the opcode
        @return the description
     */
    public static String getVerbose(int opcode) {
        if (opcode < 0 || opcode > 255) {
            return null;
        } else {
            return opcodeVerbose[opcode];
        }
    }
    
    /**
        Get the verbose description for a numeric array type in the immediate
        argument of the <tt>newarray</tt> instruction.
        @param arrayType the array type
        @return the description
     */
    public static String getArrayTypeVerbose(int arrayType) {
        
        switch (arrayType) {
            case NEWARRAY_T_BOOLEAN:
                return "boolean";
            case NEWARRAY_T_CHAR:
                return "char";
            case NEWARRAY_T_FLOAT:
                return "float";
            case NEWARRAY_T_DOUBLE:
                return "double";
            case NEWARRAY_T_BYTE:
                return "byte";
            case NEWARRAY_T_SHORT:
                return "short";
            case NEWARRAY_T_INT:
                return "int";
            case NEWARRAY_T_LONG:
                return "long";
            default:
                return "invalid array type";
        }
    }    
}
