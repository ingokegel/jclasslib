/*
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public
License as published by the Free Software Foundation; either
version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures

/**
 * Base class for all attribute structures in the attribute package.
 * @property classFile The class file that this class member is a part of.
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
     * Get the length of this attribute in bytes.
     * @return the length
     */
    abstract fun getAttributeLength(): Int

}
