/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures;

/**
 * Represents major versions of Java
 */
public enum JavaMajorVersion {

    JAVA_1(45, "Java 1.1"),
    JAVA_2(46, "Java 1.2"),
    JAVA_3(47, "Java 1.3"),
    JAVA_4(48, "Java 1.4"),
    JAVA_5(49, "Java 5"),
    JAVA_6(50, "Java 6"),
    JAVA_7(51, "Java 7"),
    JAVA_8(52, "Java 8");

    private int byteCodeMajorVersion;
    private String verbose;

    JavaMajorVersion(int byteCodeMajorVersion, String verbose) {
        this.byteCodeMajorVersion = byteCodeMajorVersion;
        this.verbose = verbose;
    }

    public int getByteCodeMajorVersion() {
        return byteCodeMajorVersion;
    }

    /**
     * Get the major version for
     *
     * @param byteCodeMajorVersion numeric value of major version in the bytecode
     * @return the enum value if byteCodeMajorVersion is valid, <tt>null </tt>otherwise
     */
    public static JavaMajorVersion valueOf(int byteCodeMajorVersion) {

        for (JavaMajorVersion javaMajorVersion : values()) {
            if (javaMajorVersion.getByteCodeMajorVersion() == byteCodeMajorVersion) {
                return javaMajorVersion;
            }
        }
        return null;
    }


    @Override
    public String toString() {
        return verbose;
    }
}
