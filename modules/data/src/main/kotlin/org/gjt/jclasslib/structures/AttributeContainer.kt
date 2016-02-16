/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures

import org.gjt.jclasslib.structures.attributes.*
import java.io.DataInput
import java.io.DataOutput

/**
 * Implemented by structures that have attributes.
 */
interface AttributeContainer {

    /**
     * Attributes of this structure.
     */
    var attributes: Array<AttributeInfo>

    /**
     * Find an attribute of a certain class.
     * @param attributeClass the class of the attribute
     * @return the found attribute, null if not found
     */
    fun findAttribute(attributeClass: Class<Any>): AttributeInfo? =
            attributes.firstOrNull { it.javaClass == attributeClass }

    /**
     * Get the length of all attributes as a number of bytes.
     */
    val totalAttributesLength: Int
        get() = attributes.sumBy { it.getAttributeLength() }


    /**
     * Read the attributes of this structure from the given DataInput.
     * @param input the DataInput from which to read
     */
    fun AttributeContainer.readAttributes(input: DataInput, classFile: ClassFile) {
        val attributesCount = input.readUnsignedShort()
        if (java.lang.Boolean.getBoolean(SYSTEM_PROPERTY_SKIP_ATTRIBUTES)) {
            input.skipBytes(2)
            input.skipBytes(input.readInt())
        } else {
            attributes = Array(attributesCount) {
                val attributeNameIndex = input.readUnsignedShort()
                val attributeLength = input.readInt()
                val cpInfoName = classFile.getConstantPoolUtf8Entry(attributeNameIndex)
                create(attributeLength, cpInfoName.string, input, classFile).apply {
                    this.attributeNameIndex = attributeNameIndex
                    if (this !is AnnotationDefaultAttribute) {
                        read(input);
                    }
                }
            }
            if (isDebug) debug("read $attributesCount attributes")
        }
    }

    private fun create(attributeLength: Int, attributeName: String, input: DataInput, classFile: ClassFile): AttributeInfo = when (attributeName) {
        ConstantValueAttribute.ATTRIBUTE_NAME -> ConstantValueAttribute(classFile)
        CodeAttribute.ATTRIBUTE_NAME -> CodeAttribute(classFile)
        ExceptionsAttribute.ATTRIBUTE_NAME -> ExceptionsAttribute(classFile)
        InnerClassesAttribute.ATTRIBUTE_NAME -> InnerClassesAttribute(classFile)
        SyntheticAttribute.ATTRIBUTE_NAME -> SyntheticAttribute(classFile)
        SourceFileAttribute.ATTRIBUTE_NAME -> SourceFileAttribute(classFile)
        LineNumberTableAttribute.ATTRIBUTE_NAME -> LineNumberTableAttribute(classFile)
        LocalVariableTableAttribute.ATTRIBUTE_NAME -> LocalVariableTableAttribute(classFile)
        DeprecatedAttribute.ATTRIBUTE_NAME -> DeprecatedAttribute(classFile)
        EnclosingMethodAttribute.ATTRIBUTE_NAME -> EnclosingMethodAttribute(classFile)
        SignatureAttribute.ATTRIBUTE_NAME -> SignatureAttribute(classFile)
        LocalVariableTypeTableAttribute.ATTRIBUTE_NAME -> LocalVariableTypeTableAttribute(classFile)
        RuntimeVisibleAnnotationsAttribute.ATTRIBUTE_NAME -> RuntimeVisibleAnnotationsAttribute(classFile)
        RuntimeInvisibleAnnotationsAttribute.ATTRIBUTE_NAME -> RuntimeInvisibleAnnotationsAttribute(classFile)
        RuntimeVisibleParameterAnnotationsAttribute.ATTRIBUTE_NAME -> RuntimeVisibleParameterAnnotationsAttribute(classFile)
        RuntimeInvisibleParameterAnnotationsAttribute.ATTRIBUTE_NAME -> RuntimeInvisibleParameterAnnotationsAttribute(classFile)
        RuntimeVisibleTypeAnnotationsAttribute.ATTRIBUTE_NAME -> RuntimeVisibleTypeAnnotationsAttribute(classFile)
        RuntimeInvisibleTypeAnnotationsAttribute.ATTRIBUTE_NAME -> RuntimeInvisibleTypeAnnotationsAttribute(classFile)
        AnnotationDefaultAttribute.ATTRIBUTE_NAME -> AnnotationDefaultAttribute(classFile, input)
        BootstrapMethodsAttribute.ATTRIBUTE_NAME -> BootstrapMethodsAttribute(classFile)
        StackMapTableAttribute.ATTRIBUTE_NAME -> StackMapTableAttribute(classFile)
        MethodParametersAttribute.ATTRIBUTE_NAME -> MethodParametersAttribute(classFile)
        else -> UnknownAttribute(attributeLength, classFile)
    }

    /**
     * Write the attributes of this structure to the given DataOutput.
     * @param output the DataOutput to which to write
     */
    fun AttributeContainer.writeAttributes(output: DataOutput) {
        val attributesCount = attributes.size
        output.writeShort(attributesCount)
        attributes.forEach {
            output.writeShort(it.attributeNameIndex)
            output.writeInt(it.getAttributeLength())
            it.write(output)
        }
        if (isDebug) debug("wrote $attributesCount attributes")
    }
}

