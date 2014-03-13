/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.elementvalues;

import org.gjt.jclasslib.structures.*;

import java.io.*;

/**
 * Describes an entry in a <tt>BootstrapMethods</tt> attribute structure.
 *
 */
public class TypeAnnotationElementValue extends AbstractStructure {

	
	//Target Types
	public static final int TYPE_PARAMETER_TARGET__CLASS_INTERFACE = 0x00;
	public static final int TYPE_PARAMETER_TARGET__METHOD = 0x01;
	public static final int SUPERTYPE_TARGET = 0x10;
	public static final int TYPE_PARAMETER_BOUND_TARGET__CLASS_INTERFACE = 0x11;
	public static final int TYPE_PARAMETER_BOUND_TARGET__METHOD = 0x12;
	public static final int EMPTY_TARGET__FIELD = 0x13;
	public static final int EMPTY_TARGET__METHODRETURN_OBJECT = 0x14;
	public static final int EMPTY_TARGET__METHOD_CONSTRUCTOR = 0x15;
	public static final int METHOD_FORMAL_PARAMETER_TARGET = 0x16;
	public static final int THROWS_TARGET = 0x17;
	public static final int LOCALVAR_TARGET__LOCAL = 0x40;
	public static final int LOCALVAR_TARGET__RESOURCE = 0x41;
	public static final int CATCH_TARGET = 0x42;
	public static final int OFFSET_TARGET__INSTANCE = 0x43;
	public static final int OFFSET_TARGET__NEW = 0x44;
	public static final int OFFSET_TARGET__METHODREF_NEW = 0x45;
	public static final int OFFSET_TARGET__METHODREF_IDENTIFIER = 0x46;
	public static final int TYPE_ARGUMENT_TARGET__CAST = 0x47;
	public static final int TYPE_ARGUMENT_TARGET__CONSTRUCTOR = 0x48;
	public static final int TYPE_ARGUMENT_TARGET__GENERIC = 0x49;
	public static final int TYPE_ARGUMENT_TARGET__METHODREF_NEW = 0x4A;
	public static final int TYPE_ARGUMENT_TARGET__METHODREF_IDENTIFIER = 0x4B;
	
	

    private int targetType;
    private int targetInfo_val1, targetInfo_val2;
    private LocalVarTarget localVarEntries[];
    private int typePathLength;
    private TypePathEntry typePathEntries[];
    AnnotationElementValue annotation;
    
    private static int INITIAL_LENGTH=1;


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
    public static TypeAnnotationElementValue create(DataInput in, ClassFile classFile)
            throws InvalidByteCodeException, IOException {

        TypeAnnotationElementValue bootStrapMethodsEntry = new TypeAnnotationElementValue();
        bootStrapMethodsEntry.setClassFile(classFile);
        bootStrapMethodsEntry.read(in);

        return bootStrapMethodsEntry;
    }


