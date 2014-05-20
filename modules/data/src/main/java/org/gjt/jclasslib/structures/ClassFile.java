/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures;

import org.gjt.jclasslib.io.Log;
import org.gjt.jclasslib.structures.constants.ConstantLargeNumeric;
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.HashMap;

/**
 * The class file structure in which all other structures are hooked up.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>, <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class ClassFile extends AbstractStructureWithAttributes {

    /**
     * Set this JVM System property to true to skip reading of constant pool
     * entries. This is not advisable, since most sunsequent operations on the
     * class file structure will fail.
     */
    public static final String SYSTEM_PROPERTY_SKIP_CONSTANT_POOL = "jclasslib.io.skipConstantPool";

    private static final int MAGIC_NUMBER = 0xcafebabe;

    private final boolean skipConstantPool;

    private int minorVersion;
    private int majorVersion;
    private CPInfo[] constantPool;
    private HashMap<CPInfo, Integer> constantPoolEntryToIndex = new HashMap<CPInfo, Integer>();
    private int accessFlags;
    private int thisClass;
    private int superClass;
    private int[] interfaces;
    private FieldInfo[] fields;
    private MethodInfo[] methods;


    /**
     * Constructor.
     */
    public ClassFile() {
        skipConstantPool = Boolean.getBoolean(SYSTEM_PROPERTY_SKIP_CONSTANT_POOL);
        setClassFile(this);
    }

    /**
     * Get the minor version of the class file format.
     *
     * @return the minor version
     */
    public int getMinorVersion() {
        return minorVersion;
    }

    /**
     * Set the minor version of the class file format.
     *
     * @param minorVersion the minor version
     */
    public void setMinorVersion(int minorVersion) {
        this.minorVersion = minorVersion;
    }

    /**
     * Get the major version of the class file format.
     *
     * @return the major version
     */
    public int getMajorVersion() {
        return majorVersion;
    }

    /**
     * Get the verbose major version of the class file format.
     *
     * @return the major version as text
     */
    public String getMajorVersionVerbose() {
        switch (majorVersion) {
            case 45:
                return "1.1";
            case 46:
                return "1.2";
            case 47:
                return "1.3";
            case 48:
                return "1.4";
            case 49:
                return "1.5";
            case 50:
                return "1.6";
            case 51:
                return "1.7";
            case 52:
                return "1.8";
            case 53:
                return "1.9";
            default:
                return "unknown value " + majorVersion;
        }
    }

    /**
     * Set the major version of the class file format.
     *
     * @param majorVersion the major version
     */
    public void setMajorVersion(int majorVersion) {
        this.majorVersion = majorVersion;
    }

    /**
     * Get the array with all constant pool entries.
     *
     * @return the array
     */
    public CPInfo[] getConstantPool() {
        return constantPool;
    }

    /**
     * Get the index of an equivalent constant pool entry.
     *
     * @param cpInfo the constant pool entry
     * @return the index, -1 if no equivalent constant pool entry can be found
     */
    public int getConstantPoolIndex(CPInfo cpInfo) {
        Integer index = constantPoolEntryToIndex.get(cpInfo);
        if (index != null) {
            return index;
        } else {
            return -1;
        }
    }

    /**
     * Set the array with all constant pool entries. An internal hash map
     * will need to be recalulated. If you add to the end of the constant
     * pool, use <tt>enlargeConstantPool</tt>.
     *
     * @param constantPool the array
     */
    public void setConstantPool(CPInfo[] constantPool) {
        this.constantPool = constantPool;
        for (int i = 0; i < constantPool.length; i++) {
            constantPoolEntryToIndex.put(constantPool[i], i);
        }
    }

    /**
     * Set the array with all constant pool entries where the new array
     * of constant pool entries starts with the old constant pool. If
     * you delete entries, use <tt>setConstantPool</tt>.
     *
     * @param enlargedConstantPool the array
     */
    public void enlargeConstantPool(CPInfo[] enlargedConstantPool) {
        int startIndex = constantPool == null ? 0 : constantPool.length;
        this.constantPool = enlargedConstantPool;
        for (int i = startIndex; i < constantPool.length; i++) {
            if (constantPool[i] != null) {
                constantPoolEntryToIndex.put(constantPool[i], i);
            }
        }
    }

    /**
     * Register the constant pool entry at a given index, so that it can
     * be found through the <tt>getConstantPoolIndex</tt> method.
     *
     * @param index the index
     */
    public void registerConstantPoolEntry(int index) {
        constantPoolEntryToIndex.put(constantPool[index], index);
    }

    /**
     * Unregister the constant pool entry at a given index, so that it can
     * no longer be found through the <tt>getConstantPoolIndex</tt> method.
     *
     * @param index the index
     */
    public void unregisterConstantPoolEntry(int index) {
        constantPoolEntryToIndex.remove(constantPool[index]);
    }

    /**
     * Get the access flags of this class.
     *
     * @return the access flags
     */
    public int getAccessFlags() {
        return accessFlags;
    }

    /**
     * Set the access flags of this class.
     *
     * @param accessFlags the access flags
     */
    public void setAccessFlags(int accessFlags) {
        this.accessFlags = accessFlags;
    }

    /**
     * Get the constant pool index of this class.
     *
     * @return the index
     */
    public int getThisClass() {
        return thisClass;
    }

    /**
     * Set the constant pool index of this class.
     *
     * @param thisClass the index
     */
    public void setThisClass(int thisClass) {
        this.thisClass = thisClass;
    }

    /**
     * Get the name of this class.
     *
     * @return the name
     * @throws InvalidByteCodeException
     */
    public String getThisClassName() throws InvalidByteCodeException {
        return getConstantPoolEntryName(getThisClass());
    }

    /**
     * Get the constant pool index of the super class of this class.
     *
     * @return the index
     */
    public int getSuperClass() {
        return superClass;
    }

    /**
     * Set the constant pool index of the super class of this class.
     *
     * @param superClass the index
     */
    public void setSuperClass(int superClass) {
        this.superClass = superClass;
    }

    /**
     * Get the name of the super class.
     *
     * @return the name
     * @throws InvalidByteCodeException
     */
    public String getSuperClassName() throws InvalidByteCodeException {
        return getConstantPoolEntryName(getSuperClass());
    }

    /**
     * Get the array with the constant pool entries of all interfaces.
     *
     * @return the array
     */
    public int[] getInterfaces() {
        return interfaces;
    }

    /**
     * Set the array with the constant pool entries of all interfaces.
     *
     * @param interfaces the array
     */
    public void setInterfaces(int[] interfaces) {
        this.interfaces = interfaces;
    }

    /**
     * Get the array with the <tt>FieldInfo</tt> structures for the fields of this class.
     *
     * @return the array
     */
    public FieldInfo[] getFields() {
        return fields;
    }

    /**
     * Set the array with the <tt>FieldInfo</tt> structures for the fields of this class.
     *
     * @param fields the array
     */
    public void setFields(FieldInfo[] fields) {
        this.fields = fields;
    }

    /**
     * Get the array with the <tt>MethodInfo</tt> structures for the methods of this class.
     *
     * @return the array
     */
    public MethodInfo[] getMethods() {
        return methods;
    }

    /**
     * Set the array with the <tt>MethodInfo</tt> structures for the methods of this class.
     *
     * @param methods the array
     */
    public void setMethods(MethodInfo[] methods) {
        this.methods = methods;
    }

    /**
     * Get the the access flags of this class as a hex string.
     *
     * @return the hex string
     */
    public String getFormattedAccessFlags() {
        return printAccessFlags(accessFlags);
    }

    /**
     * Get the verbose description of the access flags of this class.
     *
     * @return the description
     */
    public String getAccessFlagsVerbose() {
        return printAccessFlagsVerbose(accessFlags);
    }

    /**
     * Get the <tt>ConstantUtf8Info</tt> constant pool entry at the specified index.
     *
     * @param index the index
     * @return the constant pool entry
     * @throws InvalidByteCodeException if the entry is not a <tt>ConstantUtf8Info</tt>
     */
    public ConstantUtf8Info getConstantPoolUtf8Entry(int index)
            throws InvalidByteCodeException {

        return (ConstantUtf8Info)getConstantPoolEntry(index, ConstantUtf8Info.class);
    }

    /**
     * Get the constant pool entry at the specified index and cast it to a specified class.
     *
     * @param index      the index
     * @param entryClass the required subtype of <tt>CPInfo</tt>
     * @return the constant pool entry
     * @throws InvalidByteCodeException if the entry is of a different class than expected
     */
    public CPInfo getConstantPoolEntry(int index, Class<? extends CPInfo> entryClass)
            throws InvalidByteCodeException {

        if (!checkValidConstantPoolIndex(index)) {
            return null;
        }

        CPInfo cpInfo = constantPool[index];

        if (cpInfo == null) {
            return null;
        }

        if (entryClass.isAssignableFrom(cpInfo.getClass())) {
            return cpInfo;
        } else {
            throw new InvalidByteCodeException("constant pool entry at " + index +
                    " is not assignable to " +
                    entryClass.getName());
        }
    }

    /**
     * Get an approximate verbose description of the content of the constant pool entry
     * at the specified index.
     *
     * @param index the index
     * @return the description
     * @throws InvalidByteCodeException if the entry is invalid
     */
    public String getConstantPoolEntryName(int index)
            throws InvalidByteCodeException {

        if (!checkValidConstantPoolIndex(index)) {
            return null;
        }

        CPInfo cpInfo = constantPool[index];
        if (cpInfo == null) {
            return "invalid constant pool index";
        } else {
            return cpInfo.getVerbose();
        }
    }

    /**
     * Get the index of a field for given field name and signature.
     *
     * @param name       the field name.
     * @param descriptor the signature.
     * @return the index or <tt>-1</tt> if not found.
     * @throws InvalidByteCodeException
     */
    public int getFieldIndex(String name, String descriptor) throws InvalidByteCodeException {

        for (int i = 0; i < fields.length; i++) {
            FieldInfo field = fields[i];
            if (field.getName().equals(name) && field.getDescriptor().equals(descriptor)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get the <tt>FieldInfo</tt> for given field name and signature.
     *
     * @param name       the field name.
     * @param descriptor the signature.
     * @return the <tt>FieldInfo</tt> or <tt>null</tt> if not found.
     * @throws InvalidByteCodeException
     */
    public FieldInfo getField(String name, String descriptor) throws InvalidByteCodeException {

        int index = getFieldIndex(name, descriptor);
        if (index < 0) {
            return null;
        } else {
            return fields[index];
        }
    }

    /**
     * Get the index of a method for given method name and signature.
     *
     * @param name       the method name.
     * @param descriptor the signature.
     * @return the index or <tt>-1</tt> if not found.
     * @throws InvalidByteCodeException
     */
    public int getMethodIndex(String name, String descriptor) throws InvalidByteCodeException {

        for (int i = 0; i < methods.length; i++) {
            MethodInfo method = methods[i];
            if (method.getName().equals(name) && method.getDescriptor().equals(descriptor)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get the <tt>MethodInfo</tt> for given method name and signature.
     *
     * @param name       the method name.
     * @param descriptor the signature.
     * @return the <tt>MethodInfo</tt> or <tt>null</tt> if not found.
     * @throws InvalidByteCodeException
     */
    public MethodInfo getMethod(String name, String descriptor) throws InvalidByteCodeException {

        int index = getMethodIndex(name, descriptor);
        if (index < 0) {
            return null;
        } else {
            return methods[index];
        }
    }

    public void read(DataInput in)
            throws InvalidByteCodeException, IOException {

        readMagicNumber(in);
        readVersion(in);
        readConstantPool(in);
        readAccessFlags(in);
        readThisClass(in);
        readSuperClass(in);
        readInterfaces(in);
        readFields(in);
        readMethods(in);
        readAttributes(in);
    }

    public void write(DataOutput in)
            throws InvalidByteCodeException, IOException {

        writeMagicNumber(in);
        writeVersion(in);
        writeConstantPool(in);
        writeAccessFlags(in);
        writeThisClass(in);
        writeSuperClass(in);
        writeInterfaces(in);
        writeFields(in);
        writeMethods(in);
        writeAttributes(in);

    }

    private boolean checkValidConstantPoolIndex(int index) {

        if (index < 1 || index >= constantPool.length) {
            return false;
        }
        return true;

    }

    private void readMagicNumber(DataInput in)
            throws InvalidByteCodeException, IOException {

        int magicNumber = in.readInt();
        if (magicNumber != MAGIC_NUMBER) {
            throw new InvalidByteCodeException("Invalid magic number 0x" +
                    Integer.toHexString(magicNumber) +
                    " instead of 0x" +
                    Integer.toHexString(MAGIC_NUMBER));
        }

        if (debug) debug("read magic number");
    }

    private void writeMagicNumber(DataOutput out) throws IOException {

        out.writeInt(MAGIC_NUMBER);
        if (debug) debug("wrote magic number");
    }

    private void readVersion(DataInput in) throws IOException {

        minorVersion = in.readUnsignedShort();
        if (debug) debug("read minor version " + minorVersion);

        majorVersion = in.readUnsignedShort();
        if (debug) debug("read major version " + majorVersion);

        checkMajorVersion(majorVersion);
    }

    private void writeVersion(DataOutput out) throws IOException {

        out.writeShort(minorVersion);
        if (debug) debug("wrote minor version " + minorVersion);

        out.writeShort(majorVersion);
        if (debug) debug("wrote major version " + majorVersion);

        checkMajorVersion(majorVersion);
    }

    private void readConstantPool(DataInput in)
            throws InvalidByteCodeException, IOException {

        constantPoolEntryToIndex.clear();
        int constantPoolCount = in.readUnsignedShort();
        if (debug) debug("read constant pool count " + constantPoolCount);

        constantPool = new CPInfo[constantPoolCount];

        // constantPool has effective length constantPoolCount - 1
        // constantPool[0] defaults to null
        for (int i = 1; i < constantPoolCount; i++) {
            if (skipConstantPool) {
                // see below for i++
                i += CPInfo.skip(in);
            } else {
                // create CPInfos via factory method since the actual type
                // of the constant is not yet known
                if (debug) debug("reading constant pool entry " + i);
                constantPool[i] = CPInfo.create(in, this);
                constantPoolEntryToIndex.put(constantPool[i], i);
                if (constantPool[i] instanceof ConstantLargeNumeric) {
                    // CONSTANT_Double_info and CONSTANT_Long_info take 2 constant
                    // pool entries, the second entry is unusable (design mistake)
                    i++;
                }
            }
        }
    }

    private void writeConstantPool(DataOutput out)
            throws InvalidByteCodeException, IOException {

        int lastFreeIndex;
        lastFreeIndex = getLength(constantPool) - 1;
        while (lastFreeIndex >= 0 && constantPool[lastFreeIndex] == null) {
            lastFreeIndex--;
        }

        out.writeShort(lastFreeIndex + 1);
        if (debug) debug("wrote constant pool count " + (lastFreeIndex + 1));

        // constantPool[0] defaults to null and is not written into the class file
        for (int i = 1; i <= lastFreeIndex; i++) {
            if (constantPool[i] == null) {
                throw new InvalidByteCodeException("constant pool entry " + i + " is null");
            }
            if (debug) debug("writing constant pool entry " + i);
            constantPool[i].write(out);
            if (constantPool[i] instanceof ConstantLargeNumeric) {
                // CONSTANT_Double_info and CONSTANT_Long_info take 2 constant
                // pool entries, the second entry is unusable (design mistake)
                i++;
            }
        }
    }

    private void readAccessFlags(DataInput in) throws IOException {

        accessFlags = in.readUnsignedShort();
        if (debug) debug("read access flags " + printAccessFlags(accessFlags));
    }

    private void writeAccessFlags(DataOutput out) throws IOException {

        out.writeShort(accessFlags);
        if (debug) debug("wrote access flags " + printAccessFlags(accessFlags));
    }

    private void readThisClass(DataInput in) throws IOException {

        thisClass = in.readUnsignedShort();
        if (debug) debug("read this_class index " + thisClass);
    }

    private void writeThisClass(DataOutput out) throws IOException {

        out.writeShort(thisClass);
        if (debug) debug("wrote this_class index " + thisClass);
    }

    private void readSuperClass(DataInput in) throws IOException {

        superClass = in.readUnsignedShort();
        if (debug) debug("read super_class index " + superClass);
    }

    private void writeSuperClass(DataOutput out) throws IOException {

        out.writeShort(superClass);
        if (debug) debug("wrote super_class index " + superClass);
    }

    private void readInterfaces(DataInput in) throws IOException {

        int interfacesCount = in.readUnsignedShort();
        if (debug) debug("read interfaces count " + interfacesCount);

        interfaces = new int[interfacesCount];

        for (int i = 0; i < interfacesCount; i++) {
            interfaces[i] = in.readUnsignedShort();
            if (debug) debug("read interface index " + interfaces[i]);
        }

    }

    private void writeInterfaces(DataOutput out) throws IOException {

        int interfacesCount = getLength(interfaces);

        out.writeShort(interfacesCount);
        if (debug) debug("wrote interfaces count " + interfacesCount);

        for (int i = 0; i < interfacesCount; i++) {
            out.writeShort(interfaces[i]);
            if (debug) debug("wrote interface index " + interfaces[i]);
        }

    }

    private void readFields(DataInput in)
            throws InvalidByteCodeException, IOException {

        int fieldsCount = in.readUnsignedShort();
        if (debug) debug("read fields count " + fieldsCount);

        fields = new FieldInfo[fieldsCount];

        for (int i = 0; i < fieldsCount; i++) {
            fields[i] = FieldInfo.create(in, this);
        }

    }

    private void writeFields(DataOutput out)
            throws InvalidByteCodeException, IOException {

        int fieldsCount = getLength(fields);

        out.writeShort(fieldsCount);
        if (debug) debug("wrote fields count " + fieldsCount);

        for (int i = 0; i < fieldsCount; i++) {
            if (fields[i] == null) {
                throw new InvalidByteCodeException("field " + i + " is null");
            }
            fields[i].write(out);
        }

    }

    private void readMethods(DataInput in)
            throws InvalidByteCodeException, IOException {

        int methodsCount = in.readUnsignedShort();
        if (debug) debug("read methods count " + methodsCount);

        methods = new MethodInfo[methodsCount];

        for (int i = 0; i < methodsCount; i++) {
            methods[i] = MethodInfo.create(in, this);
        }

    }

    private void writeMethods(DataOutput out)
            throws InvalidByteCodeException, IOException {

        int methodsCount = getLength(methods);

        out.writeShort(methodsCount);
        if (debug) debug("wrote methods count " + methodsCount);

        for (int i = 0; i < methodsCount; i++) {
            if (methods[i] == null) {
                throw new InvalidByteCodeException("method " + i + " is null");
            }
            methods[i].write(out);
        }

    }

    protected void readAttributes(DataInput in)
            throws InvalidByteCodeException, IOException {

        super.readAttributes(in);
        if (debug) debug("read " + getLength(attributes) + " attributes for the ClassFile structure");
    }

    protected void writeAttributes(DataOutput out)
            throws InvalidByteCodeException, IOException {

        super.writeAttributes(out);
        if (debug) debug("wrote " + getLength(attributes) + " attributes for the ClassFile structure");
    }

    private void checkMajorVersion(int majorVersion) {

        if (majorVersion < 45 || majorVersion > 52) {
            Log.warning("major version should be between 45 and 51 for JDK <= 1.8, was " + majorVersion);
        }

    }

    protected String printAccessFlagsVerbose(int accessFlags) {
        return printAccessFlagsVerbose(AccessFlag.CLASS_ACCESS_FLAGS, accessFlags);
    }
}
