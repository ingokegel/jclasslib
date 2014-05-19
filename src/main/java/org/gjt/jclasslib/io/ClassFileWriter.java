/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io;

import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.*;

/**
    Converts class file structure <tt>ClassFile</tt> as defined in
    <tt>org.gjt.jclasslib.structures</tt> to class files.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ClassFileWriter {

    private ClassFileWriter() {
    }

    /**
        Converts a <tt>ClassFile</tt> structure to a class file.
        @param file the file to which to write the <tt>ClassFile</tt> structure
        @param classFile the <tt>ClassFile</tt> structure to be written
        @throws InvalidByteCodeException if the code is invalid
        @throws IOException if an exception occurs while reading the file
     */
    public static void writeToFile(File file, ClassFile classFile)
        throws InvalidByteCodeException, IOException {
            
        DataOutputStream out = new DataOutputStream(
                                new BufferedOutputStream(
                                new FileOutputStream(file)));
        
        classFile.write(out);
        out.flush();
        out.close();
    }

    /**
     * Converts a <tt>ClassFile</tt> structure to a byte array.
     * @param classFile the class file
     */
    public static byte[] writeToByteArray(ClassFile classFile)
        throws InvalidByteCodeException, IOException {

        ByteArrayOutputStream result = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(result);

        classFile.write(out);
        out.flush();
        out.close();

        return result.toByteArray();
    }

}
