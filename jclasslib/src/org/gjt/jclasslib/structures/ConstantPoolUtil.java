/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures;

import org.gjt.jclasslib.structures.attributes.*;
import org.gjt.jclasslib.structures.constants.*;

/**
    Utility methods for working on the constant pool of a <tt>ClassFile</tt>
    object.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2002-02-17 17:33:16 $
*/

public class ConstantPoolUtil {
    
    private ConstantPoolUtil() {
    }

    public static int addConstantMethodrefInfo(ClassFile classFile,
                                               String className,
                                               String methodName,
                                               String methodSignature,
                                               int sizeDelta)
    {
        sizeDelta = Math.max(sizeDelta, 6);
        int classIndex = addConstantClassInfo(classFile, className, sizeDelta);
        int nameAndTypeIndex = addConstantNameAndTypeInfo(classFile, methodName, methodSignature, sizeDelta);

        ConstantMethodrefInfo methodrefInfo = new ConstantMethodrefInfo();
        methodrefInfo.setClassFile(classFile);
        methodrefInfo.setClassIndex(classIndex);
        methodrefInfo.setNameAndTypeIndex(nameAndTypeIndex);
        return addConstantPoolEntry(classFile, methodrefInfo, sizeDelta);
    }

    public static int addConstantNameAndTypeInfo(ClassFile classFile,
                                                 String name,
                                                 String descriptor,
                                                 int sizeDelta)
    {
        sizeDelta = Math.max(sizeDelta, 3);
        int nameIndex = addConstantUTF8Info(classFile, name, sizeDelta);
        int descriptorIndex = addConstantUTF8Info(classFile, descriptor, sizeDelta);

        ConstantNameAndTypeInfo nameAndTypeInfo = new ConstantNameAndTypeInfo();
        nameAndTypeInfo.setClassFile(classFile);
        nameAndTypeInfo.setNameIndex(nameIndex);
        nameAndTypeInfo.setDescriptorIndex(descriptorIndex);
        return addConstantPoolEntry(classFile, nameAndTypeInfo, sizeDelta);
    }
    
    public static int addConstantClassInfo(ClassFile classFile,
                                           String className,
                                           int sizeDelta)
    {
        sizeDelta = Math.max(sizeDelta, 2);
        int nameIndex = addConstantUTF8Info(classFile, className, sizeDelta);

        ConstantClassInfo classInfo = new ConstantClassInfo();
        classInfo.setClassFile(classFile);
        classInfo.setNameIndex(nameIndex);
        return addConstantPoolEntry(classFile, classInfo, sizeDelta);
    }

    public static int addConstantUTF8Info(ClassFile classFile,
                                          String string,
                                          int sizeDelta)
    {
        ConstantUtf8Info utf8Info = new ConstantUtf8Info();
        utf8Info.setClassFile(classFile);
        utf8Info.setString(string);
        return addConstantPoolEntry(classFile, utf8Info, sizeDelta);
    }

    public static int addConstantPoolEntry(ClassFile classFile,
                                           CPInfo newEntry,
                                           int sizeDelta)
    {
        CPInfo[] constantPool = classFile.getConstantPool();
        
        int index = classFile.getConstantPoolIndex(newEntry);
        if (index > -1) {
            return index;
        }

        int lastFreeIndex = -1;
        for (lastFreeIndex = constantPool.length - 1;
             lastFreeIndex >= 0 && constantPool[lastFreeIndex] == null;
             lastFreeIndex--) {}
        if (lastFreeIndex == constantPool.length - 1) {
            CPInfo[] newConstantPool = new CPInfo[constantPool.length + Math.max(1, sizeDelta)];
            System.arraycopy(constantPool, 0, newConstantPool, 0, constantPool.length);
            classFile.enlargeConstantPool(newConstantPool);
            constantPool = newConstantPool;
        }
        int newIndex = lastFreeIndex + 1;
        constantPool[newIndex] = newEntry;
        classFile.registerConstantPoolEntry(newIndex);
        return newIndex;
    }

}
