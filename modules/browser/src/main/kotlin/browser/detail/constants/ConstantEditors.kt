/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.constants

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.browser.detail.ActionBuilder
import org.gjt.jclasslib.browser.detail.DataEditor
import org.gjt.jclasslib.browser.detail.EditResult
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.constants.*
import org.gjt.jclasslib.util.AlertType
import org.gjt.jclasslib.util.getParentWindow
import org.gjt.jclasslib.util.alertFacade
import javax.swing.JOptionPane

abstract class ConstantEditor<T : Constant> : DataEditor<T>() {
    override fun ActionBuilder.buildActions(detailPane: DetailPane<*>) {
        addAction(getString("action.edit")) {
            data?.let { constant ->
                edit(constant, detailPane)
            }
        }
    }

    fun edit(constant: T, detailPane: DetailPane<*>, delegateName: String? = null) {
        val newValue = JOptionPane.showInputDialog(
            detailPane.getParentWindow(),
            labelText,
            getEditTitle() + (if (delegateName == null) "" else (" [$delegateName]")),
            JOptionPane.QUESTION_MESSAGE, null, null, toString(constant)
        ) as? String? ?: return
        when (applyValue(constant, newValue)) {
            EditResult.APPLIED -> detailPane.modified()
            EditResult.INVALID -> alertFacade.showMessage(detailPane, getString("message.invalid.input"), AlertType.ERROR)
            EditResult.UNCHANGED -> {}
        }
    }

    fun applyValue(constant: T, newValue: String): EditResult {
        if (newValue == toString(constant)) {
            return EditResult.UNCHANGED
        }
        return try {
            setValueFromString(constant, newValue)
            EditResult.APPLIED
        } catch (_: Exception) {
            EditResult.INVALID
        }
    }

    protected abstract val labelText: String
    protected abstract fun setValueFromString(constant: T, newValue: String)
    protected open fun toString(constant: T) = constant.verbose
}

class ConstantUtf8Editor : ConstantEditor<ConstantUtf8Info>() {
    override val labelText get() = getString("key.edit.string.label")

    override fun setValueFromString(constant: ConstantUtf8Info, newValue: String) {
        constant.string = newValue
    }
}

class ConstantIntegerEditor : ConstantEditor<ConstantIntegerInfo>() {
    override val labelText get() = getString("key.edit.integer.label")

    override fun setValueFromString(constant: ConstantIntegerInfo, newValue: String) {
        constant.int = newValue.toInt()
    }
}

class ConstantLongEditor : ConstantEditor<ConstantLongInfo>() {
    override val labelText get() = getString("key.edit.long.label")

    override fun setValueFromString(constant: ConstantLongInfo, newValue: String) {
        constant.long = newValue.toLong()
    }
}

class ConstantFloatEditor : ConstantEditor<ConstantFloatInfo>() {
    override val labelText get() = getString("key.edit.float.label")

    override fun setValueFromString(constant: ConstantFloatInfo, newValue: String) {
        constant.float = newValue.toFloat()
    }
}

class ConstantDoubleEditor : ConstantEditor<ConstantDoubleInfo>() {
    override val labelText get() = getString("key.edit.double.label")

    override fun setValueFromString(constant: ConstantDoubleInfo, newValue: String) {
        constant.double = newValue.toDouble()
    }
}

class ConstantStringEditor : ConstantDelegateEditor<ConstantStringInfo>() {
    override fun getConstant(constant: ConstantStringInfo) = constant.utf8Constant
}

class ConstantNameEditor : ConstantDelegateEditor<ConstantNameInfo>() {
    override fun getConstant(constant: ConstantNameInfo) = constant.nameConstant
}

class ConstantMethodTypeEditor : ConstantDelegateEditor<ConstantMethodTypeInfo>() {
    override fun getConstant(constant: ConstantMethodTypeInfo) = constant.typeConstant
}

class ConstantReferenceEditor : DelegatesEditor<ConstantReference>() {
    override fun DelegateBuilder<ConstantReference>.buildDelegateSpecs() {
        addDelegateSpec(getString("menu.class.name")) {
            it.classConstant
        }
        addDelegateSpec(getString("menu.name")) {
            it.nameAndTypeConstant.nameConstant
        }
        addDelegateSpec(getString("menu.type")) {
            it.nameAndTypeConstant.descriptorConstant
        }
    }
}

class ConstantNameAndTypeEditor : DelegatesEditor<ConstantNameAndTypeInfo>() {
    override fun DelegateBuilder<ConstantNameAndTypeInfo>.buildDelegateSpecs() {
        addDelegateSpec(getString("menu.name")) {
            it.nameConstant
        }
        addDelegateSpec(getString("menu.descriptor")) {
            it.descriptorConstant
        }
    }
}

class ConstantMethodHandleEditor : DelegatesEditor<ConstantMethodHandleInfo>() {
    override fun DelegateBuilder<ConstantMethodHandleInfo>.buildDelegateSpecs() {
        addEnumSpec(
                getString("menu.reference.kind"),
                MethodHandleType::class.java,
                ConstantMethodHandleInfo::type
        )
        addDelegateSpec(getString("menu.class.name")) {
            it.referenceConstant.classConstant
        }
        addDelegateSpec(getString("menu.method.name")) {
            it.referenceConstant.nameAndTypeConstant.nameConstant
        }
        addDelegateSpec(getString("menu.type")) {
            it.referenceConstant.nameAndTypeConstant.descriptorConstant
        }
    }
}

class ConstantDynamicEditor : DelegatesEditor<ConstantDynamic>() {
    override fun DelegateBuilder<ConstantDynamic>.buildDelegateSpecs() {
        addDelegateSpec(getString("menu.name")) {
            it.nameAndTypeInfo.nameConstant
        }
        addDelegateSpec(getString("menu.type")) {
            it.nameAndTypeInfo.descriptorConstant
        }
    }
}

fun interface ConstantEdit {
    fun edit(detailPane: DetailPane<*>, delegateName: String?)
}

fun getConstantEdit(constant: Constant): ConstantEdit? = when (constant) {
    is ConstantUtf8Info -> ConstantEdit { pane, name -> ConstantUtf8Editor().edit(constant, pane, name) }
    is ConstantIntegerInfo -> ConstantEdit { pane, name -> ConstantIntegerEditor().edit(constant, pane, name) }
    is ConstantLongInfo -> ConstantEdit { pane, name -> ConstantLongEditor().edit(constant, pane, name) }
    is ConstantFloatInfo -> ConstantEdit { pane, name -> ConstantFloatEditor().edit(constant, pane, name) }
    is ConstantDoubleInfo -> ConstantEdit { pane, name -> ConstantDoubleEditor().edit(constant, pane, name) }
    is ConstantNameInfo -> ConstantEdit { pane, name -> ConstantNameEditor().edit(constant, pane, name) }
    is ConstantStringInfo -> ConstantEdit { pane, name -> ConstantStringEditor().edit(constant, pane, name) }
    else -> null
}