/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures

/**
 * Describes a field in a ClassFile structure.
 */
class FieldInfo(classFile: ClassFile) : ClassMember(classFile) {

    override fun formatAccessFlagsVerbose(accessFlags: Int): String =
            formatAccessFlagsVerbose(AccessFlag.FIELD_ACCESS_FLAGS, accessFlags)

}
