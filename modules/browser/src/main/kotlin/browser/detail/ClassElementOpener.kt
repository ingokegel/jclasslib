/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.browser.NodeType
import org.gjt.jclasslib.browser.config.BrowserPath
import org.gjt.jclasslib.browser.config.CategoryHolder
import org.gjt.jclasslib.browser.config.ReferenceHolder
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.Constant
import org.gjt.jclasslib.structures.InvalidByteCodeException
import org.gjt.jclasslib.structures.constants.*
import org.jetbrains.annotations.Nls
import java.awt.BorderLayout
import javax.swing.JButton
import javax.swing.JPanel

class ClassElementOpener(private val detailPane: DetailPane<*>) : JPanel() {

    private var constant: Constant? = null

    private val btnShow: JButton = JButton(getString("action.show")).apply {
        addActionListener {
            try {
                val className = getClassName()
                if (className != null) {
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
        } catch (_: InvalidByteCodeException) {
        }
    }

    private fun getClassName(): String? = constant?.let { constant ->
        when (constant) {
            is ConstantClassInfo -> constant.name.replace('/', '.')
            is ConstantReference -> {
                var currentClassName = constant.classConstant.name.replace("/", ".")
                while (currentClassName != "java.lang.Object") {
                    val classFile = detailPane.services.readClassFile(currentClassName)
                    if (classFile == null || containsReference(classFile, constant)) {
                        break
                    }
                    currentClassName = classFile.superClassName.replace("/", ".")
                }
                currentClassName
            }
            else -> null
        }
    }

    private fun containsReference(classFile: ClassFile, constant: ConstantReference): Boolean {
        val members = when (constant) {
            is ConstantFieldrefInfo -> classFile.fields
            is ConstantMethodrefInfo, is ConstantInterfaceMethodrefInfo -> classFile.methods
            else -> null
        }
        val nameAndTypeConstant = constant.nameAndTypeConstant
        return members?.any { it.name == nameAndTypeConstant.name && it.descriptor == nameAndTypeConstant.descriptor }
            ?: false
    }

    private fun getBrowserPath(): BrowserPath? {
        return constant?.let { constant ->
            when (constant) {
                is ConstantReference -> {
                    val category = getCategory(constant)
                    if (category != null) {
                        BrowserPath().apply {
                            addPathComponent(CategoryHolder(category))
                            val nameAndType = constant.nameAndTypeConstant
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

    @Nls
    private fun getButtonText(): String? = constant?.let { constant ->
        when (constant) {
            is ConstantClassInfo -> {
                if (constant.name == detailPane.services.classFile.thisClassName) {
                    null
                } else {
                    getString("action.show.class")
                }
            }
            is ConstantFieldrefInfo -> {
                getString("action.show.field")
            }
            is ConstantMethodrefInfo -> {
                getString("action.show.method")
            }
            is ConstantInterfaceMethodrefInfo -> {
                getString("action.show.interface.method")
            }
            else -> {
                null
            }
        }
    }
}