    public void read(DataInput in)
            throws InvalidByteCodeException, IOException {

    	targetType = in.readUnsignedByte();
    	switch (targetType) {
    	case TYPE_PARAMETER_TARGET__CLASS_INTERFACE:
    	case TYPE_PARAMETER_TARGET__METHOD:
    		readTypeParameterTarget(in);
    		break;
    	case SUPERTYPE_TARGET:
    		readSupertypeTarget(in);
    		break;
    	case TYPE_PARAMETER_BOUND_TARGET__CLASS_INTERFACE:
    	case TYPE_PARAMETER_BOUND_TARGET__METHOD:
    		readTypeParameterBoundTarget(in);
    		break;
    	case EMPTY_TARGET__FIELD:
    	case EMPTY_TARGET__METHODRETURN_OBJECT:
    	case EMPTY_TARGET__METHOD_CONSTRUCTOR:
    		readEmptyTarget(in);
    		break;
    	case METHOD_FORMAL_PARAMETER_TARGET:
    		readMethodFormalParameterTarget(in);
    		break;
    	case THROWS_TARGET:
    		readThrowsTarget(in);
    		break;
    	case LOCALVAR_TARGET__LOCAL:
    	case LOCALVAR_TARGET__RESOURCE:
    		readLocalVarTarget(in);
    		break;
    	case CATCH_TARGET:
    		readCatchTarget(in);
    		break;
    	case OFFSET_TARGET__INSTANCE:
    	case OFFSET_TARGET__NEW:
    	case OFFSET_TARGET__METHODREF_NEW:
    	case OFFSET_TARGET__METHODREF_IDENTIFIER:
    		readOffsetTarget(in);
    		break;
    	case TYPE_ARGUMENT_TARGET__CAST:
    	case TYPE_ARGUMENT_TARGET__CONSTRUCTOR:
    	case TYPE_ARGUMENT_TARGET__GENERIC:
    	case TYPE_ARGUMENT_TARGET__METHODREF_NEW:
    	case TYPE_ARGUMENT_TARGET__METHODREF_IDENTIFIER:
    		readTypeArgumentTarget(in);
    		break;
    	}
    	typePathLength=in.readUnsignedByte();
    	typePathEntries = new TypePathEntry[typePathLength];
    	for (int i=0; i < typePathLength; i++) {
    		typePathEntries[i]=readTypePathEntry(in);
    	}
    	annotation = new AnnotationElementValue();
    	annotation.read(in);
        if (debug) debug("read ");
    }
    
    
    private void readTypeParameterTarget(DataInput in) throws IOException {
    	targetInfo_val1 = in.readUnsignedByte();
    	targetInfo_val2 = -1;
    }
    
    private void readSupertypeTarget(DataInput in) throws IOException {
    	targetInfo_val1 = in.readUnsignedShort();
    	targetInfo_val2 = -1;
    }

    private void readTypeParameterBoundTarget(DataInput in) throws IOException {
    	targetInfo_val1 = in.readUnsignedByte();
    	targetInfo_val2 = in.readUnsignedByte();
    }

    private void readEmptyTarget(DataInput in) throws IOException {
    	//Nothing to read.
    	targetInfo_val1 = -1;
    	targetInfo_val2 = -1;
    }

    private void readMethodFormalParameterTarget(DataInput in) throws IOException {
    	targetInfo_val1 = in.readUnsignedByte();
    	targetInfo_val2 = -1;
    }

    private void readThrowsTarget(DataInput in) throws IOException {
    	targetInfo_val1 = in.readUnsignedShort();
    	targetInfo_val2 = -1;
    }

    private void readLocalVarTarget(DataInput in) throws IOException {
    	targetInfo_val1 = in.readUnsignedShort();
    	targetInfo_val2 = -1;
    	localVarEntries = new LocalVarTarget[targetInfo_val1];
    	for (int i=0; i < targetInfo_val1; i++) {
    		localVarEntries[i]= readLocalVarTargetEntry(in);
    	}
    	
    }

    private void readCatchTarget(DataInput in) throws IOException {
    	targetInfo_val1 = in.readUnsignedShort();
    	targetInfo_val2 = -1;
    }

    private void readOffsetTarget(DataInput in) throws IOException {
    	targetInfo_val1 = in.readUnsignedShort();
    	targetInfo_val2 = -1;
    }

    private void readTypeArgumentTarget(DataInput in) throws IOException {
    	targetInfo_val1 = in.readUnsignedShort();
    	targetInfo_val2 = in.readUnsignedByte();
    }

    public int getTargetType() {
    	return targetType;
    }
    
    public int getTargetInfoVal1() {
    	return targetInfo_val1;
    }
    
    public int getTargetInfoVal2() {
    	return targetInfo_val2;
    }
    
    public String getEntryName() {
    	return getTargetTypeString();
    }
 
 
	public void write(DataOutput out) throws InvalidByteCodeException,
			IOException {
		throw new UnsupportedOperationException("Writing is not supported");
    }


    protected void debug(String message) {
        super.debug(message + "TypeAnnotations entry");
    }
    
