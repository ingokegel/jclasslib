/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.structures;

/**
    Defines access flags constants and verbose expressions as defined by
    the java access modifiers.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:17 $
*/
public interface AccessFlags {

    public static final int ACC_PUBLIC =       0x0001;
    public static final int ACC_PRIVATE =      0x0002;
    public static final int ACC_PROTECTED =    0x0004;
    public static final int ACC_STATIC =       0x0008;
    public static final int ACC_FINAL =        0x0010;
    public static final int ACC_SYNCHRONIZED = 0x0020;
    /** For ClassFile structures, 0x0020 is ACC_SUPER, which has historical
    significance only */
    public static final int ACC_SUPER        = 0x0020;
    public static final int ACC_VOLATILE =     0x0040;
    public static final int ACC_TRANSIENT =    0x0080;
    public static final int ACC_NATIVE =       0x0100;
    public static final int ACC_INTERFACE =    0x0200;
    public static final int ACC_ABSTRACT =     0x0400;
    public static final int ACC_STRICT =       0x0800;

    /** All access flag bits for generating a verbose list in a loop */
    public static final int[] allAccessFlags = new int[] {
        ACC_PUBLIC,
        ACC_PRIVATE,
        ACC_PROTECTED,
        ACC_STATIC,
        ACC_FINAL,
        ACC_SYNCHRONIZED,
        ACC_VOLATILE,
        ACC_TRANSIENT,
        ACC_NATIVE,
        ACC_INTERFACE,
        ACC_ABSTRACT,
        ACC_STRICT
    };
    
    public static final String ACC_PUBLIC_VERBOSE =       "public";
    public static final String ACC_PRIVATE_VERBOSE =      "private";
    public static final String ACC_PROTECTED_VERBOSE =    "protected";
    public static final String ACC_STATIC_VERBOSE =       "static";
    public static final String ACC_FINAL_VERBOSE =        "final";
    public static final String ACC_SYNCHRONIZED_VERBOSE = "synchronized";
    public static final String ACC_VOLATILE_VERBOSE =     "volatile";
    public static final String ACC_TRANSIENT_VERBOSE =    "transient";
    public static final String ACC_NATIVE_VERBOSE =       "native";
    public static final String ACC_INTERFACE_VERBOSE =    "interface";
    public static final String ACC_ABSTRACT_VERBOSE =     "abstract";
    public static final String ACC_STRICT_VERBOSE =       "strict";

    
    /**
        All verbose descriptions of access flag bits for generating a verbose
        list in a loop
    */
    public static final String[] allAccessFlagsVerbose = new String[] {
        ACC_PUBLIC_VERBOSE,
        ACC_PRIVATE_VERBOSE,
        ACC_PROTECTED_VERBOSE,
        ACC_STATIC_VERBOSE,
        ACC_FINAL_VERBOSE,
        ACC_SYNCHRONIZED_VERBOSE,
        ACC_VOLATILE_VERBOSE,
        ACC_TRANSIENT_VERBOSE,
        ACC_NATIVE_VERBOSE,
        ACC_INTERFACE_VERBOSE,
        ACC_ABSTRACT_VERBOSE,
        ACC_STRICT_VERBOSE
    };
    
}
