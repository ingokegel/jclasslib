/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.bytecode.*
import org.gjt.jclasslib.util.AlertType
import org.gjt.jclasslib.util.GUIHelper
import org.jetbrains.annotations.Nls
import java.awt.Window
import javax.swing.JOptionPane

fun getImmediateEditActions(instruction: Instruction): List<ImmediateEditAction<*>> =
    when (instruction) {
        is SimpleImmediateByteInstruction ->
            listOf(ImmediateByteEditAction(getString("action.edit.immediate.byte")))
        is IncrementInstruction -> listOf(
                ImmediateByteEditAction(getString("action.edit.index")),
                IncrementConstantEditAction()
        )
        is SimpleImmediateShortInstruction ->
            listOf(ImmediateShortEditAction(getString("action.edit.immediate.short")))
        is InvokeDynamicInstruction ->
            listOf(ImmediateShortEditAction(getString("action.edit.immediate.short")))
        is BranchInstruction -> listOf(BranchEditAction())
        is WideBranchInstruction -> listOf(WideBranchEditAction())
        is MultianewarrayInstruction -> listOf(
                ImmediateShortEditAction(getString("action.edit.index")),
                ArrayDimensionEditAction()
        )
        is InvokeInterfaceInstruction -> listOf(
                ImmediateShortEditAction(getString("action.edit.index")),
                InvokeInterfaceCountEditAction()
        )
        is TableSwitchInstruction -> instruction.jumpOffsets.mapIndexed { index, _ ->
            TableSwitchJumpOffsetEditAction(index)
        } + listOf(TableSwitchDefaultJumpOffsetEditAction())
        is LookupSwitchInstruction -> instruction.matchOffsetPairs.flatMapIndexed { index, matchOffsetPair ->
            listOf(
                    LookupSwitchMatchEditAction(index, matchOffsetPair.match),
                    LookupSwitchJumpOffsetEditAction(index, matchOffsetPair.match)
            )
        } + listOf(LookupSwitchDefaultJumpOffsetEditAction())
        else -> emptyList()
    }

abstract class ImmediateEditAction<I : Instruction>(@Nls val name: String) {
    abstract val instructionClass: Class<I>
    abstract fun execute(instruction: I, parentWindow: Window?): Boolean
    fun executeRaw(instruction: Instruction, parentWindow: Window?): Boolean =
        execute(instructionClass.cast(instruction), parentWindow)

    @get:Nls
    open val group: String?
        get() = null
}

abstract class ValueEditAction<I : Instruction, T>(@Nls name: String) : ImmediateEditAction<I>(name) {
    @get:Nls
    abstract val message: String

    @get:Nls
    abstract val conversionErrorMessage: String
    abstract fun getValue(instruction: I): T
    abstract fun setValue(value: T, instruction: I)
    abstract fun convertToValue(result: Any?): T?
    override fun execute(instruction: I, parentWindow: Window?): Boolean {
        val value = getValue(instruction)
        val newValue = try {
            convertToValue(JOptionPane.showInputDialog(
                    parentWindow,
                    message,
                    name,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    value
            ))
        } catch (e: NumberFormatException) {
            GUIHelper.showMessage(parentWindow, conversionErrorMessage, AlertType.WARNING)
            null
        }

        return if (newValue != null && newValue != value) {
            setValue(newValue, instruction)
            true
        } else {
            false
        }
    }
}

abstract class ByteEditAction<I : Instruction>(@Nls name: String) : ValueEditAction<I, UByte>(name) {
    override val message get() = getString("key.byte.value")
    override val conversionErrorMessage get() = getString("input.not.valid.byte")
    override fun convertToValue(result: Any?) = result?.toString()?.toUByte()
}

class ImmediateByteEditAction(@Nls name: String) : ByteEditAction<ImmediateByteInstruction>(name) {
    override val instructionClass get() = ImmediateByteInstruction::class.java

    override fun getValue(instruction: ImmediateByteInstruction): UByte = instruction.immediateByte.toUByte()

    override fun setValue(value: UByte, instruction: ImmediateByteInstruction) {
        instruction.immediateByte = value.toInt()
    }
}

abstract class ShortEditAction<I : Instruction>(@Nls name: String) : ValueEditAction<I, UShort>(name) {
    override val message get() = getString("key.short.value")
    override val conversionErrorMessage get() = getString("input.not.valid.short")
    override fun convertToValue(result: Any?) = result?.toString()?.toUShort()
}

class ImmediateShortEditAction(@Nls name: String) : ShortEditAction<ImmediateShortInstruction>(name) {
    override val instructionClass get() = ImmediateShortInstruction::class.java

    override fun getValue(instruction: ImmediateShortInstruction): UShort = instruction.immediateShort.toUShort()

    override fun setValue(value: UShort, instruction: ImmediateShortInstruction) {
        instruction.immediateShort = value.toInt()
    }
}

