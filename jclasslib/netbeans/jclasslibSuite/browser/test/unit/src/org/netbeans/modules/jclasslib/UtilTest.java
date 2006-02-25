/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
 */

package org.netbeans.modules.jclasslib;

import junit.framework.TestCase;

/**
 * Test {@link Util} class.
 *
 * @author Martin Krauskopf
 */
public class UtilTest extends TestCase {
    
    public UtilTest(String testName) {
        super(testName);
    }
    
    
    public void testGetJVMFieldType() {
        String nbToJVM[][] = {
            { "java.lang.String", "Ljava/lang/String;" },
            { "org.foo.Clazz", "Lorg/foo/Clazz;" },
            { "int", "I" },
        };
        for (int i = 0; i < nbToJVM.length; i++) {
            assertEquals("conversion works", nbToJVM[i][1], Util.getJVMFieldType(nbToJVM[i][0]));
        }

    }
    
}
