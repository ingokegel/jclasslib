/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes;


/**
 * Describes an  <tt>RuntimeVisibleTypeAnnotations</tt> attribute structure.
 */
public class RuntimeInvisibleTypeAnnotationsAttribute extends RuntimeTypeAnnotationsAttribute {
    /**
     * Name of the attribute as in the corresponding constant pool entry.
     */
    public static final String ATTRIBUTE_NAME = "RuntimeInvisibleTypeAnnotations";


    protected void debug(String message) {
        super.debug(message + "RuntimeInvisibleTypeAnnotations attribute with "
            + getLength(runtimeTypeAnnotations) + " entries");
    }

}
