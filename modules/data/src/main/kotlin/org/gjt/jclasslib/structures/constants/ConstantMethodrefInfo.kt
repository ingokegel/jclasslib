/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.constants

import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.ConstantType
import java.io.DataOutput

/**
 * Describes a CONSTANT_Methodref_info constant pool data structure.
 */
class ConstantMethodrefInfo(classFile: ClassFile) : ConstantReference(classFile) {

    override val constantType: ConstantType
        get() = ConstantType.METHODREF

    override fun writeData(output: DataOutput) {
        output.writeByte(ConstantType.METHODREF.tag)
        super.writeData(output)
    }

}
