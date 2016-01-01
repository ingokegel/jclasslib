/*
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public
License as published by the Free Software Foundation; either
version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures

import org.gjt.jclasslib.structures.attributes.*
import java.io.DataInput

/**
 * Base class for all attribute structures in the attribute package.

 * @author [Ingo Kegel](mailto:jclasslib@ej-technologies.com), [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
abstract class AttributeInfo(protected val classFile: ClassFile) : Structure() {

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
            return create(attributeLength, cpInfoName.string, classFile).apply {
                this.attributeNameIndex = attributeNameIndex
                this.read(input);
            }
        }

        private fun create(attributeLength: Int, attributeName: String, classFile : ClassFile): AttributeInfo = when (attributeName) {
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
            AnnotationDefaultAttribute.ATTRIBUTE_NAME -> AnnotationDefaultAttribute(classFile)
            BootstrapMethodsAttribute.ATTRIBUTE_NAME -> BootstrapMethodsAttribute(classFile)
            StackMapTableAttribute.ATTRIBUTE_NAME -> StackMapTableAttribute(classFile)
            MethodParametersAttribute.ATTRIBUTE_NAME -> MethodParametersAttribute(classFile)
            else -> UnknownAttribute(attributeLength, classFile)
        }
    }
}
