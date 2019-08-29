/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.ClassFileEnum
import org.gjt.jclasslib.structures.Lookup

/**
 * Describes the kind of a type path in a TypePathEntry structure.
 */
@Suppress("NOT_DOCUMENTED")
enum class TypePathKind(override val tag: Int) : ClassFileEnum {
    DEEPER_IN_ARRAY_TYPE(0),
    DEEPER_IN_NESTED_TYPE(1),
    WILDCARD_BOUND(2),
    TYPE_ARGUMENT(3);

    companion object : Lookup<TypePathKind>(TypePathKind::class.java, "type path kind")
}
