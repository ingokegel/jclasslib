/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import com.install4j.runtime.alert.AlertType
import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.constants.*
import org.gjt.jclasslib.util.GUIHelper
import org.gjt.jclasslib.util.GUIHelper.getParentWindow
import javax.swing.JButton
import javax.swing.JOptionPane

interface ConstantEditorFactory<T : Constant> {
    fun createEditor(detailPane: ConstantDetailPane<T>): ConstantEditor<T>
}

abstract class ConstantEditor<T : Constant>(detailPane: ConstantDetailPane<T>) :
        JButton(getString("action.edit")) {

    private var constant: T? = null

    init {
        detailPane.addShowHandler {
            constant = it
        }
        addActionListener {
            constant?.let { constant ->
                val value = constant.verbose
                val newValue = JOptionPane.showInputDialog(
                        getParentWindow(),
                        labelText,
                        title, JOptionPane.QUESTION_MESSAGE, null, null, value
                ) as String?
                if (newValue != null && value != newValue) {
                    try {
                        setValueFromString(constant, newValue)
                        detailPane.modified()
                    } catch (e: Exception) {
                        GUIHelper.showMessage(this, getString("message.invalid.input"), AlertType.ERROR)
                    }
                }
            }
        }
    }

    protected abstract val labelText: String
    protected abstract val title: String

    protected abstract fun setValueFromString(constant: T, newValue: String)
}

class ConstantStringEditor(detailPane: ConstantDetailPane<ConstantUtf8Info>) : ConstantEditor<ConstantUtf8Info>(detailPane) {
    override val labelText get() = getString("key.edit.string.label")
    override val title get() = getString("key.edit.string.title")

    override fun setValueFromString(constant: ConstantUtf8Info, newValue: String) {
        constant.string = newValue
    }

    companion object Factory : ConstantEditorFactory<ConstantUtf8Info> {
        override fun createEditor(detailPane: ConstantDetailPane<ConstantUtf8Info>) = ConstantStringEditor(detailPane)
    }
}

class ConstantIntegerEditor(detailPane: ConstantDetailPane<ConstantIntegerInfo>) : ConstantEditor<ConstantIntegerInfo>(detailPane) {
    override val labelText get() = getString("key.edit.integer.label")
    override val title get() = getString("key.edit.integer.title")

    override fun setValueFromString(constant: ConstantIntegerInfo, newValue: String) {
        constant.int = newValue.toInt()
    }

    companion object Factory : ConstantEditorFactory<ConstantIntegerInfo> {
        override fun createEditor(detailPane: ConstantDetailPane<ConstantIntegerInfo>) = ConstantIntegerEditor(detailPane)
    }
}

class ConstantLongEditor(detailPane: ConstantDetailPane<ConstantLongInfo>) : ConstantEditor<ConstantLongInfo>(detailPane) {
    override val labelText get() = getString("key.edit.long.label")
    override val title get() = getString("key.edit.long.title")

    override fun setValueFromString(constant: ConstantLongInfo, newValue: String) {
        constant.long = newValue.toLong()
    }

    companion object Factory : ConstantEditorFactory<ConstantLongInfo> {
        override fun createEditor(detailPane: ConstantDetailPane<ConstantLongInfo>) = ConstantLongEditor(detailPane)
    }
}

class ConstantFloatEditor(detailPane: ConstantDetailPane<ConstantFloatInfo>) : ConstantEditor<ConstantFloatInfo>(detailPane) {
    override val labelText get() = getString("key.edit.float.label")
    override val title get() = getString("key.edit.float.title")

    override fun setValueFromString(constant: ConstantFloatInfo, newValue: String) {
        constant.float = newValue.toFloat()
    }

    companion object Factory : ConstantEditorFactory<ConstantFloatInfo> {
        override fun createEditor(detailPane: ConstantDetailPane<ConstantFloatInfo>) = ConstantFloatEditor(detailPane)
    }
}

class ConstantDoubleEditor(detailPane: ConstantDetailPane<ConstantDoubleInfo>) : ConstantEditor<ConstantDoubleInfo>(detailPane) {
    override val labelText get() = getString("key.edit.double.label")
    override val title get() = getString("key.edit.double.title")

    override fun setValueFromString(constant: ConstantDoubleInfo, newValue: String) {
        constant.double = newValue.toDouble()
    }

    companion object Factory : ConstantEditorFactory<ConstantDoubleInfo> {
        override fun createEditor(detailPane: ConstantDetailPane<ConstantDoubleInfo>) = ConstantDoubleEditor(detailPane)
    }
}

