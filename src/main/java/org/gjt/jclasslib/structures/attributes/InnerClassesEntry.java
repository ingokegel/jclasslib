/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.AbstractStructure;
import org.gjt.jclasslib.structures.AccessFlag;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Describes an entry in a <tt>InnerClasses</tt> attribute structure.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>, <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class InnerClassesEntry extends AbstractStructure {

    /**
     * Length in bytes of an inner class entry.
     */
    public static final int LENGTH = 8;

    private int innerClassInfoIndex;
    private int outerClassInfoIndex;
    private int innerNameIndex;
    private int innerClassAccessFlags;

    /**
     * Factory method for creating <tt>InnerClassesEntry</tt> structures.
     *
     * @param in        the <tt>DataInput</tt> from which to read the
     *                  <tt>InnerClassesEntry</tt> structure
     * @param classFile the parent class file of the structure to be created
     * @return the new <tt>InnerClassesEntry</tt> structure
     * @throws InvalidByteCodeException if the byte code is invalid
     * @throws IOException              if an exception occurs with the <tt>DataInput</tt>
     */
    public static InnerClassesEntry create(DataInput in, ClassFile classFile)
            throws InvalidByteCodeException, IOException {

        InnerClassesEntry innerClassesEntry = new InnerClassesEntry();
        innerClassesEntry.setClassFile(classFile);
        innerClassesEntry.read(in);

        return innerClassesEntry;
    }

    /**
     * Get the constant pool index of the <tt>CONSTANT_Class_info</tt> structure
     * describing the inner class of this <tt>InnerClassEntry</tt>.
     *
     * @return the index
     */
    public int getInnerClassInfoIndex() {
        return innerClassInfoIndex;
    }

    /**
     * Set the constant pool index of the <tt>CONSTANT_Class_info</tt> structure
     * describing the inner class of this <tt>InnerClassEntry</tt>.
     *
     * @param innerClassInfoIndex the index
     */
    public void setInnerClassInfoIndex(int innerClassInfoIndex) {
        this.innerClassInfoIndex = innerClassInfoIndex;
    }

    /**
     * Get the constant pool index of the <tt>CONSTANT_Class_info</tt> structure
     * describing the outer class of this <tt>InnerClassEntry</tt>.
     *
     * @return the index
     */
    public int getOuterClassInfoIndex() {
        return outerClassInfoIndex;
    }

    /**
     * Set the constant pool index of the <tt>CONSTANT_Class_info</tt> structure
     * describing the outer class of this <tt>InnerClassEntry</tt>.
     *
     * @param outerClassInfoIndex the index
     */
    public void setOuterClassInfoIndex(int outerClassInfoIndex) {
        this.outerClassInfoIndex = outerClassInfoIndex;
    }

    /**
     * Get the constant pool index containing the simple name of the
     * inner class of this <tt>InnerClassEntry</tt>.
     *
     * @return the index
     */
    public int getInnerNameIndex() {
        return innerNameIndex;
    }

    /**
     * Set the constant pool index containing the simple name of the
     * inner class of this <tt>InnerClassEntry</tt>.
     *
     * @param innerNameIndex the index
     */
    public void setInnerNameIndex(int innerNameIndex) {
        this.innerNameIndex = innerNameIndex;
    }

    /**
     * Get the access flags of the inner class.
     *
     * @return the access flags
     */
    public int getInnerClassAccessFlags() {
        return innerClassAccessFlags;
    }

    /**
     * Set the access flags of the inner class.
     *
     * @param innerClassAccessFlags the access flags
     */
    public void setInnerClassAccessFlags(int innerClassAccessFlags) {
        this.innerClassAccessFlags = innerClassAccessFlags;
    }

    /**
     * Get the the access flags of the inner class as a hex string.
     *
     * @return the hex string
     */
    public String getInnerClassFormattedAccessFlags() {
        return printAccessFlags(innerClassAccessFlags);
    }

    /**
     * Get the verbose description of the access flags of the inner class.
     *
     * @return the description
     */
    public String getInnerClassAccessFlagsVerbose() {
        return printAccessFlagsVerbose(innerClassAccessFlags);
    }

    public void read(DataInput in)
            throws InvalidByteCodeException, IOException {

        innerClassInfoIndex = in.readUnsignedShort();
        outerClassInfoIndex = in.readUnsignedShort();
        innerNameIndex = in.readUnsignedShort();
        innerClassAccessFlags = in.readUnsignedShort();

        if (debug) debug("read ");
    }

    public void write(DataOutput out)
            throws InvalidByteCodeException, IOException {

        super.write(out);
        out.writeShort(innerClassInfoIndex);
        out.writeShort(outerClassInfoIndex);
        out.writeShort(innerNameIndex);
        out.writeShort(innerClassAccessFlags);
        if (debug) debug("wrote ");
    }

    protected void debug(String message) {
        super.debug(message + "InnerClasses entry with inner_class_info_index " + innerClassInfoIndex +
                ", outer_class_info_index " + outerClassInfoIndex + ", inner_name_index " + innerNameIndex +
                ", access flags " + printAccessFlags(innerClassAccessFlags));
    }

    protected String printAccessFlagsVerbose(int accessFlags) {
        return printAccessFlagsVerbose(AccessFlag.INNER_CLASS_ACCESS_FLAGS,
            accessFlags);
    }

}
