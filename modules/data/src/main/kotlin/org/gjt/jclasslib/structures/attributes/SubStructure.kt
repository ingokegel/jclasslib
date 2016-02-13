/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.Structure

/**
 * Base class for all sub-structures that are used in attribute infos.
 */
abstract class SubStructure : Structure() {

    /**
     * The length of the structure in bytes.
     */
    abstract val length: Int
}