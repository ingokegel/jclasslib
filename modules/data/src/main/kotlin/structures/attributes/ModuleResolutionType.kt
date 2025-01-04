/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.ClassFileFlag
import org.gjt.jclasslib.structures.FlagLookup

/**
 * Enum for the possible values of the [ModuleResolutionAttribute] attribute.
 * @property flag Immediate byte value.
 * @property verbose Verbose representation.
 */
@Suppress("NOT_DOCUMENTED")
enum class ModuleResolutionType(override val flag: Int, override val verbose: String) : ClassFileFlag {
    DO_NOT_RESOLVE_BY_DEFAULT(1, "Resolve by default"),
    WARN_DEPRECATED(2, "Warn deprecated"),
    RESOLUTION_WARN_DEPRECATED_FOR_REMOVAL(4, "Warn deprecated for Removal"),
    RESOLUTION_WARN_INCUBATING(8, "Warn incubating");

    override fun toString() = verbose
    override val historical get() = false

    companion object : FlagLookup<ModuleResolutionType>()
}