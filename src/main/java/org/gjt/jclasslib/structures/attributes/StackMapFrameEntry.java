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
public class StackMapFrameEntry extends AbstractStructure {

	public static final int SAME_FRAME=63; //0-63
	public static final int SAME_LOCALS_1_STACK_ITEM_FRAME=127;//64-127
	public static final int SAME_LOCALS_1_STACK_ITEM_FRAME_EXT=247;//247
	public static final int CHOP_FRAME=250;//248-250
	public static final int SAME_FRAME_EXT=251;//251
	public static final int APPEND_FRAME=254;//252-254
	public static final int FULL_FRAME=255;//255

    private int type, frameType;
    private int delta, numLocals, numStacks;
    private VerificationTypeInfoEntry local_vti[], stack_vti[];

    /**
     * Factory method for creating <tt>StackMapFrameEntry</tt> structures.
     *
     * @param in        the <tt>DataInput</tt> from which to read the
     *                  <tt>StackMapFrameEntry</tt> structure
     * @param classFile the parent class file of the structure to be created
     * @return the new <tt>StackMapFrameEntry</tt> structure
     * @throws InvalidByteCodeException if the byte code is invalid
     * @throws IOException              if an exception occurs with the <tt>DataInput</tt>
     */
    public static StackMapFrameEntry create(DataInput in, ClassFile classFile)
            throws InvalidByteCodeException, IOException {

        StackMapFrameEntry bootStrapMethodsEntry = new StackMapFrameEntry();
        bootStrapMethodsEntry.setClassFile(classFile);
        bootStrapMethodsEntry.read(in);

        return bootStrapMethodsEntry;
    }

    public int getDelta() {
    	return delta;
    }

    public void read(DataInput in)
            throws InvalidByteCodeException, IOException {

    	type = in.readUnsignedByte();
    	if (type <= SAME_FRAME) {
    		readSame(in);
    	} else if (type <= SAME_LOCALS_1_STACK_ITEM_FRAME) {
    		readSameLocals(in);
    	} else if (type == SAME_LOCALS_1_STACK_ITEM_FRAME_EXT) {
    		readSameLocalsExt(in);
    	} else if (type <= CHOP_FRAME) {
    		readChop(in);
    	} else if (type == SAME_FRAME_EXT) {
    		readSameExt(in);
    	} else if (type <= APPEND_FRAME) {
    		readAppend(in);
    	} else if (type == FULL_FRAME) {
    		readFull(in);
    	} else {
    		throw new InvalidByteCodeException("Unsupported StackMapFrame type: "+type);
    	}
        if (debug) debug("read ");
    }
    
    private void readSame(DataInput in)
            throws InvalidByteCodeException, IOException {
    	frameType=SAME_FRAME;
    	delta=0;
    	numLocals=0;
    	local_vti = new VerificationTypeInfoEntry[numLocals];
    	numStacks = 0;
    	stack_vti = new VerificationTypeInfoEntry[numStacks];
    }
    private void readSameLocals(DataInput in)
            throws InvalidByteCodeException, IOException {
    	frameType = SAME_LOCALS_1_STACK_ITEM_FRAME;
    	delta=0;
    	numLocals=0;
    	local_vti = new VerificationTypeInfoEntry[numLocals];
    	numStacks = 1;
    	stack_vti = new VerificationTypeInfoEntry[numStacks];
    	stack_vti[0] = VerificationTypeInfoEntry.create(in, classFile);
       }
    private void readSameLocalsExt(DataInput in)
            throws InvalidByteCodeException, IOException {
    	frameType = SAME_LOCALS_1_STACK_ITEM_FRAME_EXT;
    	delta=in.readUnsignedShort();
    	numLocals=0;
    	local_vti = new VerificationTypeInfoEntry[numLocals];
    	numStacks = 1;
    	stack_vti = new VerificationTypeInfoEntry[numStacks];
    	stack_vti[0] = VerificationTypeInfoEntry.create(in, classFile);
       }
    private void readChop(DataInput in)
            throws InvalidByteCodeException, IOException {
    	frameType = CHOP_FRAME;
    	delta=in.readUnsignedShort();
    	numLocals=0;
    	local_vti = new VerificationTypeInfoEntry[numLocals];
    	numStacks = 0;
    	stack_vti = new VerificationTypeInfoEntry[numStacks];
    }
    private void readSameExt(DataInput in)
            throws InvalidByteCodeException, IOException {
    	frameType = SAME_FRAME_EXT;
    	delta=in.readUnsignedShort();
    	numLocals=0;
    	local_vti = new VerificationTypeInfoEntry[numLocals];
    	numStacks = 0;
    	stack_vti = new VerificationTypeInfoEntry[numStacks];;
    }
    private void readAppend(DataInput in)
            throws InvalidByteCodeException, IOException {
    	frameType = APPEND_FRAME;
    	delta=in.readUnsignedShort();
    	numLocals=type-251;
    	local_vti = new VerificationTypeInfoEntry[numLocals];
    	for (int i=0; i < numLocals; i++) {
    		local_vti[i]= VerificationTypeInfoEntry.create(in, classFile);
    	}
    	numStacks = 0;
    	stack_vti = new VerificationTypeInfoEntry[numStacks];
    }
    private void readFull(DataInput in)
            throws InvalidByteCodeException, IOException {
    	frameType = FULL_FRAME;
    	delta=in.readUnsignedShort();
    	numLocals=in.readUnsignedShort();
    	local_vti = new VerificationTypeInfoEntry[numLocals];
    	for (int i=0; i < numLocals; i++) {
    		local_vti[i]= VerificationTypeInfoEntry.create(in, classFile);
    	}
    	numStacks = in.readShort();
       	stack_vti = new VerificationTypeInfoEntry[numStacks];
           	for (int i=0; i < numStacks; i++) {
           		stack_vti[i]= VerificationTypeInfoEntry.create(in, classFile);
    	}
    }

