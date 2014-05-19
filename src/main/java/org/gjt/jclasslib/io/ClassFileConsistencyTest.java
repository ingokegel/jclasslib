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
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassFileConsistencyTest {

    private static final ThreadLocal<byte[]> BUFFER_TL = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[1024];
        }
    };

    private static void scanJar(File file) throws IOException, InvalidByteCodeException {
        JarFile jar = new JarFile(file);
        Enumeration<JarEntry> entries = jar.entries();
        int count = 0;
        int errors = 0;
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            int index = name.lastIndexOf(".class");
            if (index > 0) {
                String className = name.substring(0, index).replace("/", ".");
                try {
                    if (!checkClassFile(className, jar, entry)) {
                        errors++;
                    }
                } catch (IOException e) {
                    error(className);
                    throw e;
                } catch (InvalidByteCodeException e) {
                    error(className);
                    throw e;
                } catch (RuntimeException e) {
                    error(className);
                    throw e;
                }
                count++;
            }
        }
        System.out.println(String.valueOf(count) + " classes checked, " + errors + " errors");
    }

    private static void error(String className) {
        System.err.println("ERROR when processing " + className);
    }

    private static boolean checkClassFile(String className, JarFile jar, JarEntry entry) throws IOException, InvalidByteCodeException {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        InputStream is = jar.getInputStream(entry);
        pumpStream(is, os);
        is.close();
        byte[] before = os.toByteArray();

        ClassFile classFile = ClassFileReader.readFromInputStream(jar.getInputStream(entry));
        byte[] after = ClassFileWriter.writeToByteArray(classFile);

        boolean success = compare(className, before, after);
        if (!success) {
            ClassFileReader.readFromInputStream(new ByteArrayInputStream(after));
        }
        return success;
    }

    private static boolean compare(String className, byte[] before, byte[] after) {
        if (before.length != after.length) {
            System.err.println("ERROR in " + className);
            System.err.println("Different length " + before.length + " != " + after.length);
            return false;
        }

        for (int i = 0; i < before.length; i++) {
            if (before[i] != after[i]) {
                System.err.println("Different byte at index " + i);
                return false;
            }
        }

        return true;
    }

    public static long pumpStream(InputStream is, OutputStream os) throws IOException {

        byte[] buffer = BUFFER_TL.get();
        int count;
        while ((count = is.read(buffer)) != -1) {
            os.write(buffer, 0, count);
        }
        return count;
    }

    public static void main(String[] args) throws Exception {
        String javaHome = System.getProperty("java.home");
        File rtJar = new File(javaHome + File.separator + "lib" + File.separator + "rt.jar");
        scanJar(rtJar);
    }
}
