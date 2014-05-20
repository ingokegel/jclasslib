/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures;

import org.gjt.jclasslib.structures.constants.*;

import java.io.DataInput;
import java.io.IOException;

/**
 * Base class for all constant pool entries in the <tt>constants</tt> package.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>, <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public abstract class CPInfo extends AbstractStructure {

    public static final byte CONSTANT_CLASS = 7;
    public static final byte CONSTANT_FIELDREF = 9;
    public static final byte CONSTANT_METHODREF = 10;
    public static final byte CONSTANT_INTERFACE_METHODREF = 11;
    public static final byte CONSTANT_STRING = 8;
    public static final byte CONSTANT_INTEGER = 3;
    public static final byte CONSTANT_FLOAT = 4;
    public static final byte CONSTANT_LONG = 5;
    public static final byte CONSTANT_DOUBLE = 6;
    public static final byte CONSTANT_NAME_AND_TYPE = 12;
    public static final byte CONSTANT_METHOD_HANDLE = 15;
    public static final byte CONSTANT_METHOD_TYPE = 16;
    public static final byte CONSTANT_INVOKE_DYNAMIC = 18;
    public static final byte CONSTANT_UTF8 = 1;

    public static final String CONSTANT_CLASS_VERBOSE = "CONSTANT_Class_info";
    public static final String CONSTANT_FIELDREF_VERBOSE = "CONSTANT_Fieldref_info";
    public static final String CONSTANT_METHODREF_VERBOSE = "CONSTANT_Methodref_info";
    public static final String CONSTANT_INTERFACE_METHODREF_VERBOSE = "CONSTANT_InterfaceMethodref_info";
    public static final String CONSTANT_STRING_VERBOSE = "CONSTANT_String_info";
    public static final String CONSTANT_INTEGER_VERBOSE = "CONSTANT_Integer_info";
    public static final String CONSTANT_FLOAT_VERBOSE = "CONSTANT_Float_info";
    public static final String CONSTANT_LONG_VERBOSE = "CONSTANT_Long_info";
    public static final String CONSTANT_DOUBLE_VERBOSE = "CONSTANT_Double_info";
    public static final String CONSTANT_NAME_AND_TYPE_VERBOSE = "CONSTANT_NameAndType_info";
    public static final String CONSTANT_METHOD_HANDLE_VERBOSE = "CONSTANT_MethodHandle_info";
    public static final String CONSTANT_METHOD_TYPE_VERBOSE = "CONSTANT_MethodType_info";
    public static final String CONSTANT_INVOKE_DYNAMIC_VERBOSE = "CONSTANT_InvokeDynamic_info";
    public static final String CONSTANT_UTF8_VERBOSE = "CONSTANT_Utf8_info";

    /**
     * Factory method for creating <tt>CPInfo</tt> structures. <p>
     * A <tt>CPInfo</tt> of the appropriate subtype from the <tt>constants</tt> package
     * is created. <p>
     *
     * @param in        the <tt>DataInput</tt> from which to read the <tt>CPInfo</tt> structure
     * @param classFile the parent class file of the structure to be created
     * @return the new <tt>CPInfo</tt> structure
     * @throws InvalidByteCodeException if the byte code is invalid
     * @throws IOException              if an exception occurs with the <tt>DataInput</tt>
     */
    public static CPInfo create(DataInput in, ClassFile classFile)
            throws InvalidByteCodeException, IOException {

        CPInfo cpInfo;

        byte tag = in.readByte();

        switch (tag) {
            case CONSTANT_CLASS:
                cpInfo = new ConstantClassInfo();
                break;
            case CONSTANT_FIELDREF:
                cpInfo = new ConstantFieldrefInfo();
                break;
            case CONSTANT_METHODREF:
                cpInfo = new ConstantMethodrefInfo();
                break;
            case CONSTANT_INTERFACE_METHODREF:
                cpInfo = new ConstantInterfaceMethodrefInfo();
                break;
            case CONSTANT_STRING:
                cpInfo = new ConstantStringInfo();
                break;
            case CONSTANT_INTEGER:
                cpInfo = new ConstantIntegerInfo();
                break;
            case CONSTANT_FLOAT:
                cpInfo = new ConstantFloatInfo();
                break;
            case CONSTANT_LONG:
                cpInfo = new ConstantLongInfo();
                break;
            case CONSTANT_DOUBLE:
                cpInfo = new ConstantDoubleInfo();
                break;
            case CONSTANT_NAME_AND_TYPE:
                cpInfo = new ConstantNameAndTypeInfo();
                break;
            case CONSTANT_METHOD_TYPE:
                cpInfo = new ConstantMethodTypeInfo();
                break;
            case CONSTANT_METHOD_HANDLE:
                cpInfo = new ConstantMethodHandleInfo();
                break;
            case CONSTANT_INVOKE_DYNAMIC:
                cpInfo = new ConstantInvokeDynamicInfo();
                break;
            case CONSTANT_UTF8:
                cpInfo = new ConstantUtf8Info();
                break;
            default:
                throw new InvalidByteCodeException("invalid constant pool entry with unknown tag " + tag);
        }
        cpInfo.setClassFile(classFile);
        cpInfo.read(in);

        return cpInfo;
    }


    /**
     * Get the value of the <tt>tag</tt> field of the <tt>cp_info</tt> structure.
     *
     * @return the tag
     */
    public abstract byte getTag();

    /**
     * Get the verbose description of the <tt>tag</tt> field of the
     * <tt>cp_info</tt> structure.
     *
     * @return the verbose description
     */
    public abstract String getTagVerbose();

    /**
     * Get the verbose description of the content of the constant pool entry.
     *
     * @return the verbose description
     * @throws InvalidByteCodeException if the byte code is invalid
     */
    public String getVerbose() throws InvalidByteCodeException {
        return "";
    }

    /**
     * Skip a <tt>CPInfo</tt> structure in a <tt>DataInput</tt>. <p>
     *
     * @param in the <tt>DataInput</tt> from which to read the <tt>CPInfo</tt> structure
     * @return the number of bytes skipped
     * @throws InvalidByteCodeException if the byte code is invalid
     * @throws IOException              if an exception occurs with the <tt>DataInput</tt>
     */
    public static int skip(DataInput in)
            throws InvalidByteCodeException, IOException {

        int offset = 0;

        byte tag = in.readByte();

        switch (tag) {
            case CONSTANT_CLASS:
                in.skipBytes(ConstantClassInfo.SIZE);
                break;
            case CONSTANT_FIELDREF:
            case CONSTANT_METHODREF:
            case CONSTANT_INTERFACE_METHODREF:
                in.skipBytes(ConstantReference.SIZE);
                break;
            case CONSTANT_STRING:
                in.skipBytes(ConstantStringInfo.SIZE);
                break;
            case CONSTANT_INTEGER:
            case CONSTANT_FLOAT:
                in.skipBytes(ConstantNumeric.SIZE);
                break;
            case CONSTANT_LONG:
            case CONSTANT_DOUBLE:
                in.skipBytes(ConstantLargeNumeric.SIZE);
                offset = 1;
                break;
            case CONSTANT_NAME_AND_TYPE:
                in.skipBytes(ConstantNameAndTypeInfo.SIZE);
                break;
            case CONSTANT_METHOD_TYPE:
                in.skipBytes(ConstantMethodTypeInfo.SIZE);
                break;
            case CONSTANT_METHOD_HANDLE:
                in.skipBytes(ConstantMethodHandleInfo.SIZE);
                break;
            case CONSTANT_INVOKE_DYNAMIC:
                in.skipBytes(ConstantInvokeDynamicInfo.SIZE);
                break;
            case CONSTANT_UTF8:
                // Length of the constant is determined by the length of the byte array
                in.skipBytes(in.readUnsignedShort());
                break;
            default:
                throw new InvalidByteCodeException("invalid constant pool entry with unknown tag " + tag);
        }

        return offset;
    }

    public boolean equals(Object object) {
        return object instanceof CPInfo;
    }

    public int hashCode() {
        return 0;
    }

    protected String printAccessFlagsVerbose(int accessFlags) {
        if (accessFlags != 0)
            throw new RuntimeException("Access flags should be zero: " + Integer.toHexString(accessFlags));
        return "";
    }
}
