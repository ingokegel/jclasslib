/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.bytecode.Opcode
import org.gjt.jclasslib.bytecode.Opcode.*

interface StackChanges {
    fun isReplacementCompatibleWith(stackChanges: StackChanges): Boolean
    val changes: List<SingleStackChange>
}

class SingleStackChange(val pops: List<StackValueType>, val pushes: List<StackValueType>) : StackChanges {
    override fun toString() = "$pops -> $pushes"
    override val changes get() = listOf(this)
    override fun isReplacementCompatibleWith(stackChanges: StackChanges): Boolean {
        return stackChanges.changes.any { isReplacementCompatibleWith(it) }
    }
    fun isReplacementCompatibleWith(other: SingleStackChange): Boolean =
        pops == other.pops && pushes == other.pushes
}

class MultipleStackChanges(vararg singleStackChanges: SingleStackChange) : StackChanges {
    override fun toString() = stackChanges.toList().toString()
    override val changes: List<SingleStackChange> = singleStackChanges.toList()
    override fun isReplacementCompatibleWith(stackChanges: StackChanges): Boolean =
        changes.any { it.isReplacementCompatibleWith(stackChanges) }
}

enum class StackValueType(val verbose: String) {
    REFERENCE("Reference"),
    VALUE("Value"),
    VALUE_C1("Value C1"),
    VALUE_C2("Value C2"),
    INTEGER("Index"),
    LONG("Long"),
    FLOAT("Float"),
    DOUBLE("Double"),
    ARRAY_REFERENCE("Array reference"),
    VARIABLE("Variable"),
    OPTIONAL_VALUE("Optional value");

    override fun toString() = verbose

    fun times(n: Int) = List(n) { this }
    fun single() = listOf(this)
}

private val none = emptyList<StackValueType>()
private val reference = StackValueType.REFERENCE.single()
private val value = StackValueType.VALUE.single()
private val twoValues = StackValueType.VALUE.times(2)
private val valueC1 = StackValueType.VALUE_C1.single()
private val twoValuesC1 = StackValueType.VALUE_C1.times(2)
private val threeValuesC1 = StackValueType.VALUE_C1.times(3)
private val fourValuesC1 = StackValueType.VALUE_C1.times(4)
private val fiveValuesC1 = StackValueType.VALUE_C1.times(5)
private val sixValuesC1 = StackValueType.VALUE_C1.times(6)
private val valueC2 = StackValueType.VALUE_C2.single()
private val twoValuesC2 = StackValueType.VALUE_C2.times(2)
private val threeValuesC2 = StackValueType.VALUE_C2.times(3)
private val integer = StackValueType.INTEGER.single()
private val twoIntegers = StackValueType.INTEGER.times(2)
private val long = StackValueType.LONG.single()
private val twoLongs = StackValueType.LONG.times(2)
private val float = StackValueType.FLOAT.single()
private val twoFloats = StackValueType.FLOAT.times(2)
private val double = StackValueType.DOUBLE.single()
private val twoDoubles = StackValueType.DOUBLE.times(2)
private val arrayReference = StackValueType.ARRAY_REFERENCE.single()
private val arrayReferenceAndInteger = listOf(StackValueType.ARRAY_REFERENCE, StackValueType.INTEGER)
private val arrayReferenceAndIntegerAndReference = listOf(StackValueType.ARRAY_REFERENCE, StackValueType.INTEGER, StackValueType.REFERENCE)
private val variable = StackValueType.VARIABLE.single()
private val optionalValue = StackValueType.OPTIONAL_VALUE.single()

fun getStackChanges(opcode: Opcode): StackChanges? = stackChanges[opcode]

