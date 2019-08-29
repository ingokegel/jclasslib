/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes.targettype

import org.gjt.jclasslib.structures.attributes.SubStructure

/**
 * Base class for target infos in a TypeAnnotation structure.
 */
abstract class TargetInfo : SubStructure() {

    /**
     * A verbose description for the target info.
     */
    abstract val verbose: String

}
