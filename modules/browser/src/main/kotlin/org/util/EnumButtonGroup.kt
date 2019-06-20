/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import javax.swing.ButtonGroup
import javax.swing.JRadioButton

class EnumButtonGroup<E : Enum<E>>(values: Array<E>, actionListener: (E) -> Unit = {}) : ButtonGroup() {

    val radioButtons = values.toList().associateWith {
        JRadioButton(it.toString()).apply {
            addActionListener {
                actionListener(selectedValue)
            }
        }
    }

    fun getButton(value: E): JRadioButton = radioButtons[value] ?: error("value $value not found in radio button group")

    var selectedValue: E
        get() {
            for ((value, radioButton) in radioButtons) {
                if (radioButton.isSelected) {
                    return value
                }
            }
            throw IllegalStateException()
        }
        set(value) {
            radioButtons[value]?.isSelected = true
        }

    init {
        radioButtons.values.forEach {
            add(it)
        }
        radioButtons.values.firstOrNull()?.isSelected = true
    }
}