private val stackChanges: Map<Opcode, StackChanges> by lazy {
    mutableMapOf<Opcode, StackChanges>().apply {
        add(listOf(AALOAD, BALOAD, CALOAD, DALOAD, FALOAD, IALOAD, LALOAD, SALOAD), arrayReferenceAndInteger, reference)
        add(listOf(AASTORE, BASTORE, CASTORE, DASTORE, FASTORE, IASTORE, LASTORE, SASTORE), arrayReferenceAndIntegerAndReference, none)
        add(listOf(ALOAD, ALOAD_0, ALOAD_1, ALOAD_2, ALOAD_3, ACONST_NULL), none, reference)
        add(ANEWARRAY, integer, arrayReference)
        add(ARRAYLENGTH, arrayReference, integer)
        add(listOf(ARETURN, ASTORE, ASTORE_0, ASTORE_1, ASTORE_2, ASTORE_3), reference, none)
        add(ATHROW, reference, reference)
        add(listOf(BIPUSH, SIPUSH), none, integer)
        add(CHECKCAST, reference, reference)

        add(D2F, double, float)
        add(D2I, double, integer)
        add(D2L, double, long)
        add(listOf(DADD, DDIV, DMUL, DREM, DSUB), twoDoubles, double)
        add(listOf(DCMPG, DCMPL), twoDoubles, integer)
        add(listOf(DLOAD, DLOAD_0, DLOAD_1, DLOAD_2, DLOAD_3, DCONST_0, DCONST_1), none, double)
        add(DNEG, double, double)
        add(listOf(DRETURN, DSTORE, DSTORE_0, DSTORE_1, DSTORE_2, DSTORE_3), double, none)

        add(DUP, valueC1, twoValuesC1)
        add(DUP_X1, twoValuesC1, threeValuesC1)
        add(DUP_X2,
                SingleStackChange(threeValuesC1, fourValuesC1),
                SingleStackChange(
                        listOf(StackValueType.VALUE_C2, StackValueType.VALUE_C1),
                        listOf(StackValueType.VALUE_C1, StackValueType.VALUE_C2, StackValueType.VALUE_C1)
                )
        )
        add(DUP2,
                SingleStackChange(twoValuesC1, fourValuesC1),
                SingleStackChange(valueC2, twoValuesC2)
        )
        add(DUP2_X1,
                SingleStackChange(threeValuesC1, fiveValuesC1),
                SingleStackChange(
                        listOf(StackValueType.VALUE_C1, StackValueType.VALUE_C2),
                        listOf(StackValueType.VALUE_C2, StackValueType.VALUE_C1, StackValueType.VALUE_C2)
                )
        )
        add(DUP2_X2,
                SingleStackChange(fourValuesC1, sixValuesC1),
                SingleStackChange(
                        listOf(StackValueType.VALUE_C1, StackValueType.VALUE_C1, StackValueType.VALUE_C2),
                        listOf(StackValueType.VALUE_C2, StackValueType.VALUE_C1, StackValueType.VALUE_C1, StackValueType.VALUE_C2)
                ),
                SingleStackChange(
                        listOf(StackValueType.VALUE_C2, StackValueType.VALUE_C1, StackValueType.VALUE_C1),
                        listOf(StackValueType.VALUE_C1, StackValueType.VALUE_C1, StackValueType.VALUE_C2, StackValueType.VALUE_C1, StackValueType.VALUE_C1)
                ),
                SingleStackChange(twoValuesC2, threeValuesC2)
        )

        add(F2D, float, double)
        add(F2I, float, integer)
        add(F2L, float, long)
        add(listOf(FADD, FDIV, FMUL, FREM, FSUB), twoFloats, float)
        add(listOf(FCMPG, FCMPL), twoFloats, integer)
        add(listOf(FLOAD, FLOAD_0, FLOAD_1, FLOAD_2, FLOAD_3, FCONST_0, FCONST_1, FCONST_2), none, float)
        add(FNEG, float, float)
        add(listOf(FRETURN, FSTORE, FSTORE_0, FSTORE_1, FSTORE_2, FSTORE_3), float, none)

        add(GETFIELD, reference, value)
        add(GETSTATIC, none, value)
        add(listOf(GOTO, GOTO_W), none, none)

        add(I2B, integer, integer)
        add(I2C, integer, integer)
        add(I2D, integer, double)
        add(I2F, integer, float)
        add(I2L, integer, long)
        add(I2S, integer, integer)
        add(listOf(IADD, IDIV, IMUL, IREM, ISUB, IAND, IOR, ISHL, ISHR, IUSHR, IXOR), twoIntegers, integer)
        add(listOf(ILOAD, ILOAD_0, ILOAD_1, ILOAD_2, ILOAD_3, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5, ICONST_M1), none, integer)
        add(INEG, integer, integer)
        add(listOf(IRETURN, ISTORE, ISTORE_0, ISTORE_1, ISTORE_2, ISTORE_3), integer, none)

        add(listOf(IF_ACMPEQ, IF_ACMPNE, IF_ICMPEQ, IF_ICMPNE, IF_ICMPLT, IF_ICMPGE, IF_ICMPGT, IF_ICMPLE), twoValues, none)
        add(listOf(IFEQ, IFNE, IFLT, IFGE, IFGT, IFLE, IFNONNULL, IFNULL), value, none)
        add(IINC, none, none)
        add(INSTANCEOF, reference, integer)
        add(INVOKEDYNAMIC, variable, optionalValue)
        add(listOf(INVOKEINTERFACE, INVOKESPECIAL, INVOKESTATIC, INVOKEVIRTUAL),
                listOf(StackValueType.REFERENCE, StackValueType.VARIABLE),
                optionalValue
        )
        add(listOf(JSR, JSR_W), none, integer)

        add(L2D, long, double)
        add(L2F, long, integer)
        add(L2I, long, integer)
        add(listOf(LADD, LDIV, LMUL, LREM, LSUB, LAND, LOR, LSHL, LSHR, LUSHR, LXOR), twoLongs, long)
        add(listOf(LLOAD, LLOAD_0, LLOAD_1, LLOAD_2, LLOAD_3, LCONST_0, LCONST_1), none, long)
        add(LNEG, long, long)
        add(listOf(LRETURN, LSTORE, LSTORE_0, LSTORE_1, LSTORE_2, LSTORE_3), long, none)
        add(LCMP, twoLongs, integer)

        add(listOf(LDC, LDC_W, LDC2_W), none, value)
        add(listOf(LOOKUPSWITCH, TABLESWITCH), integer, none)
        add(listOf(MONITORENTER, MONITOREXIT), reference, none)
        add(MULTIANEWARRAY, variable, arrayReference)
        add(NEW, none, reference)
        add(NEWARRAY, integer, arrayReference)
        add(NOP, none, none)
        add(POP, value, none)
        add(POP2, twoValues, none)
        add(PUTFIELD, listOf(StackValueType.REFERENCE, StackValueType.INTEGER), none)
        add(PUTSTATIC, integer, none)
        add(RET, none, none)
        add(RETURN, none, none)
        add(SWAP, twoValues, twoValues)
        add(WIDE, none, none)
    }
}

private fun MutableMap<Opcode, StackChanges>.add(opcode: Opcode, pops: List<StackValueType>, pushes: List<StackValueType>) {
    check(!containsKey(opcode))
    put(opcode, SingleStackChange(pops, pushes))
}

private fun MutableMap<Opcode, StackChanges>.add(opcode: Opcode, vararg stackChanges: SingleStackChange) {
    check(!containsKey(opcode))
    put(opcode, MultipleStackChanges(*stackChanges))
}

private fun MutableMap<Opcode, StackChanges>.add(opcodes: List<Opcode>, pops: List<StackValueType>, pushes: List<StackValueType>) {
    for (opcode in opcodes) {
        add(opcode, pops, pushes)
    }
}
