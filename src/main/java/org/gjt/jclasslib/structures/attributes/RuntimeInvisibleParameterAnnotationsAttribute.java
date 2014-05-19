/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes;


/**
 * Describes an  <tt>RuntimeVisibleParameterAnnotations</tt> attribute structure.
 */
public class RuntimeInvisibleParameterAnnotationsAttribute extends RuntimeParameterAnnotationsAttribute {
    /**
     * Name of the attribute as in the corresponding constant pool entry.
     */
    public static final String ATTRIBUTE_NAME = "RuntimeInvisibleParameterAnnotations";

    protected void debug(String message) {
        super.debug(message + "RuntimeInvisibleParameterAnnotations attribute with "
            + getLength(getParameterAnnotations()) + " entries");
    }

}
