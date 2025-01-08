/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.test

import org.gjt.jclasslib.structures.attributes.ModuleMainClassAttribute
import org.gjt.jclasslib.structures.constants.ConstantClassInfo
import org.junit.jupiter.api.Assertions.assertEquals
import kotlin.test.Test

class AttributeTests {
    @Test
    fun testModuleMainClassAttributes() {
        readResourceClassFile("/moduleMainClass/module-info.class").apply {
            val attribute = requireNotNull(findAttribute(ModuleMainClassAttribute::class))
            assertEquals(getConstantPoolEntry(attribute.mainClassIndex, ConstantClassInfo::class).name, "test/main/Main")
        }
    }
}

