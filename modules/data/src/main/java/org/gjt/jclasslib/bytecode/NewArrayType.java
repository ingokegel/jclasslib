/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.bytecode;

/**
 * Enum for the possible values of the immediate byte of the {@link org.gjt.jclasslib.bytecode.Opcode#NEWARRAY} opcode.
 */
public enum NewArrayType {

    BOOLEAN(4, "boolean"),
    CHAR(5, "char"),
    FLOAT(6, "float"),
    DOUBLE(7, "double"),
    BYTE(8, "byte"),
    SHORT(9, "short"),
    INT(10, "int"),
    LONG(11, "long");

    public static NewArrayType getFromCode(int code) {
        for (NewArrayType newArrayType : values()) {
            if (newArrayType.code == code) {
                return newArrayType;
            }
        }
        return null;
    }

    private int code;
    private String verbose;

    NewArrayType(int code, String verbose) {
        this.code = code;
        this.verbose = verbose;
    }

    /**
     * Returns the immediate byte value.
     * @return the value
     */
    public int getCode() {
        return code;
    }

    /**
     * Returns the verbose representation.
     * @return the text
     */
    public String getVerbose() {
        return verbose;
    }
}
