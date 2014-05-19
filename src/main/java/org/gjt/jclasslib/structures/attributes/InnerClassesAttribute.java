/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures.attributes;

import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
    Describes an <tt>InnerClasses</tt> attribute structure.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class InnerClassesAttribute extends AttributeInfo {

    /** Name of the attribute as in the corresponding constant pool entry. */
    public static final String ATTRIBUTE_NAME = "InnerClasses";

    private static final int INITIAL_LENGTH = 2;
    
    private InnerClassesEntry[] classes;
    
    /**
        Get the list of inner classes of the parent <tt>ClassFile</tt> structure
        as an array of <tt>InnerClassesEntry</tt> structures.
        @return the array
     */
      public InnerClassesEntry[] getClasses() {
        return classes;
    }

    /**
        Set the list of inner classes of the parent <tt>ClassFile</tt> structure
        as an array of <tt>InnerClassesEntry</tt> structures.
        @param classes the array
     */
    public void setClasses(InnerClassesEntry[] classes) {
        this.classes = classes;
    }
    
    public void read(DataInput in) throws InvalidByteCodeException, IOException {
            
        int numberOfClasses = in.readUnsignedShort();
        classes = new InnerClassesEntry[numberOfClasses];
        
        for (int i = 0; i < numberOfClasses; i++) {
            classes[i] = InnerClassesEntry.create(in, classFile);
        }

        if (debug) debug("read ");
    }

    public void write(DataOutput out) throws InvalidByteCodeException, IOException {
        super.write(out);

        int numberOfClasses = getLength(classes);
        
        out.writeShort(numberOfClasses);
        for (int i = 0 ; i < numberOfClasses; i++) {
            classes[i].write(out);
        }
        if (debug) debug("wrote ");
    }

    public int getAttributeLength() {
        return INITIAL_LENGTH + getLength(classes) * InnerClassesEntry.LENGTH;
    }

    protected void debug(String message) {
        super.debug(message + "InnerClasses attribute with " + getLength(classes) + " classes");
    }

}
