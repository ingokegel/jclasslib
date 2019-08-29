/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.ClassFile

class GeneralDetailPane(services: BrowserServices) : KeyValueDetailPane<ClassFile>(ClassFile::class.java, services) {
    override fun addLabels() {
        addDetail("Minor version:") { classFile -> classFile.minorVersion.toString() }
        addDetail("Major version:") { classFile -> "${classFile.majorVersion} [${classFile.majorVersionVerbose}]" }
        addDetail("Constant pool count:") { classFile -> classFile.constantPool.size.toString() }
        addDetail("Access flags:") { classFile -> "${classFile.formattedAccessFlags} [${classFile.accessFlagsVerbose}]" }
        addConstantPoolLink("This class:", ClassFile::thisClass)
        addConstantPoolLink("Super class:", ClassFile::superClass)
        addDetail("Interfaces count:") { classFile -> classFile.interfaces.size.toString() }
        addDetail("Fields count:") { classFile -> classFile.fields.size.toString() }
        addDetail("Methods count:") { classFile -> classFile.methods.size.toString() }
        addDetail("Attributes count:") { classFile -> classFile.attributes.size.toString() }
    }

    override fun hasInsets() = true
}

