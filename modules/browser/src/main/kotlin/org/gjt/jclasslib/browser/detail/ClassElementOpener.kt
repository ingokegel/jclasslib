/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.browser.NodeType
import org.gjt.jclasslib.browser.config.BrowserPath
import org.gjt.jclasslib.browser.config.CategoryHolder
import org.gjt.jclasslib.browser.config.ReferenceHolder
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.structures.constants.*
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JPanel

class ClassElementOpener(private val detailPane: DetailPane<*>) : JPanel() {

    private var constant: Constant? = null

    private val btnShow: JButton = JButton("Show").apply {
        addActionListener {
            try {
                val classInfo = getClassInfo()
                if (classInfo != null) {
                    val className = classInfo.name.replace('/', '.')
                    detailPane.services.openClassFile(className, getBrowserPath())
                }
            } catch (ex: InvalidByteCodeException) {
                ex.printStackTrace()
            }
        }
    }

    init {
        layout = BorderLayout()
        add(btnShow, BorderLayout.CENTER)
        isOpaque = false
    }

    fun setConstant(constant: Constant) {
        this.constant = constant
        try {
            val buttonText = getButtonText()
            btnShow.apply {
                if (buttonText != null) {
                    isVisible = true
                    text = buttonText
                } else {
                    isVisible = false
                }
            }
        } catch (e: InvalidByteCodeException) {
        }
    }

    private fun getClassInfo(): ConstantClassInfo? = constant?.let { constant ->
        when (constant) {
            is ConstantClassInfo -> constant
            is ConstantReference -> constant.classInfo
            else -> null
        }
    }

    private fun getBrowserPath(): BrowserPath? {
        return constant?.let { constant ->
            when (constant) {
                is ConstantReference -> {
                    val category = getCategory(constant)
                    if (category != null) {
                        BrowserPath().apply {
                            addPathComponent(CategoryHolder(category))
                            val nameAndType = constant.nameAndTypeInfo
                            addPathComponent(ReferenceHolder(nameAndType.name, nameAndType.descriptor))
                        }
                    } else {
                        null
                    }
                }
                else -> null
            }
        }
    }

    private fun getCategory(constant: ConstantReference): NodeType? = when (constant) {
        is ConstantFieldrefInfo -> {
            NodeType.FIELDS
        }
        is ConstantMethodrefInfo, is ConstantInterfaceMethodrefInfo -> {
            NodeType.METHODS
        }
        else -> {
            null
        }
    }

    private fun getButtonText(): String? = constant?.let { constant ->
        when (constant) {
            is ConstantClassInfo -> {
                if (constant.name == detailPane.services.classFile.thisClassName) {
                    null
                } else {
                    "Show class"
                }
            }
            is ConstantFieldrefInfo -> {
                "Show field"
            }
            is ConstantMethodrefInfo -> {
                "Show method"
            }
            is ConstantInterfaceMethodrefInfo -> {
                "Show interface method"
            }
            else -> {
                null
            }
        }
    }
}