abstract class IntegerEditAction<I : Instruction>(@Nls name: String) : ValueEditAction<I, UInt>(name) {
    override val message get() = getString("key.integer.value")
    override val conversionErrorMessage get() = getString("input.not.valid.integer")
    override fun convertToValue(result: Any?) = result?.toString()?.toUInt()
}

class IncrementConstantEditAction : IntegerEditAction<IncrementInstruction>(getString("action.edit.constant")) {
    override val instructionClass get() = IncrementInstruction::class.java

    override fun getValue(instruction: IncrementInstruction): UInt = instruction.incrementConst.toUInt()

    override fun setValue(value: UInt, instruction: IncrementInstruction) {
        instruction.incrementConst = value.toInt()
    }
}

class BranchEditAction : ShortEditAction<BranchInstruction>(getString("action.edit.branch.offset")) {
    override val instructionClass get() = BranchInstruction::class.java

    override fun getValue(instruction: BranchInstruction) = instruction.branchOffset.toUShort()

    override fun setValue(value: UShort, instruction: BranchInstruction) {
        instruction.branchOffset = value.toInt()
    }
}

class WideBranchEditAction : IntegerEditAction<WideBranchInstruction>(getString("action.edit.branch.offset")) {
    override val instructionClass get() = WideBranchInstruction::class.java

    override fun getValue(instruction: WideBranchInstruction) = instruction.branchOffset.toUInt()

    override fun setValue(value: UInt, instruction: WideBranchInstruction) {
        instruction.branchOffset = value.toInt()
    }
}

class ArrayDimensionEditAction : ByteEditAction<MultianewarrayInstruction>(getString("action.edit.dimensions")) {
    override val instructionClass get() = MultianewarrayInstruction::class.java

    override fun getValue(instruction: MultianewarrayInstruction) = instruction.dimensions.toUByte()

    override fun setValue(value: UByte, instruction: MultianewarrayInstruction) {
        instruction.dimensions = value.toInt()
    }
}

class InvokeInterfaceCountEditAction : ByteEditAction<InvokeInterfaceInstruction>(getString("action.edit.count")) {
    override val instructionClass get() = InvokeInterfaceInstruction::class.java

    override fun getValue(instruction: InvokeInterfaceInstruction) = instruction.count.toUByte()

    override fun setValue(value: UByte, instruction: InvokeInterfaceInstruction) {
        instruction.count = value.toInt()
    }
}

class TableSwitchJumpOffsetEditAction(val index: Int) : IntegerEditAction<TableSwitchInstruction>(getString("action.jump.offset.0", index)) {
    override val instructionClass get() = TableSwitchInstruction::class.java

    override fun getValue(instruction: TableSwitchInstruction): UInt = instruction.jumpOffsets[index].toUInt()

    override fun setValue(value: UInt, instruction: TableSwitchInstruction) {
        instruction.jumpOffsets[index] = value.toInt()
    }

    override val group get() = getString("action.edit.jump.offsets")
}

class TableSwitchDefaultJumpOffsetEditAction() : IntegerEditAction<TableSwitchInstruction>(getString("action.edit.default.jump.offset")) {
    override val instructionClass get() = TableSwitchInstruction::class.java

    override fun getValue(instruction: TableSwitchInstruction): UInt = instruction.defaultOffset.toUInt()

    override fun setValue(value: UInt, instruction: TableSwitchInstruction) {
        instruction.defaultOffset = value.toInt()
    }
}

class LookupSwitchMatchEditAction(val index: Int, match: Int) : IntegerEditAction<LookupSwitchInstruction>(getString("action.match.0", match)) {
    override val instructionClass get() = LookupSwitchInstruction::class.java

    override fun getValue(instruction: LookupSwitchInstruction): UInt = instruction.matchOffsetPairs[index].match.toUInt()

    override fun setValue(value: UInt, instruction: LookupSwitchInstruction) {
        instruction.matchOffsetPairs[index].match = value.toInt()
    }

    override val group get() = getString("action.edit.matches")
}

class LookupSwitchJumpOffsetEditAction(val index: Int, match: Int) : IntegerEditAction<LookupSwitchInstruction>(getString("action.jump.offset.for.match.0", match)) {
    override val instructionClass get() = LookupSwitchInstruction::class.java

    override fun getValue(instruction: LookupSwitchInstruction): UInt = instruction.matchOffsetPairs[index].offset.toUInt()

    override fun setValue(value: UInt, instruction: LookupSwitchInstruction) {
        instruction.matchOffsetPairs[index].offset = value.toInt()
    }

    override val group get() = getString("action.edit.jump.offsets")
}

class LookupSwitchDefaultJumpOffsetEditAction() : IntegerEditAction<LookupSwitchInstruction>(getString("action.edit.default.jump.offset")) {
    override val instructionClass get() = LookupSwitchInstruction::class.java

    override fun getValue(instruction: LookupSwitchInstruction): UInt = instruction.defaultOffset.toUInt()

    override fun setValue(value: UInt, instruction: LookupSwitchInstruction) {
        instruction.defaultOffset = value.toInt()
    }
}