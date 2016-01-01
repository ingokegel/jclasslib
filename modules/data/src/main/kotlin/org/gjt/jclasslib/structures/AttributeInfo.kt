/*
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public
License as published by the Free Software Foundation; either
version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures

import org.gjt.jclasslib.structures.attributes.*

import java.io.DataInput
import java.io.IOException

/**
 * Base class for all attribute structures in the attribute package.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com), [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
abstract class AttributeInfo : AbstractStructureWithAttributes() {

    /**
     * Constant pool index for the name of the attribute.
     */
    var attributeNameIndex: Int = 0

    /**
     * Name of the attribute.
     */
    val name: String
        @Throws(InvalidByteCodeException::class)
        get() = classFile.getConstantPoolUtf8Entry(attributeNameIndex).string

    /**
     * Get the length of this attribute as a number of bytes.
     * @return the length
     */
    abstract fun getAttributeLength(): Int

    companion object {

        /**
         * Set this JVM System property to true to skip reading of all attributes.
         * Some class file operations may fail in this case.
         */
        const val SYSTEM_PROPERTY_SKIP_ATTRIBUTES = "jclasslib.io.skipAttributes"

        /**
         * Factory method for creating AttributeInfo structures.
         *
         * An AttributeInfo of the appropriate subtype from the attributes package
         * is created unless the type of the attribute is unknown in which case an instance of
         * AttributeInfo is returned.
         *
         * Attributes are skipped if the environment variable SYSTEM_PROPERTY_SKIP_ATTRIBUTES
         * is set to true.

         * @param input        the DataInput from which to read the AttributeInfo structure
         * @param classFile the parent class file of the structure to be created
         * @return the new AttributeInfo structure
         */
        fun create(input: DataInput, classFile: ClassFile): AttributeInfo {

            val attributeNameIndex = input.readUnsignedShort()
            val attributeLength = input.readInt()

            val cpInfoName = classFile.getConstantPoolUtf8Entry(attributeNameIndex)
            return create(attributeLength, cpInfoName.string).apply {
                this.attributeNameIndex = attributeNameIndex
                this.classFile = classFile
                this.read(input);
            }
        }

        private fun create(attributeLength: Int, attributeName: String): AttributeInfo = when (attributeName) {
            ConstantValueAttribute.ATTRIBUTE_NAME -> ConstantValueAttribute()
            CodeAttribute.ATTRIBUTE_NAME -> CodeAttribute()
            ExceptionsAttribute.ATTRIBUTE_NAME -> ExceptionsAttribute()
            InnerClassesAttribute.ATTRIBUTE_NAME -> InnerClassesAttribute()
            SyntheticAttribute.ATTRIBUTE_NAME -> SyntheticAttribute()
            SourceFileAttribute.ATTRIBUTE_NAME -> SourceFileAttribute()
            LineNumberTableAttribute.ATTRIBUTE_NAME -> LineNumberTableAttribute()
            LocalVariableTableAttribute.ATTRIBUTE_NAME -> LocalVariableTableAttribute()
            DeprecatedAttribute.ATTRIBUTE_NAME -> DeprecatedAttribute()
            EnclosingMethodAttribute.ATTRIBUTE_NAME -> EnclosingMethodAttribute()
            SignatureAttribute.ATTRIBUTE_NAME -> SignatureAttribute()
            LocalVariableTypeTableAttribute.ATTRIBUTE_NAME -> LocalVariableTypeTableAttribute()
            RuntimeVisibleAnnotationsAttribute.ATTRIBUTE_NAME -> RuntimeVisibleAnnotationsAttribute()
            RuntimeInvisibleAnnotationsAttribute.ATTRIBUTE_NAME -> RuntimeInvisibleAnnotationsAttribute()
            RuntimeVisibleParameterAnnotationsAttribute.ATTRIBUTE_NAME -> RuntimeVisibleParameterAnnotationsAttribute()
            RuntimeInvisibleParameterAnnotationsAttribute.ATTRIBUTE_NAME -> RuntimeInvisibleParameterAnnotationsAttribute()
            RuntimeVisibleTypeAnnotationsAttribute.ATTRIBUTE_NAME -> RuntimeVisibleTypeAnnotationsAttribute()
            RuntimeInvisibleTypeAnnotationsAttribute.ATTRIBUTE_NAME -> RuntimeInvisibleTypeAnnotationsAttribute()
            AnnotationDefaultAttribute.ATTRIBUTE_NAME -> AnnotationDefaultAttribute()
            BootstrapMethodsAttribute.ATTRIBUTE_NAME -> BootstrapMethodsAttribute()
            StackMapTableAttribute.ATTRIBUTE_NAME -> StackMapTableAttribute()
            MethodParametersAttribute.ATTRIBUTE_NAME -> MethodParametersAttribute()
            else -> UnknownAttribute(attributeLength)
        }
    }
}
