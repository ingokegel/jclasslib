/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io;

import org.gjt.jclasslib.structures.AttributeInfo;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
    Converts class files to a class file structure <tt>ClassFile</tt> as defined in
    <tt>org.gjt.jclasslib.structures</tt>.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ClassFileReader {

    private ClassFileReader() {
    }

    /**
        Looks up a class file in the specified class path and converts it 
        to a <tt>ClassFile</tt> structure.
        @param classPath the class path from which to read the <tt>ClassFile</tt> structure
        @param packageName the name of the package in which the class resides
        @param className the simple name of the class
        @return the new <tt>ClassFile</tt> structure or <tt>null</tt> if it cannot be found
        @throws InvalidByteCodeException if the code is invalid
        @throws IOException if an exception occurs while reading the file
     */
    public static ClassFile readFromClassPath(String[] classPath, String packageName, String className)
        throws InvalidByteCodeException, IOException
    {
        
        String relativePath = packageName.replace('.', File.separatorChar) + (packageName.length() == 0 ? "" : File.separator) + className + ".class";
        String jarRelativePath = relativePath.replace(File.separatorChar, '/');
        for (String singlePath : classPath) {
            File currentClassPathEntry = new File(singlePath);
            if (!currentClassPathEntry.exists()) {
                continue;
            }
            if (currentClassPathEntry.isDirectory()) {
                File testFile = new File(currentClassPathEntry, relativePath);
                if (testFile.exists()) {
                    return readFromFile(testFile);
                }
            } else if (currentClassPathEntry.isFile()) {
                JarFile jarFile = new JarFile(currentClassPathEntry);
                try {
                    JarEntry jarEntry = jarFile.getJarEntry(jarRelativePath);
                    if (jarEntry != null) {
                        return readFromInputStream(jarFile.getInputStream(jarEntry));
                    }
                } finally {
                    jarFile.close();
                }
            }
        }

        return null;
    }

    /**
        Converts a class file to a <tt>ClassFile</tt> structure.
        @param file the file from which to read the <tt>ClassFile</tt> structure
        @return the new <tt>ClassFile</tt> structure
        @throws InvalidByteCodeException if the code is invalid
        @throws IOException if an exception occurs while reading the file
     */
    public static ClassFile readFromFile(File file)
        throws InvalidByteCodeException, IOException
    {

        return readFromInputStream(new FileInputStream(file));
    }

    /**
        Converts a class file to a <tt>ClassFile</tt> structure.
        @param is the input stream from which to read the
                  <tt>ClassFile</tt> structure
        @return the new <tt>ClassFile</tt> structure
        @throws InvalidByteCodeException if the code is invalid
        @throws IOException if an exception occurs while reading from
                            the input stream
     */
    public static ClassFile readFromInputStream(InputStream is)
        throws InvalidByteCodeException, IOException
    {

        DataInputStream in = new DataInputStream(
                                new BufferedInputStream(is));

        ClassFile classFile = new ClassFile();
        classFile.read(in);
        in.close();
        return classFile;
    }

    /**
     * Test method.
     * @param args arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        final int maxCount = 500;
        long startTime, endTime;

        File file = new File(args[0]);
        ClassFile classFile = readFromFile(file);

        startTime = System.currentTimeMillis();
        for (int i = 0; i < maxCount; i++) {
            classFile = readFromFile(file);
        }
        endTime = System.currentTimeMillis();
        System.out.println("With attributes:");
        System.out.print((endTime - startTime));
        System.out.println(" ms");

        System.setProperty(AttributeInfo.SYSTEM_PROPERTY_SKIP_ATTRIBUTES, "true");
        startTime = System.currentTimeMillis();
        for (int i = 0; i < maxCount; i++) {
            classFile = readFromFile(file);
        }
        endTime = System.currentTimeMillis();
        System.out.println("Without attributes:");
        System.out.print((endTime - startTime));
        System.out.println(" ms");

    }

}
