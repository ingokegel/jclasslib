/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import com.install4j.runtime.alert.AlertType
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.constants.*
import org.gjt.jclasslib.util.GUIHelper
import org.gjt.jclasslib.util.GUIHelper.getParentWindow
import org.gjt.jclasslib.util.MenuButton
import org.jetbrains.annotations.Nls
import java.awt.event.ActionListener
import javax.swing.*

abstract class ConstantEditor<T : Constant> {
    protected var constant: T? = null

    abstract fun ActionBuilder.buildActions(detailPane: DetailPane<*>)

    fun createButton(detailPane: ConstantDetailPane<T>): JButton {
        detailPane.addShowHandler {
            constant = it
        }
        val actionBuilder = ActionBuilderImpl()
        actionBuilder.buildActions(detailPane)
        return MenuButton(getString("action.edit"), actionBuilder.popupMenu)
    }

    protected fun askForEnumValue(values: Array<*>, selectedValue: Any, labelText: String, title: String, parent: JComponent): Any? =
        JOptionPane.showInputDialog(
                parent.getParentWindow(),
                labelText,
                title,
                JOptionPane.QUESTION_MESSAGE,
                null,
                values,
                selectedValue
        )
}

interface ActionBuilder {
    fun addAction(name: String, actionListener: ActionListener)
}

class ActionBuilderImpl : ActionBuilder {
    val popupMenu = JPopupMenu()
    override fun addAction(@Nls name: String, actionListener: ActionListener) {
        popupMenu.add(JMenuItem(name).apply {
            addActionListener(actionListener)
        })
    }
}

abstract class DirectConstantEditor<T : Constant> : ConstantEditor<T>() {
    override fun ActionBuilder.buildActions(detailPane: DetailPane<*>) {
        addAction(getString("action.edit")) {
            constant?.let { constant ->
                edit(constant, detailPane)
            }
        }
    }

    fun edit(constant: T, detailPane: DetailPane<*>) {
        val value = toString(constant)
        val newValue = JOptionPane.showInputDialog(
                detailPane.getParentWindow(),
                labelText,
                title, JOptionPane.QUESTION_MESSAGE, null, null, value
        ) as String?
        if (newValue != null && value != newValue) {
            try {
                setValueFromString(constant, newValue)
                detailPane.modified()
            } catch (e: Exception) {
                GUIHelper.showMessage(detailPane, getString("message.invalid.input"), AlertType.ERROR)
            }
        }
    }

    protected abstract val labelText: String
    protected abstract val title: String

    protected abstract fun setValueFromString(constant: T, newValue: String)
    protected open fun toString(constant: T) = constant.verbose
}

class ConstantUtf8Editor : DirectConstantEditor<ConstantUtf8Info>() {
    override val labelText get() = getString("key.edit.string.label")
    override val title get() = getString("key.edit.string.title")

    override fun setValueFromString(constant: ConstantUtf8Info, newValue: String) {
        constant.string = newValue
    }
}

class ConstantIntegerEditor : DirectConstantEditor<ConstantIntegerInfo>() {
    override val labelText get() = getString("key.edit.integer.label")
    override val title get() = getString("key.edit.integer.title")

    override fun setValueFromString(constant: ConstantIntegerInfo, newValue: String) {
        constant.int = newValue.toInt()
    }
}

class ConstantLongEditor : DirectConstantEditor<ConstantLongInfo>() {
    override val labelText get() = getString("key.edit.long.label")
    override val title get() = getString("key.edit.long.title")

    override fun setValueFromString(constant: ConstantLongInfo, newValue: String) {
        constant.long = newValue.toLong()
    }
}

class ConstantFloatEditor : DirectConstantEditor<ConstantFloatInfo>() {
    override val labelText get() = getString("key.edit.float.label")
    override val title get() = getString("key.edit.float.title")

