/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.test

import org.gjt.jclasslib.io.ClassFileReader
import org.gjt.jclasslib.io.writeToByteArray
import org.gjt.jclasslib.structures.ConstantPoolUtil
import org.gjt.jclasslib.structures.constants.*
import java.io.ByteArrayInputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ConstantPoolEditingTest {

    @Test
    fun testEqualEntryIsReusedNotAppended() {
        val classFile = readResourceClassFile("/moduleMainClass/module-info.class")
        val originalPoolSize = classFile.constantPool.size
        val existingUtf8 = classFile.constantPool.filterIsInstance<ConstantUtf8Info>().first()

        val index = ConstantPoolUtil.addConstantPoolEntry(classFile, ConstantUtf8Info(classFile).apply {
            string = existingUtf8.string
        })

        assertEquals(classFile.getConstantPoolIndex(existingUtf8), index)
        assertEquals(originalPoolSize, classFile.constantPool.size)
    }

    @Test
    fun testAddingSameEntryTwiceGrowsPoolOnlyOnce() {
        val classFile = readResourceClassFile("/moduleMainClass/module-info.class")
        val originalPoolSize = classFile.constantPool.size

        val index1 = ConstantPoolUtil.addConstantUTF8Info(classFile, "com/example/Added")
        val index2 = ConstantPoolUtil.addConstantUTF8Info(classFile, "com/example/Added")

        assertEquals(index1, index2)
        assertEquals(originalPoolSize + 1, classFile.constantPool.size)
    }

    @Test
    fun testExistingIndicesAreStableAfterGrowth() {
        val classFile = readResourceClassFile("/moduleMainClass/module-info.class")
        val originalEntries = classFile.constantPool.toList()

        ConstantPoolUtil.addConstantUTF8Info(classFile, "com/example/Added")
        ConstantPoolUtil.addConstantPoolEntry(classFile, ConstantLongInfo(classFile).apply { long = 42L })
        ConstantPoolUtil.addConstantClassInfo(classFile, "com/example/Other")

        originalEntries.forEachIndexed { index, entry ->
            assertSame(entry, classFile.constantPool[index])
        }
    }

    @Test
    fun testWideEntriesReserveFollowingPlaceholderSlot() {
        val classFile = readResourceClassFile("/moduleMainClass/module-info.class")

        val longIndex = ConstantPoolUtil.addConstantPoolEntry(classFile, ConstantLongInfo(classFile).apply { long = 1L })
        val doubleIndex = ConstantPoolUtil.addConstantPoolEntry(classFile, ConstantDoubleInfo(classFile).apply { double = 1.0 })

        assertEquals(longIndex + 2, doubleIndex)
        assertSame(ConstantPlaceholder, classFile.constantPool[longIndex + 1])
        assertSame(ConstantPlaceholder, classFile.constantPool[doubleIndex + 1])

        classFile.constantPool.forEachIndexed { index, constant ->
            if (constant === ConstantPlaceholder && index > 0) {
                val previous = classFile.constantPool[index - 1]
                assertTrue(previous is ConstantLongInfo || previous is ConstantDoubleInfo,
                        "placeholder at index $index does not follow a wide entry")
            }
        }
    }

    @Test
    fun testWideEntryOccupiesExtraSlot() {
        val classFile = readResourceClassFile("/moduleMainClass/module-info.class")
        assertEquals(1, ConstantLongInfo(classFile).constantType.extraEntryCount)
        assertEquals(1, ConstantDoubleInfo(classFile).constantType.extraEntryCount)
        assertEquals(0, ConstantUtf8Info(classFile).constantType.extraEntryCount)
    }

    @Test
    fun testMethodrefAdditionIsTransitiveAndIdempotent() {
        val classFile = readResourceClassFile("/moduleMainClass/module-info.class")

        val index1 = ConstantPoolUtil.addConstantMethodrefInfo(classFile, "com/example/Foo", "bar", "()V")
        val sizeAfterFirst = classFile.constantPool.size

        val methodref = classFile.constantPool[index1] as ConstantMethodrefInfo
        val classInfo = classFile.getConstantPoolEntry(methodref.classIndex, ConstantClassInfo::class)
        val nameAndType = classFile.getConstantPoolEntry(methodref.nameAndTypeIndex, ConstantNameAndTypeInfo::class)
        assertEquals("com/example/Foo", classFile.getConstantPoolUtf8Entry(classInfo.nameIndex).string)
        assertEquals("bar", classFile.getConstantPoolUtf8Entry(nameAndType.nameIndex).string)
        assertEquals("()V", classFile.getConstantPoolUtf8Entry(nameAndType.descriptorIndex).string)

        val index2 = ConstantPoolUtil.addConstantMethodrefInfo(classFile, "com/example/Foo", "bar", "()V")
        assertEquals(index1, index2)
        assertEquals(sizeAfterFirst, classFile.constantPool.size)
    }

    @Test
    fun testEditedConstantPoolSurvivesRoundTrip() {
        val classFile = readResourceClassFile("/moduleMainClass/module-info.class")
        val utf8Index = ConstantPoolUtil.addConstantUTF8Info(classFile, "added")
        val longIndex = ConstantPoolUtil.addConstantPoolEntry(classFile, ConstantLongInfo(classFile).apply { long = 123L })

        val bytes = classFile.writeToByteArray()
        val reread = ClassFileReader.readFromInputStream(ByteArrayInputStream(bytes))

        assertEquals(classFile.constantPool.size, reread.constantPool.size)
        assertEquals("added", (reread.constantPool[utf8Index] as ConstantUtf8Info).string)
        assertEquals(123L, (reread.constantPool[longIndex] as ConstantLongInfo).long)
        assertSame(ConstantPlaceholder, reread.constantPool[longIndex + 1])
    }
}
