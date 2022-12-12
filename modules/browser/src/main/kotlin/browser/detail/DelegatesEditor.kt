/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail.constants

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.browser.detail.AccessFlagsEditDialog
import org.gjt.jclasslib.browser.detail.ActionBuilder
import org.gjt.jclasslib.browser.detail.DataEditor
import org.gjt.jclasslib.browser.detail.FlagsEditDialog
import org.gjt.jclasslib.structures.AccessFlag
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.constants.*
import org.gjt.jclasslib.util.AlertType
import org.gjt.jclasslib.util.alertFacade
import org.jetbrains.annotations.Nls
import java.awt.Window
import kotlin.reflect.KMutableProperty1

abstract class DelegatesEditor<T : Any> : DataEditor<T>() {
    abstract fun DelegateBuilder<T>.buildDelegateSpecs()

    override fun ActionBuilder.buildActions(detailPane: DetailPane<*>) {
        val builder = DelegateBuilderImpl<T>()
        builder.buildDelegateSpecs()
        for (delegateSpec in builder.result) {
            addAction(delegateSpec.name) {
                data?.let { data ->
                    buildDelegateAction(delegateSpec, data, detailPane)
                }
            }
        }
    }

    private fun buildDelegateAction(delegateSpec: DelegateSpec<T>, data: T, detailPane: DetailPane<*>) {
        when (delegateSpec) {
            is ConstantDelegateSpec<T> -> editDelegate(delegateSpec.delegateProvider(data), detailPane, delegateSpec.delegateName)
            is EnumSpec<T, *> -> {
                val value = delegateSpec.getter(data)
                val newValue = askForEnumValue(
                        delegateSpec.enumClass.enumConstants,
                        value,
                        delegateSpec.name,
                        detailPane
                )
                if (newValue != null && value != newValue) {
                    getUntypedSetter(delegateSpec)(data, newValue)
                    detailPane.modified()
                }
            }
            is FlagsSpec<T, *> -> changeValue(data, delegateSpec, detailPane) { value ->
                askForFlags(value, delegateSpec.validFlags, getRawDialogCreator(delegateSpec), detailPane, delegateSpec.name)
            }
            is IntSpec<T> -> changeValue(data, delegateSpec, detailPane) { value ->
                askForIntValue(value, delegateSpec.name, detailPane)
            }
            else -> error(delegateSpec.javaClass.name) // Kotlin compiler does not understand that the above cases are exhaustive
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getRawDialogCreator(delegateSpec: FlagsSpec<T, *>) =
        delegateSpec.dialogCreator as (Int, Set<*>, Window?, String?) -> FlagsEditDialog<*>

    private fun <V> changeValue(data: T, delegateSpec: OptionPaneDelegateSpec<T, V>, detailPane: DetailPane<*>, valueEditor: (V) -> V?) {
        val value = delegateSpec.getter(data)
        val newValue = valueEditor(value)
        if (newValue != null && value != newValue) {
            delegateSpec.setter(data, newValue)
            detailPane.modified()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getUntypedSetter(delegateSpec: EnumSpec<T, *>) = delegateSpec.setter as T.(Any) -> Unit

    protected fun editDelegate(delegate: Constant, detailPane: DetailPane<*>, delegateName: String?) {
        when (delegate) {
            is ConstantUtf8Info -> ConstantUtf8Editor().edit(delegate, detailPane, delegateName)
            is ConstantIntegerInfo -> ConstantIntegerEditor().edit(delegate, detailPane, delegateName)
            is ConstantLongInfo -> ConstantLongEditor().edit(delegate, detailPane, delegateName)
            is ConstantFloatInfo -> ConstantFloatEditor().edit(delegate, detailPane, delegateName)
            is ConstantDoubleInfo -> ConstantDoubleEditor().edit(delegate, detailPane, delegateName)
            is ConstantNameInfo -> ConstantNameEditor().edit(delegate, detailPane, delegateName)
            is ConstantStringInfo -> ConstantStringEditor().edit(delegate, detailPane, delegateName)
            else -> alertFacade.showMessage(
                    detailPane,
                    getString("message.constant.pool.type.edit.error", delegate.javaClass.name),
                    AlertType.WARNING
            )
        }
    }
}

sealed class DelegateSpec<T : Any>(val name: String)
abstract class OptionPaneDelegateSpec<T : Any, V>(
        name: String,
        val getter: T.() -> V,
        val setter: T.(V) -> Unit
) : DelegateSpec<T>(name)

class EnumSpec<T : Any, E : Enum<E>>(
        name: String,
        val enumClass: Class<E>,
        getter: T.() -> E,
        setter: T.(E) -> Unit
) : OptionPaneDelegateSpec<T, E>(name, getter, setter)

open class IntSpec<T: Any>(
        name: String,
        getter: T.() -> Int,
        setter: T.(Int) -> Unit
) : OptionPaneDelegateSpec<T, Int>(name, getter, setter)

class FlagsSpec<T : Any, A>(
        name: String,
        val validFlags: Set<A>,
        val dialogCreator: (Int, Set<A>, Window?, String?) -> FlagsEditDialog<A>,
        getter: T.() -> Int,
        setter: T.(Int) -> Unit
): IntSpec<T>(name, getter, setter)

class ConstantDelegateSpec<T : Any>(val delegateName: String?, val delegateProvider: (T) -> Constant) : DelegateSpec<T>(delegateName ?: getString("action.edit"))

interface DelegateBuilder<T: Any> {
    fun addDelegateSpec(@Nls name: String? = null, delegateProvider: (T) -> Constant)

    fun <E : Enum<E>> addEnumSpec(@Nls name: String, enumClass: Class<E>, property: KMutableProperty1<T, E>) {
        addEnumSpec(name, enumClass, { property.get(this) }, { property.set(this, it) })
    }
    fun <E : Enum<E>> addEnumSpec(@Nls name: String, enumClass: Class<E>, getter: T.() -> E, setter: T.(E) -> Unit)

    fun addIntSpec(@Nls name: String, getter: T.() -> Int, setter: T.(Int) -> Unit)
    fun addIntSpec(@Nls name: String, property: KMutableProperty1<T, Int>) {
        addIntSpec(name, {property.get(this)}, { property.set(this, it)})
    }

    fun <A> addFlagsSpec(@Nls name: String, validFlags: Set<A>, dialogCreator: (Int, Set<A>, Window?, String?) -> FlagsEditDialog<A>, getter: T.() -> Int, setter: T.(Int) -> Unit)
    fun <A> addFlagsSpec(@Nls name: String, validFlags: Set<A>, dialogCreator: (Int, Set<A>, Window?, String?) -> FlagsEditDialog<A>, property: KMutableProperty1<T, Int>) {
        addFlagsSpec(name, validFlags, dialogCreator, {property.get(this)}, { property.set(this, it)})
    }

    fun addAccessFlagsSpec(@Nls name: String, validFlags: Set<AccessFlag>, property: KMutableProperty1<T, Int>) {
        addFlagsSpec(name, validFlags, ::AccessFlagsEditDialog, property)
    }
}

class DelegateBuilderImpl<T : Any> : DelegateBuilder<T> {
    private val delegateSpecs = mutableListOf<DelegateSpec<T>>()
    override fun addDelegateSpec(name: String?, delegateProvider: (T) -> Constant) {
        delegateSpecs.add(ConstantDelegateSpec(name, delegateProvider))
    }

    override fun <E : Enum<E>> addEnumSpec(name: String, enumClass: Class<E>, getter: T.() -> E, setter: T.(E) -> Unit) {
        delegateSpecs.add(EnumSpec(name, enumClass, getter, setter))
    }

    override fun addIntSpec(name: String, getter: T.() -> Int, setter: T.(Int) -> Unit) {
        delegateSpecs.add(IntSpec(name, getter, setter))
    }

    override fun <A> addFlagsSpec(name: String, validFlags: Set<A>, dialogCreator: (Int, Set<A>, Window?, String?) -> FlagsEditDialog<A>, getter: T.() -> Int, setter: T.(Int) -> Unit) {
        delegateSpecs.add(FlagsSpec(name, validFlags, dialogCreator, getter, setter))
    }

    val result: List<DelegateSpec<T>> get() = delegateSpecs
}

abstract class ConstantDelegateEditor<T : Any> : DelegatesEditor<T>() {
    override fun DelegateBuilder<T>.buildDelegateSpecs() {
        addDelegateSpec {
            getConstant(it)
        }
    }

    abstract fun getConstant(constant: T): Constant

    fun edit(data: T, detailPane: DetailPane<*>, delegateName: String?) {
        editDelegate(getConstant(data), detailPane, delegateName)
    }
}
