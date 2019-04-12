/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.elementvalues

import org.gjt.jclasslib.structures.ClassFileEnum
import org.gjt.jclasslib.structures.Lookup

@Suppress("NOT_DOCUMENTED")
enum class ElementValueType(val charTag: Char, val verbose: String) : ClassFileEnum {
    BYTE('B', "byte"),
    CHAR('C', "String"),
    DOUBLE('D', "double"),
    FLOAT('F', "float"),
    INT('I', "int"),
    LONG('J', "long"),
    SHORT('S', "short"),
    BOOL('Z', "boolean"),
    STRING('s', "String"),
    ENUM('e', "Enum"),
    CLASS('c', "Class"),
    ARRAY('[', "Array"),
    ANNOTATION('@', "Annotation");

    override val tag: Int
        get() = charTag.toInt()

    fun createEntry(): ElementValue = when (this) {
        ENUM -> EnumElementValue()
        CLASS -> ClassElementValue()
        ANNOTATION -> AnnotationElementValue()
        ARRAY -> ArrayElementValue()
        else -> ConstElementValue(this)
    }

    companion object : Lookup<ElementValueType>(ElementValueType::class.java, "element value type")

}