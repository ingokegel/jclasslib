/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.util.getParentWindow
import org.gjt.jclasslib.util.MenuButton
import org.jetbrains.annotations.Nls
import java.awt.Window
import java.awt.event.ActionEvent
import javax.swing.*

abstract class DataEditor<T : Any> {
    protected var data: T? = null

    abstract fun ActionBuilder.buildActions(detailPane: DetailPane<*>)

    fun createButton(detailPane: KeyValueDetailPane<T>): JButton {
        detailPane.addShowHandler {
            data = it
        }
        val actionBuilder = ActionBuilderImpl()
        actionBuilder.buildActions(detailPane)
        return MenuButton(getEditTitle(), actionBuilder.popupMenu)
    }

    protected fun askForEnumValue(values: Array<*>, selectedValue: Any, name: String, parent: JComponent): Any? =
        JOptionPane.showInputDialog(
                parent.getParentWindow(),
                "$name:",
                getEditTitle(),
                JOptionPane.QUESTION_MESSAGE,
                null,
                values,
                selectedValue
        )

    protected fun askForIntValue(selectedValue: Int, name: String, parent: JComponent): Int? =
        JOptionPane.showInputDialog(
                parent.getParentWindow(),
                "$name:",
                getEditTitle(),
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                selectedValue
        )?.toString()?.toIntOrNull()

    protected fun askForFlags(
            selectedValue: Int,
            validAccessFlags: Set<*>,
            dialogCreator: (Int, Set<*>, Window?, String?) -> FlagsEditDialog<*>, parent: JComponent, delegateName: String? = null
    ): Int? = dialogCreator(selectedValue, validAccessFlags, parent.getParentWindow(), delegateName).select()

    protected fun getEditTitle() = getString("action.edit")
}

interface ActionBuilder {
    fun addAction(@Nls name: String, block: () -> Unit) = addAction(name) {_, _ ->
        block()
    }
    fun addAction(@Nls name: String, actionListener: ExtendedActionListener)
}

fun interface ExtendedActionListener {
    fun actionPerformed(event: ActionEvent, actionName: String)
}

class ActionBuilderImpl : ActionBuilder {
    val popupMenu = JPopupMenu()
    override fun addAction(@Nls name: String, actionListener: ExtendedActionListener) {
        popupMenu.add(JMenuItem(name).apply {
            addActionListener {
                actionListener.actionPerformed(it, name)
            }
        })
    }
}
