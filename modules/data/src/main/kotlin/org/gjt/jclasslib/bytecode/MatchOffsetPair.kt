/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.bytecode

/**
 * Holds a single match-offset pair.
 * @property match The match value.
 * @property offset The bytecode offset.
 */
class MatchOffsetPair(@Suppress("NOT_DOCUMENTED") var match: Int, var offset: Int)
