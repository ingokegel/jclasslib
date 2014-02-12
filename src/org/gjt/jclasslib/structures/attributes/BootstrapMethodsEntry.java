/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.*;

import java.io.*;

/**
 * Describes an entry in a <tt>BootstrapMethods</tt> attribute structure.
 *
 */
public class BootstrapMethodsEntry extends AbstractStructure {



    private int methodRefIndex;
    private int argumentNum;
    private int argumentRefs[];

    /**
     * Factory method for creating <tt>InnerClassesEntry</tt> structures.
     *
     * @param in        the <tt>DataInput</tt> from which to read the
     *                  <tt>BootstrapMethodsEntry</tt> structure
     * @param classFile the parent class file of the structure to be created
     * @return the new <tt>BootstrapMethos</tt> structure
     * @throws InvalidByteCodeException if the byte code is invalid
     * @throws IOException              if an exception occurs with the <tt>DataInput</tt>
     */
    public static BootstrapMethodsEntry create(DataInput in, ClassFile classFile)
            throws InvalidByteCodeException, IOException {

        BootstrapMethodsEntry bootStrapMethodsEntry = new BootstrapMethodsEntry();
        bootStrapMethodsEntry.setClassFile(classFile);
        bootStrapMethodsEntry.read(in);

        return bootStrapMethodsEntry;
    }

    /**
     * Get the constant pool index of the <tt>CONSTANT_MethodRef_info</tt> structure
     * describing the bootstrap method of this <tt>BootstrapMethodsEntry</tt>.
     *
     * @return the index
     */
    public int getMethodRefIndexIndex() {
        return methodRefIndex;
    }

    /**
     * Set the constant pool index of the <tt>CONSTANT_MethodRef_info</tt> structure
     * describing the bootstrap method of this <tt>BootstrapMethodsEntry</tt>.
     *
     * @param methodRefIndex the index
     */
    public void setMethodRefIndexIndex(int methodRefIndex) {
        this.methodRefIndex = methodRefIndex;
    }


    /**
     * Get the number of arguments for the bootstrap method
     * inner class of this <tt>BootstrapMethodsEntry</tt>.
     *
     * @return the number of arguments
     */
    public int getArgumentNumber() {
        return argumentNum;
    }

    /**
     * set the number of arguments for the bootstrap method
     * inner class of this <tt>BootstrapMethodsEntry</tt>.
     *
     * @param argumentNum the number of arguments
     */
    public void setArgumentNumber(int argumentNum) {
        this.argumentNum = argumentNum;
    }

    /**
     * Get the array of argument references of this  <tt>BootstrapMethodsEntry</tt>.
     *
     * @return the argument references
     */
    public int[] getArgumentRefs() {
        return argumentRefs;
    }

    /**
     * Set the array of argument references of this  <tt>BootstrapMethodsEntry</tt>.
     *
     * @param argumentRefs the argument references
     */
    public void setArgumentRefs(int argumentRefs[]) {
        this.argumentRefs = argumentRefs;
    }


    public void read(DataInput in)
            throws InvalidByteCodeException, IOException {

    	methodRefIndex = in.readUnsignedShort();
    	argumentNum = in.readUnsignedShort();
    	argumentRefs = new int[argumentNum];
    	for (int i=0; i < argumentNum; i++) {
    		argumentRefs[i] = in.readUnsignedShort();
    	}
        if (debug) debug("read ");
    }

    public void write(DataOutput out)
            throws InvalidByteCodeException, IOException {

        super.write(out);
        out.writeShort(methodRefIndex);
        out.writeShort(argumentNum);
        for (int i=0; i < argumentNum; i++) {
        	out.writeShort(argumentRefs[i]);
    	}
        if (debug) debug("wrote ");
    }

    protected void debug(String message) {
        super.debug(message + "BootstrapMethods entry with bootstrap_method_index " + methodRefIndex +
                ", arguments (" + printArguments()+")");
    }

    public String printArguments() {
    	StringBuffer sb = new StringBuffer();
    	for (int i=0; i < argumentNum; i++) {
    		sb.append(argumentRefs[i]);
    		if (i+1 < argumentNum) {
    			sb.append(", ");
    		}
    	}
    	return sb.toString();
    }
    
    public int getLength() {
    	return 4+argumentNum*2;
    }

	@Override
	protected String printAccessFlagsVerbose(int accessFlags) {
		return null;
	}

    
}
