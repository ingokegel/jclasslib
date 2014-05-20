/*
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public
License as published by the Free Software Foundation; either
version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures;

import org.gjt.jclasslib.structures.attributes.*;
import org.gjt.jclasslib.structures.constants.ConstantUtf8Info;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Base class for all attribute structures in the <tt>attribute</tt> package.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>, <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class AttributeInfo extends AbstractStructureWithAttributes {

    /**
     * Set this JVM System property to true to skip reading of all attributes.
     * Some class file operations may fail in this case.
     */
    public static final String SYSTEM_PROPERTY_SKIP_ATTRIBUTES = "jclasslib.io.skipAttributes";

    private int attributeNameIndex;
    private int attributeLength;
    private byte[] info;

    /**
     * Factory method for creating <tt>AttributeInfo</tt> structures. <p>
     * An <tt>AttributeInfo</tt> of the appropriate subtype from the <tt>attributes</tt> package
     * is created unless the type of the attribute is unknown in which case an instance of
     * <tt>AttributeInfo</tt> is returned. <p>
     * <p/>
     * Attributes are skipped if the environment variable <tt>SYSTEM_PROPERTY_SKIP_ATTRIBUTES</tt>
     * is set to true.
     *
     * @param in        the <tt>DataInput</tt> from which to read the <tt>AttributeInfo</tt> structure
     * @param classFile the parent class file of the structure to be created
     * @return the new <tt>AttributeInfo</tt> structure
     * @throws InvalidByteCodeException if the byte code is invalid
     * @throws IOException              if an exception occurs with the <tt>DataInput</tt>
     */
    public static AttributeInfo createOrSkip(DataInput in, ClassFile classFile)
            throws InvalidByteCodeException, IOException {

        AttributeInfo attributeInfo = null;

        if (Boolean.getBoolean(SYSTEM_PROPERTY_SKIP_ATTRIBUTES)) {
            in.skipBytes(2);
            in.skipBytes(in.readInt());
        } else {
            int attributeNameIndex = in.readUnsignedShort();
            int attributeLength = in.readInt();

            ConstantUtf8Info cpInfoName = classFile.getConstantPoolUtf8Entry(attributeNameIndex);
            String attributeName;

            if (cpInfoName == null) {
                return null;
            }

            attributeName = cpInfoName.getString();

            if (ConstantValueAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new ConstantValueAttribute();
            } else if (CodeAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new CodeAttribute();
            } else if (ExceptionsAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new ExceptionsAttribute();
            } else if (InnerClassesAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new InnerClassesAttribute();
            } else if (SyntheticAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new SyntheticAttribute();
            } else if (SourceFileAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new SourceFileAttribute();
            } else if (LineNumberTableAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new LineNumberTableAttribute();
            } else if (LocalVariableTableAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new LocalVariableTableAttribute();
            } else if (DeprecatedAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new DeprecatedAttribute();
            } else if (EnclosingMethodAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new EnclosingMethodAttribute();
            } else if (SignatureAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new SignatureAttribute();
            } else if (LocalVariableTypeTableAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new LocalVariableTypeTableAttribute();
            } else if (RuntimeVisibleAnnotationsAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new RuntimeVisibleAnnotationsAttribute();
            } else if (RuntimeInvisibleAnnotationsAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new RuntimeInvisibleAnnotationsAttribute();
            } else if (RuntimeVisibleParameterAnnotationsAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new RuntimeVisibleParameterAnnotationsAttribute();
            } else if (RuntimeInvisibleParameterAnnotationsAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new RuntimeInvisibleParameterAnnotationsAttribute();
            } else if (RuntimeVisibleTypeAnnotationsAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new RuntimeVisibleTypeAnnotationsAttribute();
            } else if (RuntimeInvisibleTypeAnnotationsAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new RuntimeInvisibleTypeAnnotationsAttribute();
            } else if (AnnotationDefaultAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
                attributeInfo = new AnnotationDefaultAttribute();
            } else if (BootstrapMethodsAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
            	attributeInfo = new BootstrapMethodsAttribute();
            } else if (StackMapTableAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
            	attributeInfo = new StackMapTableAttribute();
            } else if (MethodParametersAttribute.ATTRIBUTE_NAME.equals(attributeName)) {
            	attributeInfo = new MethodParametersAttribute();
            } else {
                attributeInfo = new AttributeInfo(attributeLength);
            }
            attributeInfo.setAttributeNameIndex(attributeNameIndex);
            attributeInfo.setClassFile(classFile);
            attributeInfo.read(in);
        }

        return attributeInfo;
    }

    /**
     * Constructor.
     */
    protected AttributeInfo() {
    }

    private AttributeInfo(int attributeLength) {
        this.attributeLength = attributeLength;
    }

    /**
     * Get the constant pool index for the name of the attribute.
     *
     * @return the index
     */
    public int getAttributeNameIndex() {
        return attributeNameIndex;
    }

    /**
     * Set the constant pool index for the name of the attribute.
     *
     * @param attributeNameIndex the new index
     */
    public void setAttributeNameIndex(int attributeNameIndex) {
        this.attributeNameIndex = attributeNameIndex;
    }

    /**
     * Get the raw bytes of the attribute. <p>
     * <p/>
     * Is non-null only if attribute is of unknown type.
     *
     * @return the byte array
     */
    public byte[] getInfo() {
        return info;
    }

    /**
     * Set the raw bytes of the attribute. <p>
     * <p/>
     * Works only if attribute is an instance of <tt>AttributeInfo</tt>.
     *
     * @param info the new byte array
     */
    public void setInfo(byte[] info) {
        this.info = info;
    }

    /**
     * Get the name of the attribute.
     *
     * @return the name
     * @throws InvalidByteCodeException if the byte code is invalid
     */
    public String getName() throws InvalidByteCodeException {
        return classFile.getConstantPoolUtf8Entry(attributeNameIndex).getString();
    }

    public void read(DataInput in) throws InvalidByteCodeException, IOException {

        info = new byte[attributeLength];
        in.readFully(info);

        if (debug) debug("read " + getDebugMessage());
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {

        out.writeShort(attributeNameIndex);
        out.writeInt(getAttributeLength());
        if (getClass().equals(AttributeInfo.class)) {
            out.write(info);
            if (debug) debug("wrote " + getDebugMessage());
        }
    }

    /**
     * Get the length of this attribute as a number of bytes.
     *
     * @return the length
     */
    public int getAttributeLength() {
        return getLength(info);
    }

    // cannot override debug because subclasses will call super.debug
    // and expect to call the implementation in AbstractStructure
    private String getDebugMessage() {
        String type;
        try {
            type = classFile.getConstantPoolUtf8Entry(attributeNameIndex).getString();
        } catch (InvalidByteCodeException ex) {
            type = "(unknown)";
        }

        return "uninterpreted attribute of reported type " + type;
    }

    protected String printAccessFlagsVerbose(int accessFlags) {
        if (accessFlags != 0)
            throw new RuntimeException("Access flags should be zero: " + Integer.toHexString(accessFlags));
        return "";
    }

}
