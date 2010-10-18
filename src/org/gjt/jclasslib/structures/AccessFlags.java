/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures;

/**
 * Defines access flags constants and verbose expressions as defined by
 * the java access modifiers.
 *
 * @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>, <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 * @version $Revision: 1.5 $ $Date: 2004-12-29 10:45:58 $
 */
public interface AccessFlags {

    public static final int ACC_PUBLIC = 0x0001;
    public static final int ACC_PRIVATE = 0x0002;
    public static final int ACC_PROTECTED = 0x0004;
    public static final int ACC_STATIC = 0x0008;
    public static final int ACC_FINAL = 0x0010;
    public static final int ACC_SYNCHRONIZED = 0x0020;
    /**
     * For ClassFile structures, 0x0020 is ACC_SUPER, which has historical significance only
     */
    public static final int ACC_SUPER = 0x0020;
    public static final int ACC_VOLATILE = 0x0040;
    public static final int ACC_TRANSIENT = 0x0080;

    public static final int ACC_NATIVE = 0x0100;
    public static final int ACC_INTERFACE = 0x0200;
    public static final int ACC_ABSTRACT = 0x0400;
    public static final int ACC_STRICT = 0x0800;

    /**
     * new in Java 1.4
     */
    public final static int ACC_SYNTHETIC = 0x1000;
    /**
     * new in Java 1.5
     */
    public final static int ACC_ANNOTATION = 0x2000;
    /**
     * new in Java 1.5
     */
    public final static int ACC_ENUM = 0x4000;
    /**
     * new in Java 1.5
     */
    public final static int ACC_BRIDGE = 0x0040;
    /**
     * new in Java 1.5
     */
    public final static int ACC_VARARGS = 0x0080;


    /**
     * Class access flag bits for generating a verbose list in a loop.
     */
    public static final int[] CLASS_ACCESS_FLAGS = new int[]{
        ACC_PUBLIC,
        ACC_FINAL,
        ACC_SUPER,
        ACC_INTERFACE,
        ACC_ABSTRACT,
        ACC_SYNTHETIC,
        ACC_ANNOTATION,
        ACC_ENUM
    };

    /**
     * Inner class access flag bits for generating a verbose list in a loop.
     */
    public static final int[] INNER_CLASS_ACCESS_FLAGS = new int[]{
        ACC_PUBLIC,
        ACC_PRIVATE,
        ACC_PROTECTED,
        ACC_STATIC,
        ACC_FINAL,
        ACC_INTERFACE,
        ACC_ABSTRACT,
        ACC_SYNTHETIC,
        ACC_ANNOTATION,
        ACC_ENUM
    };


    /**
     * Field access flag bits for generating a verbose list in a loop.
     */
    public static final int[] FIELD_ACCESS_FLAGS = new int[]{
        ACC_PUBLIC,
        ACC_PRIVATE,
        ACC_PROTECTED,
        ACC_STATIC,
        ACC_FINAL,
        ACC_VOLATILE,
        ACC_TRANSIENT,
        ACC_SYNTHETIC,
        ACC_ENUM
    };

    /**
     * Method access flag bits for generating a verbose list in a loop.
     */
    public static final int[] METHOD_ACCESS_FLAGS = new int[]{
        ACC_PUBLIC,
        ACC_PRIVATE,
        ACC_PROTECTED,
        ACC_STATIC,
        ACC_FINAL,
        ACC_SYNCHRONIZED,
        ACC_BRIDGE,
        ACC_VARARGS,
        ACC_NATIVE,
        ACC_ABSTRACT,
        ACC_STRICT,
        ACC_SYNTHETIC
    };

    public static final String ACC_SUPER_VERBOSE = "";

    public static final String ACC_PUBLIC_VERBOSE = "public";
    public static final String ACC_PRIVATE_VERBOSE = "private";
    public static final String ACC_PROTECTED_VERBOSE = "protected";
    public static final String ACC_STATIC_VERBOSE = "static";
    public static final String ACC_FINAL_VERBOSE = "final";
    public static final String ACC_SYNCHRONIZED_VERBOSE = "synchronized";
    public static final String ACC_VOLATILE_VERBOSE = "volatile";
    public static final String ACC_TRANSIENT_VERBOSE = "transient";
    public final static String ACC_BRIDGE_VERBOSE = "bridge";
    public final static String ACC_VARARGS_VERBOSE = "varargs";
    public static final String ACC_NATIVE_VERBOSE = "native";
    public static final String ACC_INTERFACE_VERBOSE = "interface";
    public static final String ACC_ABSTRACT_VERBOSE = "abstract";
    public static final String ACC_STRICT_VERBOSE = "strict";
    public final static String ACC_SYNTHETIC_VERBOSE = "synthetic";
    public final static String ACC_ANNOTATION_VERBOSE = "annotation";
    public final static String ACC_ENUM_VERBOSE = "enum";

    /**
     * Class verbose descriptions of access flag bits for generating a verbose
     * list in a loop.
     */
    public static final String[] CLASS_ACCESS_FLAGS_VERBOSE = new String[]{
        ACC_PUBLIC_VERBOSE,
        ACC_FINAL_VERBOSE,
        ACC_SUPER_VERBOSE,
        ACC_INTERFACE_VERBOSE,
        ACC_ABSTRACT_VERBOSE,
        ACC_SYNTHETIC_VERBOSE,
        ACC_ANNOTATION_VERBOSE,
        ACC_ENUM_VERBOSE
    };

    /**
     * Inner class verbose descriptions of access flag bits for generating a verbose
     * list in a loop.
     */
    public static final String[] INNER_CLASS_ACCESS_FLAGS_VERBOSE = new String[]{
        ACC_PUBLIC_VERBOSE,
        ACC_PRIVATE_VERBOSE,
        ACC_PROTECTED_VERBOSE,
        ACC_STATIC_VERBOSE,
        ACC_FINAL_VERBOSE,
        ACC_INTERFACE_VERBOSE,
        ACC_ABSTRACT_VERBOSE,
        ACC_SYNTHETIC_VERBOSE,
        ACC_ANNOTATION_VERBOSE,
        ACC_ENUM_VERBOSE
    };


    /**
     * Field verbose descriptions of access flag bits for generating a verbose
     * list in a loop.
     */
    public static final String[] FIELD_ACCESS_FLAGS_VERBOSE = new String[]{
        ACC_PUBLIC_VERBOSE,
        ACC_PRIVATE_VERBOSE,
        ACC_PROTECTED_VERBOSE,
        ACC_STATIC_VERBOSE,
        ACC_FINAL_VERBOSE,
        ACC_VOLATILE_VERBOSE,
        ACC_TRANSIENT_VERBOSE,
        ACC_SYNTHETIC_VERBOSE,
        ACC_ENUM_VERBOSE
    };

    /**
     * Field verbose descriptions of access flag bits for generating a verbose
     * list in a loop.
     */
    public static final String[] METHOD_ACCESS_FLAGS_VERBOSE = new String[]{
        ACC_PUBLIC_VERBOSE,
        ACC_PRIVATE_VERBOSE,
        ACC_PROTECTED_VERBOSE,
        ACC_STATIC_VERBOSE,
        ACC_FINAL_VERBOSE,
        ACC_SYNCHRONIZED_VERBOSE,
        ACC_BRIDGE_VERBOSE,
        ACC_VARARGS_VERBOSE,
        ACC_NATIVE_VERBOSE,
        ACC_ABSTRACT_VERBOSE,
        ACC_STRICT_VERBOSE,
        ACC_SYNTHETIC_VERBOSE
    };
}
