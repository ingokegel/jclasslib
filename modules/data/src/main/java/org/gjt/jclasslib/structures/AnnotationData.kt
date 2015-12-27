/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures;

import org.gjt.jclasslib.structures.elementvalues.ElementValuePair;

/**
 * Base class for annotation content.
 */
public interface AnnotationData {
    /**
     * Get the list of element value pair associations of the parent
     * structure as an array of <tt>ElementValuePair</tt> structures.
     *
     * @return the array
     */
    ElementValuePair[] getElementValuePairEntries();
    /**
     * Get the <tt>type_index</tt> of this annotation.
     *
     * @return the <tt>type_index</tt>
     */
    int getTypeIndex();
}
