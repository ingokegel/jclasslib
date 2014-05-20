/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.test;

import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;

public class CurrentJdkTest extends ClassFileConsistencyTest {
    @Test
    public void test() throws IOException, InvalidByteCodeException {
        String javaHome = System.getProperty("java.home");
        File rtJar = new File(javaHome + File.separator + "lib" + File.separator + "rt.jar");
        scanJar(rtJar);
    }
}