    public String getTargetTypeString() {
    	switch (targetType) {
    	case TYPE_PARAMETER_TARGET__CLASS_INTERFACE:
    		return "TYPE_PARAMETER_TARGET__CLASS_INTERFACE";
    	case TYPE_PARAMETER_TARGET__METHOD:
    		return "TYPE_PARAMETER_TARGET__METHOD";
    	case SUPERTYPE_TARGET:
    		return "SUPERTYPE_TARGET";
    	case TYPE_PARAMETER_BOUND_TARGET__CLASS_INTERFACE:
    		return "TYPE_PARAMETER_BOUND_TARGET__CLASS_INTERFACE";
    	case TYPE_PARAMETER_BOUND_TARGET__METHOD:
    		return "TYPE_PARAMETER_BOUND_TARGET__METHOD";
    	case EMPTY_TARGET__FIELD:
    		return "EMPTY_TARGET__FIELD";
    	case EMPTY_TARGET__METHODRETURN_OBJECT:
    		return "EMPTY_TARGET__METHODRETURN_OBJECT";
    	case EMPTY_TARGET__METHOD_CONSTRUCTOR:
    		return "EMPTY_TARGET__METHOD_CONSTRUCTOR";
    	case METHOD_FORMAL_PARAMETER_TARGET:
    		return "METHOD_FORMAL_PARAMETER_TARGET";
    	case THROWS_TARGET:
    		return "THROWS_TARGET";
    	case LOCALVAR_TARGET__LOCAL:
    		return "LOCALVAR_TARGET__LOCAL";
    	case LOCALVAR_TARGET__RESOURCE:
    		return "LOCALVAR_TARGET__RESOURCE";
    	case CATCH_TARGET:
    		return "CATCH_TARGET";
    	case OFFSET_TARGET__INSTANCE:
    		return "OFFSET_TARGET__INSTANCE";
    	case OFFSET_TARGET__NEW:
    		return "OFFSET_TARGET__NEW";
    	case OFFSET_TARGET__METHODREF_NEW:
    		return "OFFSET_TARGET__METHODREF_NEW";
    	case OFFSET_TARGET__METHODREF_IDENTIFIER:
    		return "OFFSET_TARGET__METHODREF_IDENTIFIER";
    	case TYPE_ARGUMENT_TARGET__CAST:
    		return "TYPE_ARGUMENT_TARGET__CAST";
    	case TYPE_ARGUMENT_TARGET__CONSTRUCTOR:
    		return "TYPE_ARGUMENT_TARGET__CONSTRUCTOR";
    	case TYPE_ARGUMENT_TARGET__GENERIC:
    		return "TYPE_ARGUMENT_TARGET__GENERIC";
    	case TYPE_ARGUMENT_TARGET__METHODREF_NEW:
    		return "TYPE_ARGUMENT_TARGET__METHODREF_NEW";
    	case TYPE_ARGUMENT_TARGET__METHODREF_IDENTIFIER:
    		return "TYPE_ARGUMENT_TARGET__METHODREF_IDENTIFIER";
    	}
    	return "UNKNOWN_TYPE";
    }
    
    public String getTargetInfo() {
    	switch (targetType) {
    	case TYPE_PARAMETER_TARGET__CLASS_INTERFACE:
    	case TYPE_PARAMETER_TARGET__METHOD:
    		return "Parameter Index: "+targetInfo_val1;
    	case SUPERTYPE_TARGET:
    		return "Supertype Index: "+targetInfo_val1;
    	case TYPE_PARAMETER_BOUND_TARGET__CLASS_INTERFACE:
    	case TYPE_PARAMETER_BOUND_TARGET__METHOD:
    		return "Parameter Index: "+targetInfo_val1+"\nBound Index: "+targetInfo_val2;
    	case EMPTY_TARGET__FIELD:
    	case EMPTY_TARGET__METHODRETURN_OBJECT:
    	case EMPTY_TARGET__METHOD_CONSTRUCTOR:
    		return "{Empty}";
    	case METHOD_FORMAL_PARAMETER_TARGET:
    		return "Formal Parameter Index: "+targetInfo_val1;
    	case THROWS_TARGET:
    		return "Throws Type Index: "+targetInfo_val1;
    	case LOCALVAR_TARGET__LOCAL:
    	case LOCALVAR_TARGET__RESOURCE:
    		StringBuffer sb = new StringBuffer();
    		for (int i=0; i < targetInfo_val1; i++) {
    			LocalVarTarget target = localVarEntries[i];
    			sb.append("["+i+"] Start: "+target.start_pc+"\tLength: "+target.length+"\tIndex: "+target.index+"\n");
    		}
    		return sb.toString();
    	case CATCH_TARGET:
    		return "Exception Table Index: "+targetInfo_val1;
    	case OFFSET_TARGET__INSTANCE:
    	case OFFSET_TARGET__NEW:
    	case OFFSET_TARGET__METHODREF_NEW:
    	case OFFSET_TARGET__METHODREF_IDENTIFIER:
    		return "Offset: "+targetInfo_val1;
    	case TYPE_ARGUMENT_TARGET__CAST:
    	case TYPE_ARGUMENT_TARGET__CONSTRUCTOR:
    	case TYPE_ARGUMENT_TARGET__GENERIC:
    	case TYPE_ARGUMENT_TARGET__METHODREF_NEW:
    	case TYPE_ARGUMENT_TARGET__METHODREF_IDENTIFIER:
    		return "Offset: "+targetInfo_val1+"\nType Argument Index: "+targetInfo_val2;
    	}
    	return "UNSUPPORTED TYPE";

    }
    