	public void write(DataOutput out) throws InvalidByteCodeException,
			IOException {

		super.write(out);
		out.writeByte(type);
		switch (frameType) {
		case SAME_FRAME:
			writeSame(out);
			break;
		case SAME_LOCALS_1_STACK_ITEM_FRAME:
			writeSameLocals(out);
			break;
		case SAME_LOCALS_1_STACK_ITEM_FRAME_EXT:
			writeSameLocalsExt(out);
			break;
		case CHOP_FRAME:
			writeChop(out);
			break;
		case SAME_FRAME_EXT:
			writeSameExt(out);
			break;
		case APPEND_FRAME:
			writeAppend(out);
			break;
		case FULL_FRAME:
			writeFull(out);
			break;
		default:
			throw new InvalidByteCodeException(
					"Unsupported StackMapFrame type: " + type);

		}
        if (debug) debug("wrote ");
    }

    private void writeSame(DataOutput in)
            throws InvalidByteCodeException, IOException {
    	//nothing to write
    }
    private void writeSameLocals(DataOutput in)
            throws InvalidByteCodeException, IOException {
    }
    private void writeSameLocalsExt(DataOutput in)
            throws InvalidByteCodeException, IOException {
    }
    private void writeChop(DataOutput in)
            throws InvalidByteCodeException, IOException {
    }
    private void writeSameExt(DataOutput in)
            throws InvalidByteCodeException, IOException {
    }
    private void writeAppend(DataOutput in)
            throws InvalidByteCodeException, IOException {
    }
    private void writeFull(DataOutput in)
            throws InvalidByteCodeException, IOException {
    }
    protected void debug(String message) {
        super.debug(message + "StackMapFrame entry of type " + type);
    }
    
    public String printEntry() {
    	StringBuffer sb = new StringBuffer();
    	switch (frameType) {
		case SAME_FRAME:
			sb.append("Type ").append(type).append("(SAME): ");
			break;
		case SAME_LOCALS_1_STACK_ITEM_FRAME:
			sb.append("TYPE ").append(type).append("(SAME_LOCAL_1_STACK): ");
			appendStackVerificationTypeInfos(sb);
			break;
		case SAME_LOCALS_1_STACK_ITEM_FRAME_EXT:
			sb.append("TYPE ").append(type).append("(SAME_LOCALS_1_STACK_EXT): ");
			sb.append("(offset: "+delta+")\n");
			appendStackVerificationTypeInfos(sb);
			break;
		case CHOP_FRAME:
			sb.append("TYPE ").append(type).append("(CHOP): ");
			sb.append("(offset: "+delta+")\n");
			break;
		case SAME_FRAME_EXT:
			sb.append("TYPE ").append(type).append("(SAME_EXT): ");
			sb.append("(offset: "+delta+")\n");
			break;
		case APPEND_FRAME:
			sb.append("TYPE ").append(type).append("(APPEND): ");
			sb.append("(offset: "+delta+")\n");
			appendLocalsVerificationTypeInfos(sb);
			break;
		case FULL_FRAME:
			sb.append("TYPE ").append(type).append("(FULL): ");
			sb.append("(offset: "+delta+")\n");
			appendLocalsVerificationTypeInfos(sb);
			appendStackVerificationTypeInfos(sb);
			break;
    	}
    	
    	return sb.toString();
    }

    private void appendStackVerificationTypeInfos(StringBuffer sb) {
    	sb.append(" Stack Verification: { \n");
    	for (int i=0; i < numStacks; i++) {
    		sb.append(stack_vti[i].printEntry());
    		if (i + 1 < numStacks) {
    			sb.append(", ");
    		}
    	}
    	sb.append("}");
    }
    
    private void appendLocalsVerificationTypeInfos(StringBuffer sb) {
    	sb.append(" Locals Verification: {\n");
    	for (int i=0; i < numLocals; i++) {
    		sb.append(local_vti[i].printEntry());
    		if (i + 1 < numLocals) {
    			sb.append(", ");
    		}
    	}
    	sb.append("}");
    }

    public int getLength() {
		switch (frameType) {
		case SAME_FRAME:
			return 1;
		case SAME_LOCALS_1_STACK_ITEM_FRAME:
			return 1 + getVerificationInfoLength(stack_vti);
		case SAME_LOCALS_1_STACK_ITEM_FRAME_EXT:
			return 3 + getVerificationInfoLength(stack_vti);
		case CHOP_FRAME:
		case SAME_FRAME_EXT:
			return 3;
		case APPEND_FRAME:
			return 3 + getVerificationInfoLength(local_vti);
		case FULL_FRAME:
			return 7 + getVerificationInfoLength(local_vti)
					+ getVerificationInfoLength(stack_vti);

		}
		return 0;
    }
    
    private int getVerificationInfoLength(VerificationTypeInfoEntry vti[]) {
    	int size = 0;
    	int length = vti.length;
    	for (int i =0; i < length; i++) {
    		size += vti[i].getLength();
    	}
    	return size;
    }

	@Override
	protected String printAccessFlagsVerbose(int accessFlags) {
		return null;
	}

    
}