    override fun setValueFromString(constant: ConstantFloatInfo, newValue: String) {
        constant.float = newValue.toFloat()
    }
}

class ConstantDoubleEditor : DirectConstantEditor<ConstantDoubleInfo>() {
    override val labelText get() = getString("key.edit.double.label")
    override val title get() = getString("key.edit.double.title")

    override fun setValueFromString(constant: ConstantDoubleInfo, newValue: String) {
        constant.double = newValue.toDouble()
    }
}

abstract class ConstantDelegatesEditor<T : Constant> : ConstantEditor<T>() {
    abstract fun ConstantEditSpecBuilder<T>.buildDelegateSpecs()

    override fun ActionBuilder.buildActions(detailPane: DetailPane<*>) {
        val editSpecBuilder = ConstantEditSpecBuilderImpl<T>()
        editSpecBuilder.buildDelegateSpecs()
        for (delegateSpec in editSpecBuilder.result) {
            addAction(delegateSpec.name) {
                constant?.let { constant ->
                    buildDelegateAction(delegateSpec, constant, detailPane)
                }
            }
        }
    }

    private fun buildDelegateAction(delegateSpec: ConstantEditSpec<T>, constant: T, detailPane: DetailPane<*>) {
        when (delegateSpec) {
            is ConstantDelegateSpec<T> -> editDelegate(delegateSpec.delegateProvider(constant), detailPane)
            is ConstantEnumSpec<T, *> -> {
                val value = delegateSpec.getter(constant)
                val newValue = askForEnumValue(
                        delegateSpec.enumClass.enumConstants,
                        value,
                        delegateSpec.labelText,
                        delegateSpec.title,
                        detailPane
                )
                if (newValue != null && value != newValue) {
                    getUntypedSetter(delegateSpec)(constant, newValue)
                    detailPane.modified()
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getUntypedSetter(delegateSpec: ConstantEnumSpec<T, *>) = delegateSpec.setter as T.(Any) -> Unit

    protected fun editDelegate(delegate: Constant, detailPane: DetailPane<*>) {
        when (delegate) {
            is ConstantUtf8Info -> ConstantUtf8Editor().edit(delegate, detailPane)
            is ConstantIntegerInfo -> ConstantIntegerEditor().edit(delegate, detailPane)
            is ConstantLongInfo -> ConstantLongEditor().edit(delegate, detailPane)
            is ConstantFloatInfo -> ConstantFloatEditor().edit(delegate, detailPane)
            is ConstantDoubleInfo -> ConstantDoubleEditor().edit(delegate, detailPane)
            is ConstantNameInfo -> ConstantNameEditor().edit(delegate, detailPane)
            else -> GUIHelper.showMessage(
                    detailPane,
                    getString("message.constant.pool.type.edit.error", delegate.javaClass.name),
                    AlertType.WARNING
            )
        }
    }
}

sealed class ConstantEditSpec<T : Constant>(val name: String)
class ConstantDelegateSpec<T : Constant>(name: String, val delegateProvider: (T) -> Constant) : ConstantEditSpec<T>(name)
class ConstantEnumSpec<T : Constant, E : Enum<E>>(
        name: String,
        val enumClass: Class<E>,
        val labelText: String,
        val title: String,
        val getter: T.() -> E,
        val setter: T.(E) -> Unit
) : ConstantEditSpec<T>(name)

interface ConstantEditSpecBuilder<T : Constant> {
    fun addDelegateSpec(@Nls name: String, delegateProvider: (T) -> Constant)
    fun <E : Enum<E>> addEnumSpec(
            @Nls name: String,
            enumClass: Class<E>,
            @Nls labelText: String,
            @Nls title: String,
            getter: T.() -> E,
            setter: T.(E) -> Unit
    )
}

class ConstantEditSpecBuilderImpl<T : Constant> : ConstantEditSpecBuilder<T> {
    private val delegateSpecs = mutableListOf<ConstantEditSpec<T>>()
    override fun addDelegateSpec(name: String, delegateProvider: (T) -> Constant) {
        delegateSpecs.add(ConstantDelegateSpec(name, delegateProvider))
    }

    override fun <E : Enum<E>> addEnumSpec(
            name: String,
            enumClass: Class<E>,
            labelText: String,
            title: String,
            getter: T.() -> E,
            setter: T.(E) -> Unit
    ) {
        delegateSpecs.add(ConstantEnumSpec(name, enumClass, labelText, title, getter, setter))
    }

    val result: List<ConstantEditSpec<T>> get() = delegateSpecs
}

abstract class SingleConstantDelegateEditor<T : Constant> : ConstantDelegatesEditor<T>() {
    override fun ConstantEditSpecBuilder<T>.buildDelegateSpecs() {
        addDelegateSpec(getString("action.edit")) {
            getDelegate(it)
        }
    }

    abstract fun getDelegate(constant: T): Constant

    fun edit(constant: T, detailPane: DetailPane<*>) {
        editDelegate(getDelegate(constant), detailPane)
    }
}

class ConstantStringEditor : SingleConstantDelegateEditor<ConstantStringInfo>() {
    override fun getDelegate(constant: ConstantStringInfo) = constant.utf8Constant
}

class ConstantNameEditor : SingleConstantDelegateEditor<ConstantNameInfo>() {
    override fun getDelegate(constant: ConstantNameInfo) = constant.nameConstant
}

class ConstantMethodTypeEditor : SingleConstantDelegateEditor<ConstantMethodTypeInfo>() {
    override fun getDelegate(constant: ConstantMethodTypeInfo) = constant.typeConstant
}

class ConstantReferenceEditor : ConstantDelegatesEditor<ConstantReference>() {
    override fun ConstantEditSpecBuilder<ConstantReference>.buildDelegateSpecs() {
        addDelegateSpec(getString("menu.class.name")) {
            it.classInfo
        }
        addDelegateSpec(getString("menu.name")) {
            it.nameAndTypeInfo.nameInfo
        }
        addDelegateSpec(getString("menu.type")) {
            it.nameAndTypeInfo.descriptorInfo
        }
    }
}

class ConstantNameAndTypeEditor : ConstantDelegatesEditor<ConstantNameAndTypeInfo>() {
    override fun ConstantEditSpecBuilder<ConstantNameAndTypeInfo>.buildDelegateSpecs() {
        addDelegateSpec(getString("menu.name")) {
            it.nameInfo
        }
        addDelegateSpec(getString("menu.descriptor")) {
            it.descriptorInfo
        }
    }
}

class ConstantMethodHandleEditor : ConstantDelegatesEditor<ConstantMethodHandleInfo>() {
    override fun ConstantEditSpecBuilder<ConstantMethodHandleInfo>.buildDelegateSpecs() {
        addEnumSpec(
                getString("menu.reference.kind"),
                MethodHandleType::class.java,
                getString("key.edit.reference.kind.label"),
                getString("key.edit.reference.kind.title"),
                { type },
                { type -> this.type = type }
        )
        addDelegateSpec(getString("menu.class.name")) {
            it.referenceConstant.classInfo
        }
        addDelegateSpec(getString("menu.method.name")) {
            it.referenceConstant.nameAndTypeInfo.nameInfo
        }
        addDelegateSpec(getString("menu.type")) {
            it.referenceConstant.nameAndTypeInfo.descriptorInfo
        }
    }
}

class ConstantDynamicEditor : ConstantDelegatesEditor<ConstantDynamic>() {
    override fun ConstantEditSpecBuilder<ConstantDynamic>.buildDelegateSpecs() {
        addDelegateSpec(getString("menu.name")) {
            it.nameAndTypeInfo.nameInfo
        }
        addDelegateSpec(getString("menu.type")) {
            it.nameAndTypeInfo.descriptorInfo
        }
    }
}