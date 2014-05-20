/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes;


/**
 * Describes an  <tt>RuntimeVisibleAnnotations</tt> attribute structure.
 *
 * @author <a href="mailto:vitor.carreira@gmail.com">Vitor Carreira</a>
 *
 */
public class RuntimeVisibleAnnotationsAttribute extends RuntimeAnnotationsAttribute {
    /**
     * Name of the attribute as in the corresponding constant pool entry.
     */
    public static final String ATTRIBUTE_NAME = "RuntimeVisibleAnnotations";


    protected void debug(String message) {
        super.debug(message + "RuntimeVisibleAnnotations attribute with "
                + getLength(runtimeAnnotations) + " entries");
    }

}
