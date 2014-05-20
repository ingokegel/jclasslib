/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures;

import org.gjt.jclasslib.structures.constants.*;

/**
    Utility methods for working on the constant pool of a <tt>ClassFile</tt>
    object.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/

public class ConstantPoolUtil {

    private ConstantPoolUtil() {
    }

    /**
        Add a <tt>ConstantMethodRef</tt> constant pool entry to the constant pool
        of a <tt>ClassFile</tt>.
        @param classFile the class file whose constant pool is to be edited
        @param className the name of the referenced class
        @param methodName the name of the referenced method
        @param methodSignature the signature of the referenced method
        @param sizeDelta the minimum increment by which the array holding the
                         constant pool is to be enlarged. Set to 0 if unsure.
        @return the constant pool index of the added <tt>ConstantMethodRef</tt>
     */
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

    /**
        Add a <tt>ConstantFieldRef</tt> constant pool entry to the constant pool
        of a <tt>ClassFile</tt>.
        @param classFile the class file whose constant pool is to be edited
        @param className the name of the referenced class
        @param fieldName the name of the referenced field
        @param fieldType the type of the referenced field
        @param sizeDelta the minimum increment by which the array holding the
                         constant pool is to be enlarged. Set to 0 if unsure.
        @return the constant pool index of the added <tt>ConstantMethodRef</tt>
     */
    public static int addConstantFieldrefInfo(ClassFile classFile,
                                              String className,
                                              String fieldName,
                                              String fieldType,
                                              int sizeDelta)
    {
        sizeDelta = Math.max(sizeDelta, 6);
        int classIndex = addConstantClassInfo(classFile, className, sizeDelta);
        int nameAndTypeIndex = addConstantNameAndTypeInfo(classFile, fieldName, fieldType, sizeDelta);

        ConstantFieldrefInfo fieldrefInfo = new ConstantFieldrefInfo();
        fieldrefInfo.setClassFile(classFile);
        fieldrefInfo.setClassIndex(classIndex);
        fieldrefInfo.setNameAndTypeIndex(nameAndTypeIndex);
        return addConstantPoolEntry(classFile, fieldrefInfo, sizeDelta);
    }

    /**
        Add a <tt>ConstantNameAndTypeInfo</tt> constant pool entry to the
        constant pool of a <tt>ClassFile</tt>.
        @param classFile the class file whose constant pool is to be edited
        @param name the name
        @param descriptor the descriptor
        @param sizeDelta the minimum increment by which the array holding the
                         constant pool is to be enlarged. Set to 0 if unsure.
        @return the constant pool index of the added <tt>ConstantNameAndTypeInfo</tt>
     */
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

    /**
        Add a <tt>ConstantClassInfo</tt> constant pool entry to the
        constant pool of a <tt>ClassFile</tt>.
        @param classFile the class file whose constant pool is to be edited
        @param className the name of the referenced class
        @param sizeDelta the minimum increment by which the array holding the
                         constant pool is to be enlarged. Set to 0 if unsure.
        @return the constant pool index of the added <tt>ConstantClassInfo</tt>
     */
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

    /**
        Add a <tt>ConstantUTF8Info</tt> constant pool entry to the
        constant pool of a <tt>ClassFile</tt>.
        @param classFile the class file whose constant pool is to be edited
        @param string the string
        @param sizeDelta the minimum increment by which the array holding the
                         constant pool is to be enlarged. Set to 0 if unsure.
        @return the constant pool index of the added <tt>ConstantUTF8Info</tt>
     */
    public static int addConstantUTF8Info(ClassFile classFile,
                                          String string,
                                          int sizeDelta)
    {
        ConstantUtf8Info utf8Info = new ConstantUtf8Info();
        utf8Info.setClassFile(classFile);
        utf8Info.setString(string);
        return addConstantPoolEntry(classFile, utf8Info, sizeDelta);
    }

    /**
        Add a constant pool entry to the
        constant pool of a <tt>ClassFile</tt>.
        @param classFile the class file whose constant pool is to be edited
        @param newEntry the new constant pool entry
        @param sizeDelta the minimum increment by which the array holding the
                         constant pool is to be enlarged. Set to 0 if unsure.
        @return the constant pool index of the added constant pool entry
     */
    public static int addConstantPoolEntry(ClassFile classFile,
                                           CPInfo newEntry,
                                           int sizeDelta)
    {
        CPInfo[] constantPool = classFile.getConstantPool();

        int index = classFile.getConstantPoolIndex(newEntry);
        if (index > -1) {
            return index;
        }

        int lastFreeIndex;
        lastFreeIndex = constantPool.length - 1;
        while (lastFreeIndex >= 0 && constantPool[lastFreeIndex] == null) {
            lastFreeIndex--;
        }
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
