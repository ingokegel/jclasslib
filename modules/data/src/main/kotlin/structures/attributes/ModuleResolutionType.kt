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
 * Enum for the possible values of the [ModuleResolutionAttribute] attribute.
 * @property tag Immediate byte value.
 * @property verbose Verbose representation.
 */
@Suppress("NOT_DOCUMENTED")
enum class ModuleResolutionType(override val tag: Int, val verbose: String) : ClassFileEnum {
    DO_NOT_RESOLVE_BY_DEFAULT(1, "Resolve by default"),
    WARN_DEPRECATED(2, "Warn deprecated"),
    RESOLUTION_WARN_DEPRECATED_FOR_REMOVAL(4, "Warn deprecated for Removal"),
    RESOLUTION_WARN_INCUBATING(8, "Warn incubating");

    override fun toString() = verbose

    companion object : Lookup<ModuleResolutionType>(ModuleResolutionType::class.java, "module resolution type")
}