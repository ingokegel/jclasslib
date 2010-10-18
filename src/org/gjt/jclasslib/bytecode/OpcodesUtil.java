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
    @version $Revision: 1.4 $ $Date: 2010-07-26 13:59:49 $
*/
public class OpcodesUtil implements Opcodes {

    private OpcodesUtil() {}

    private static String[] opcodeVerbose;
    private static String[] opcodeURL;

    private static final String JVM_SPEC_URL =
            "http://java.sun.com/docs/books/vmspec/2nd-edition/html/";

    private static final String INVALID_OPCODE_URL = "Instructions.doc.html";
    
    static {
        
        opcodeVerbose = new String[256];
        opcodeURL = new String[256];
        
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
        opcodeVerbose[OPCODE_INVOKEDYNAMIC] = "invokedynamic";
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

        opcodeURL[OPCODE_AALOAD] = "Instructions2.doc.html#aaload";
        opcodeURL[OPCODE_AASTORE] = "Instructions2.doc.html#aastore";
        opcodeURL[OPCODE_ACONST_NULL] = "Instructions2.doc.html#aconst_null";
        opcodeURL[OPCODE_ALOAD] = "Instructions2.doc.html#aload";
        opcodeURL[OPCODE_ALOAD_0] = "Instructions2.doc.html#aload_n";
        opcodeURL[OPCODE_ALOAD_1] = opcodeURL[OPCODE_ALOAD_0];
        opcodeURL[OPCODE_ALOAD_2] = opcodeURL[OPCODE_ALOAD_0];
        opcodeURL[OPCODE_ALOAD_3] = "Instructions2.doc.html#aload_n";
        opcodeURL[OPCODE_ANEWARRAY] = "Instructions2.doc.html#anewarray";
        opcodeURL[OPCODE_ARETURN] = "Instructions2.doc.html#areturn";
        opcodeURL[OPCODE_ARRAYLENGTH] = "Instructions2.doc.html#arraylength";
        opcodeURL[OPCODE_ASTORE] = "Instructions2.doc.html#astore";
        opcodeURL[OPCODE_ASTORE_0] = "Instructions2.doc.html#astore_n";
        opcodeURL[OPCODE_ASTORE_1] = opcodeURL[OPCODE_ASTORE_0];
        opcodeURL[OPCODE_ASTORE_2] = opcodeURL[OPCODE_ASTORE_0];
        opcodeURL[OPCODE_ASTORE_3] = "Instructions2.doc.html#astore_n";
        opcodeURL[OPCODE_ATHROW] = "Instructions2.doc.html#athrow";
        opcodeURL[OPCODE_BALOAD] = "Instructions2.doc1.html#baload";
        opcodeURL[OPCODE_BASTORE] = "Instructions2.doc1.html#bastore";
        opcodeURL[OPCODE_BIPUSH] = "Instructions2.doc1.html#bipush";
        opcodeURL[OPCODE_NEW] = "Instructions2.doc10.html#new";
        opcodeURL[OPCODE_NEWARRAY] = "Instructions2.doc10.html#newarray";
        opcodeURL[OPCODE_NOP] = "Instructions2.doc10.html#nop";
        opcodeURL[OPCODE_POP] = "Instructions2.doc11.html#pop";
        opcodeURL[OPCODE_POP2] = "Instructions2.doc11.html#pop2";
        opcodeURL[OPCODE_PUTFIELD] = "Instructions2.doc11.html#putfield";
        opcodeURL[OPCODE_PUTSTATIC] = "Instructions2.doc11.html#putstatic";
        opcodeURL[OPCODE_RET] = "Instructions2.doc12.html#ret";
        opcodeURL[OPCODE_RETURN] = "Instructions2.doc12.html#return";
        opcodeURL[OPCODE_SALOAD] = "Instructions2.doc13.html#saload";
        opcodeURL[OPCODE_SASTORE] = "Instructions2.doc13.html#sastore";
        opcodeURL[OPCODE_SIPUSH] = "Instructions2.doc13.html#sipush";
        opcodeURL[OPCODE_SWAP] = "Instructions2.doc13.html#swap";
        opcodeURL[OPCODE_TABLESWITCH] = "Instructions2.doc14.html#tableswitch";
        opcodeURL[OPCODE_WIDE] = "Instructions2.doc15.html#wide";
        opcodeURL[OPCODE_CALOAD] = "Instructions2.doc2.html#caload";
        opcodeURL[OPCODE_CASTORE] = "Instructions2.doc2.html#castore";
        opcodeURL[OPCODE_CHECKCAST] = "Instructions2.doc2.html#checkcast";
        opcodeURL[OPCODE_D2F] = "Instructions2.doc3.html#d2f";
        opcodeURL[OPCODE_D2I] = "Instructions2.doc3.html#d2i";
        opcodeURL[OPCODE_D2L] = "Instructions2.doc3.html#d2l";
        opcodeURL[OPCODE_DADD] = "Instructions2.doc3.html#dadd";
        opcodeURL[OPCODE_DALOAD] = "Instructions2.doc3.html#daload";
        opcodeURL[OPCODE_DASTORE] = "Instructions2.doc3.html#dastore";
        opcodeURL[OPCODE_DCMPG] = "Instructions2.doc3.html#dcmpop";
        opcodeURL[OPCODE_DCMPL] = opcodeURL[OPCODE_DCMPG];
        opcodeURL[OPCODE_DCONST_0] = "Instructions2.doc3.html#dconst_d";
        opcodeURL[OPCODE_DCONST_1] = opcodeURL[OPCODE_DCONST_0];
        opcodeURL[OPCODE_DDIV] = "Instructions2.doc3.html#ddiv";
        opcodeURL[OPCODE_DLOAD] = "Instructions2.doc3.html#dload";
        opcodeURL[OPCODE_DLOAD_0] = "Instructions2.doc3.html#dload_n";
        opcodeURL[OPCODE_DLOAD_1] = opcodeURL[OPCODE_DLOAD_0];
        opcodeURL[OPCODE_DLOAD_2] = opcodeURL[OPCODE_DLOAD_0];
        opcodeURL[OPCODE_DLOAD_3] = opcodeURL[OPCODE_DLOAD_0];
        opcodeURL[OPCODE_DMUL] = "Instructions2.doc3.html#dmul";
        opcodeURL[OPCODE_DNEG] = "Instructions2.doc3.html#dneg";
        opcodeURL[OPCODE_DREM] = "Instructions2.doc3.html#drem";
        opcodeURL[OPCODE_DRETURN] = "Instructions2.doc3.html#dreturn";
        opcodeURL[OPCODE_DSTORE] = "Instructions2.doc3.html#dstore";
        opcodeURL[OPCODE_DSTORE_0] = "Instructions2.doc3.html#dstore_n";
        opcodeURL[OPCODE_DSTORE_1] = opcodeURL[OPCODE_DSTORE_0];
        opcodeURL[OPCODE_DSTORE_2] = opcodeURL[OPCODE_DSTORE_0];
        opcodeURL[OPCODE_DSTORE_3] = opcodeURL[OPCODE_DSTORE_0];
        opcodeURL[OPCODE_DSUB] = "Instructions2.doc3.html#dsub";
        opcodeURL[OPCODE_DUP] = "Instructions2.doc3.html#dup";
        opcodeURL[OPCODE_DUP2] = "Instructions2.doc3.html#dup2";
        opcodeURL[OPCODE_DUP2_X1] = "Instructions2.doc3.html#dup2_x1";
        opcodeURL[OPCODE_DUP2_X2] = "Instructions2.doc3.html#dup2_x2";
        opcodeURL[OPCODE_DUP_X1] = "Instructions2.doc3.html#dup_x1";
        opcodeURL[OPCODE_DUP_X2] = "Instructions2.doc3.html#dup_x2";
        opcodeURL[OPCODE_F2D] = "Instructions2.doc4.html#f2d";
        opcodeURL[OPCODE_F2I] = "Instructions2.doc4.html#f2i";
        opcodeURL[OPCODE_F2L] = "Instructions2.doc4.html#f2l";
        opcodeURL[OPCODE_FADD] = "Instructions2.doc4.html#fadd";
        opcodeURL[OPCODE_FALOAD] = "Instructions2.doc4.html#faload";
        opcodeURL[OPCODE_FASTORE] = "Instructions2.doc4.html#fastore";
        opcodeURL[OPCODE_FCMPG] = "Instructions2.doc4.html#fcmpop";
        opcodeURL[OPCODE_FCMPL] = opcodeURL[OPCODE_FCMPG];
        opcodeURL[OPCODE_FCONST_0] = "Instructions2.doc4.html#fconst_f";
        opcodeURL[OPCODE_FCONST_1] = opcodeURL[OPCODE_FCONST_0];
        opcodeURL[OPCODE_FCONST_2] = "Instructions2.doc4.html#fconst_f";
        opcodeURL[OPCODE_FDIV] = "Instructions2.doc4.html#fdiv";
        opcodeURL[OPCODE_FLOAD] = "Instructions2.doc4.html#fload";
        opcodeURL[OPCODE_FLOAD_0] = "Instructions2.doc4.html#fload_n";
        opcodeURL[OPCODE_FLOAD_1] = opcodeURL[OPCODE_FLOAD_0];
        opcodeURL[OPCODE_FLOAD_2] = opcodeURL[OPCODE_FLOAD_0];
        opcodeURL[OPCODE_FLOAD_3] = opcodeURL[OPCODE_FLOAD_0];
        opcodeURL[OPCODE_FMUL] = "Instructions2.doc4.html#fmul";
        opcodeURL[OPCODE_FNEG] = "Instructions2.doc4.html#fneg";
        opcodeURL[OPCODE_FREM] = "Instructions2.doc4.html#frem";
        opcodeURL[OPCODE_FRETURN] = "Instructions2.doc4.html#freturn";
        opcodeURL[OPCODE_FSTORE] = "Instructions2.doc4.html#fstore";
        opcodeURL[OPCODE_FSTORE_0] = "Instructions2.doc4.html#fstore_n";
        opcodeURL[OPCODE_FSTORE_1] = opcodeURL[OPCODE_FSTORE_0];
        opcodeURL[OPCODE_FSTORE_2] = opcodeURL[OPCODE_FSTORE_0];
        opcodeURL[OPCODE_FSTORE_3] = opcodeURL[OPCODE_FSTORE_0];
        opcodeURL[OPCODE_FSUB] = "Instructions2.doc4.html#fsub";
        opcodeURL[OPCODE_GETFIELD] = "Instructions2.doc5.html#getfield";
        opcodeURL[OPCODE_GETSTATIC] = "Instructions2.doc5.html#getstatic";
        opcodeURL[OPCODE_GOTO] = "Instructions2.doc5.html#goto";
        opcodeURL[OPCODE_GOTO_W] = "Instructions2.doc5.html#goto_w";
        opcodeURL[OPCODE_I2B] = "Instructions2.doc6.html#i2b";
        opcodeURL[OPCODE_I2C] = "Instructions2.doc6.html#i2c";
        opcodeURL[OPCODE_I2D] = "Instructions2.doc6.html#i2d";
        opcodeURL[OPCODE_I2F] = "Instructions2.doc6.html#i2f";
        opcodeURL[OPCODE_I2L] = "Instructions2.doc6.html#i2l";
        opcodeURL[OPCODE_I2S] = "Instructions2.doc6.html#i2s";
        opcodeURL[OPCODE_IADD] = "Instructions2.doc6.html#iadd";
        opcodeURL[OPCODE_IALOAD] = "Instructions2.doc6.html#iaload";
        opcodeURL[OPCODE_IAND] = "Instructions2.doc6.html#iand";
        opcodeURL[OPCODE_IASTORE] = "Instructions2.doc6.html#iastore";
        opcodeURL[OPCODE_ICONST_M1] = "Instructions2.doc6.html#iconst_i";
        opcodeURL[OPCODE_ICONST_0] = opcodeURL[OPCODE_ICONST_M1];
        opcodeURL[OPCODE_ICONST_1] = opcodeURL[OPCODE_ICONST_M1];
        opcodeURL[OPCODE_ICONST_2] = opcodeURL[OPCODE_ICONST_M1];
        opcodeURL[OPCODE_ICONST_3] = opcodeURL[OPCODE_ICONST_M1];
        opcodeURL[OPCODE_ICONST_4] = opcodeURL[OPCODE_ICONST_M1];
        opcodeURL[OPCODE_ICONST_5] = opcodeURL[OPCODE_ICONST_M1];
        opcodeURL[OPCODE_IDIV] = "Instructions2.doc6.html#idiv";
        opcodeURL[OPCODE_IF_ACMPEQ] = "Instructions2.doc6.html#if_acmpcond";
        opcodeURL[OPCODE_IF_ACMPNE] = opcodeURL[OPCODE_IF_ACMPEQ];
        opcodeURL[OPCODE_IF_ICMPEQ] = "Instructions2.doc6.html#if_icmpcond";
        opcodeURL[OPCODE_IF_ICMPGE] = opcodeURL[OPCODE_IF_ACMPEQ];
        opcodeURL[OPCODE_IF_ICMPGT] = opcodeURL[OPCODE_IF_ACMPEQ];
        opcodeURL[OPCODE_IF_ICMPLE] = opcodeURL[OPCODE_IF_ACMPEQ];
        opcodeURL[OPCODE_IF_ICMPLT] = opcodeURL[OPCODE_IF_ACMPEQ];
        opcodeURL[OPCODE_IF_ICMPNE] = opcodeURL[OPCODE_IF_ACMPEQ];
        opcodeURL[OPCODE_IFEQ] = "Instructions2.doc6.html#ifcond";
        opcodeURL[OPCODE_IFGE] = opcodeURL[OPCODE_IFEQ];
        opcodeURL[OPCODE_IFGT] = opcodeURL[OPCODE_IFEQ];
        opcodeURL[OPCODE_IFLE] = opcodeURL[OPCODE_IFEQ];
        opcodeURL[OPCODE_IFLT] = opcodeURL[OPCODE_IFEQ];
        opcodeURL[OPCODE_IFNE] = opcodeURL[OPCODE_IFEQ];
        opcodeURL[OPCODE_IFNONNULL] = "Instructions2.doc6.html#ifnonnull";
        opcodeURL[OPCODE_IFNULL] = "Instructions2.doc6.html#ifnull";
        opcodeURL[OPCODE_IINC] = "Instructions2.doc6.html#iinc";
        opcodeURL[OPCODE_ILOAD] = "Instructions2.doc6.html#iload";
        opcodeURL[OPCODE_ILOAD_0] = "Instructions2.doc6.html#iload_n";
        opcodeURL[OPCODE_ILOAD_1] = opcodeURL[OPCODE_ILOAD_0];
        opcodeURL[OPCODE_ILOAD_2] = opcodeURL[OPCODE_ILOAD_0];
        opcodeURL[OPCODE_ILOAD_3] = opcodeURL[OPCODE_ILOAD_0];
        opcodeURL[OPCODE_IMUL] = "Instructions2.doc6.html#imul";
        opcodeURL[OPCODE_INEG] = "Instructions2.doc6.html#ineg";
        opcodeURL[OPCODE_INSTANCEOF] = "Instructions2.doc6.html#instanceof";
        opcodeURL[OPCODE_INVOKEINTERFACE] = "Instructions2.doc6.html#invokeinterface";
        opcodeURL[OPCODE_INVOKESPECIAL] = "Instructions2.doc6.html#invokespecial";
        opcodeURL[OPCODE_INVOKESTATIC] = "Instructions2.doc6.html#invokestatic";
        opcodeURL[OPCODE_INVOKEVIRTUAL] = "Instructions2.doc6.html#invokevirtual";
        opcodeURL[OPCODE_IOR] = "Instructions2.doc6.html#ior";
        opcodeURL[OPCODE_IREM] = "Instructions2.doc6.html#irem";
        opcodeURL[OPCODE_IRETURN] = "Instructions2.doc6.html#ireturn";
        opcodeURL[OPCODE_ISHL] = "Instructions2.doc6.html#ishl";
        opcodeURL[OPCODE_ISHR] = "Instructions2.doc6.html#ishr";
        opcodeURL[OPCODE_ISTORE] = "Instructions2.doc6.html#istore";
        opcodeURL[OPCODE_ISTORE_0] = "Instructions2.doc6.html#istore_n";
        opcodeURL[OPCODE_ISTORE_1] = opcodeURL[OPCODE_ISTORE_0];
        opcodeURL[OPCODE_ISTORE_2] = opcodeURL[OPCODE_ISTORE_0];
        opcodeURL[OPCODE_ISTORE_3] = opcodeURL[OPCODE_ISTORE_0];
        opcodeURL[OPCODE_ISUB] = "Instructions2.doc6.html#isub";
        opcodeURL[OPCODE_IUSHR] = "Instructions2.doc6.html#iushr";
        opcodeURL[OPCODE_IXOR] = "Instructions2.doc6.html#ixor";
        opcodeURL[OPCODE_JSR] = "Instructions2.doc7.html#jsr";
        opcodeURL[OPCODE_JSR_W] = "Instructions2.doc7.html#jsr_w";
        opcodeURL[OPCODE_L2D] = "Instructions2.doc8.html#l2d";
        opcodeURL[OPCODE_L2F] = "Instructions2.doc8.html#l2f";
        opcodeURL[OPCODE_L2I] = "Instructions2.doc8.html#l2i";
        opcodeURL[OPCODE_LADD] = "Instructions2.doc8.html#ladd";
        opcodeURL[OPCODE_LALOAD] = "Instructions2.doc8.html#laload";
        opcodeURL[OPCODE_LAND] = "Instructions2.doc8.html#land";
        opcodeURL[OPCODE_LASTORE] = "Instructions2.doc8.html#lastore";
        opcodeURL[OPCODE_LCMP] = "Instructions2.doc8.html#lcmp";
        opcodeURL[OPCODE_LCONST_0] = "Instructions2.doc8.html#lconst_l";
        opcodeURL[OPCODE_LCONST_1] = opcodeURL[OPCODE_LCONST_0];
        opcodeURL[OPCODE_LDC] = "Instructions2.doc8.html#ldc";
        opcodeURL[OPCODE_LDC2_W] = "Instructions2.doc8.html#ldc2_w";
        opcodeURL[OPCODE_LDC_W] = "Instructions2.doc8.html#ldc_w";
        opcodeURL[OPCODE_LDIV] = "Instructions2.doc8.html#ldiv";
        opcodeURL[OPCODE_LLOAD] = "Instructions2.doc8.html#lload";
        opcodeURL[OPCODE_LLOAD_0] = "Instructions2.doc8.html#lload_n";
        opcodeURL[OPCODE_LLOAD_1] = opcodeURL[OPCODE_LLOAD_0];
        opcodeURL[OPCODE_LLOAD_2] = opcodeURL[OPCODE_LLOAD_0];
        opcodeURL[OPCODE_LLOAD_3] = opcodeURL[OPCODE_LLOAD_0];
        opcodeURL[OPCODE_LMUL] = "Instructions2.doc8.html#lmul";
        opcodeURL[OPCODE_LNEG] = "Instructions2.doc8.html#lneg";
        opcodeURL[OPCODE_LOOKUPSWITCH] = "Instructions2.doc8.html#lookupswitch";
        opcodeURL[OPCODE_LOR] = "Instructions2.doc8.html#lor";
        opcodeURL[OPCODE_LREM] = "Instructions2.doc8.html#lrem";
        opcodeURL[OPCODE_LRETURN] = "Instructions2.doc8.html#lreturn";
        opcodeURL[OPCODE_LSHL] = "Instructions2.doc8.html#lshl";
        opcodeURL[OPCODE_LSHR] = "Instructions2.doc8.html#lshr";
        opcodeURL[OPCODE_LSTORE] = "Instructions2.doc8.html#lstore";
        opcodeURL[OPCODE_LSTORE_0] = "Instructions2.doc8.html#lstore_n";
        opcodeURL[OPCODE_LSTORE_1] = opcodeURL[OPCODE_LSTORE_0];
        opcodeURL[OPCODE_LSTORE_2] = opcodeURL[OPCODE_LSTORE_0];
        opcodeURL[OPCODE_LSTORE_3] = opcodeURL[OPCODE_LSTORE_0];
        opcodeURL[OPCODE_LSUB] = "Instructions2.doc8.html#lsub";
        opcodeURL[OPCODE_LUSHR] = "Instructions2.doc8.html#lushr";
        opcodeURL[OPCODE_LXOR] = "Instructions2.doc8.html#lxor";
        opcodeURL[OPCODE_MONITORENTER] = "Instructions2.doc9.html#monitorenter";
        opcodeURL[OPCODE_MONITOREXIT] = "Instructions2.doc9.html#monitorexit";
        opcodeURL[OPCODE_MULTIANEWARRAY] = "Instructions2.doc9.html#multianewarray";
        
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

    public static String getURL(int opcode) {
        return JVM_SPEC_URL +
                ((opcode < 0 || opcode > 255) ? INVALID_OPCODE_URL : opcodeURL[opcode]);
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
