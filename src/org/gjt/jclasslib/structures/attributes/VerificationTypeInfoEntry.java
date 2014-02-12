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
public class VerificationTypeInfoEntry extends AbstractStructure {

	public static final int ITEM_Top=0;
	public static final int ITEM_Integer=1;
	public static final int ITEM_Float=2;
	public static final int ITEM_Double=3;
	public static final int ITEM_Long=4;
	public static final int ITEM_Null=5;
	public static final int ITEM_UninitializedThis=6;
	public static final int ITEM_Object=7;
	public static final int ITEM_Uninitialized=8;
	

    private int type;
    private int extra;//cpool_idx or offset;

    /**
     * Factory method for creating <tt>VerificationTypeInfoEntry</tt> structures.
     *
     * @param in        the <tt>DataInput</tt> from which to read the
     *                  <tt>VerificationTypeInfoEntry</tt> structure
     * @param classFile the parent class file of the structure to be created
     * @return the new <tt>VerificationTypeInfoEntry</tt> structure
     * @throws InvalidByteCodeException if the byte code is invalid
     * @throws IOException              if an exception occurs with the <tt>DataInput</tt>
     */
    public static VerificationTypeInfoEntry create(DataInput in, ClassFile classFile)
            throws InvalidByteCodeException, IOException {

        VerificationTypeInfoEntry bootStrapMethodsEntry = new VerificationTypeInfoEntry();
        bootStrapMethodsEntry.setClassFile(classFile);
        bootStrapMethodsEntry.read(in);

        return bootStrapMethodsEntry;
    }


    public void read(DataInput in)
            throws InvalidByteCodeException, IOException {

    	type = in.readUnsignedByte();
    	if (type == ITEM_Object || type == ITEM_Uninitialized) {
    		extra = in.readUnsignedShort();
    	}

        if (debug) debug("read ");
    }

    public void write(DataOutput out)
            throws InvalidByteCodeException, IOException {

        super.write(out);
//        out.writeShort(methodRefIndex);
//        out.writeShort(argumentNum);
//        for (int i=0; i < argumentNum; i++) {
//        	out.writeShort(argumentRefs[i]);
//    	}
//        if (debug) debug("wrote ");
    }

    protected void debug(String message) {
        super.debug(message + "VerificationTypeInfo entry of type " + type);
    }

    
    public int getLength() {
    	if (type == ITEM_Object || type == ITEM_Uninitialized) {
    		return 3;
    	}
    	return 1;
    }
    
    public String printEntry() {
    	StringBuffer sb = new StringBuffer();
    	switch(type) {
    	case ITEM_Top:
    		sb.append("ITEM_Top");
    		break;
    	case ITEM_Integer:
    		sb.append("ITEM_Integer");
    		break;
    	case ITEM_Float:
    		sb.append("ITEM_Float");
    		break;
    	case ITEM_Double:
    		sb.append("ITEM_Double");
    		break;
    	case ITEM_Long:
    		sb.append("ITEM_Long");
    		break;
    	case ITEM_Null:
    		sb.append("ITEM_Null");
    		break;
    	case ITEM_UninitializedThis:
    		sb.append("ITEM_UninitializedThis");
    		break;
    	case ITEM_Object:
    		sb.append("ITEM_Object (CP_IDX: "+extra+")");
    		break;
    	case ITEM_Uninitialized:
    		sb.append("ITEM_Uninitialized (Offset:"+extra+")");
    		break;
    	}
    	return sb.toString();
    }
    
	@Override
	protected String printAccessFlagsVerbose(int accessFlags) {
		return null;
	}

    
}