    public int getLength() {
    	int length = INITIAL_LENGTH;
    	switch (targetType) {
    	case TYPE_PARAMETER_TARGET__CLASS_INTERFACE:
    	case TYPE_PARAMETER_TARGET__METHOD:
    		length+=1;
    		break;
    	case SUPERTYPE_TARGET:
    		length+=2;
    		break;
    	case TYPE_PARAMETER_BOUND_TARGET__CLASS_INTERFACE:
    	case TYPE_PARAMETER_BOUND_TARGET__METHOD:
    		length+=2;
    		break;
    	case EMPTY_TARGET__FIELD:
    	case EMPTY_TARGET__METHODRETURN_OBJECT:
    	case EMPTY_TARGET__METHOD_CONSTRUCTOR:
    		length+=0;
    		break;
    	case METHOD_FORMAL_PARAMETER_TARGET:
    		length+=1;
    		break;
    	case THROWS_TARGET:
    		length+=2;
    		break;
    	case LOCALVAR_TARGET__LOCAL:
    	case LOCALVAR_TARGET__RESOURCE:
    		length+=2 + localVarEntries.length*6;
    		break;
    	case CATCH_TARGET:
    		length+=2;
    		break;
    	case OFFSET_TARGET__INSTANCE:
    	case OFFSET_TARGET__NEW:
    	case OFFSET_TARGET__METHODREF_NEW:
    	case OFFSET_TARGET__METHODREF_IDENTIFIER:
    		length+=2;
    		break;
    	case TYPE_ARGUMENT_TARGET__CAST:
    	case TYPE_ARGUMENT_TARGET__CONSTRUCTOR:
    	case TYPE_ARGUMENT_TARGET__GENERIC:
    	case TYPE_ARGUMENT_TARGET__METHODREF_NEW:
    	case TYPE_ARGUMENT_TARGET__METHODREF_IDENTIFIER:
    		length+=3;
    		break;
    	}
    	length +=1 + typePathLength*2;
    	length += annotation.getLength();
    	return length;
    }
    
    public AnnotationElementValue getAnnotation() {
    	return annotation;
    }
    
    protected String printAccessFlagsVerbose(int accessFlags) {
        return "";
    }
    
	private LocalVarTarget readLocalVarTargetEntry(DataInput in) throws IOException {
		LocalVarTarget t = new LocalVarTarget();
		t.start_pc = in.readUnsignedShort();
		t.length = in.readUnsignedShort();
		t.index = in.readUnsignedShort();
		return t;
	}
	
	private TypePathEntry readTypePathEntry(DataInput in) throws IOException {
		TypePathEntry t = new TypePathEntry();
		t.kind = in.readUnsignedByte();
		t.index = in.readUnsignedByte();
		return t;
	}
	
    private class LocalVarTarget {
    	public int start_pc, length, index;
    }
    
    private class TypePathEntry {
    	public int kind, index;
    }

}


