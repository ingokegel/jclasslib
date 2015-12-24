/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes


/**
 * Describes a RuntimeVisibleParameterAnnotations attribute structure.
 */
class RuntimeVisibleParameterAnnotationsAttribute : RuntimeParameterAnnotationsAttribute() {

    override fun debug(message: String) {
        super.debug("$message RuntimeVisibleParameterAnnotations attribute with ${parameterAnnotations.size} entries")
    }

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        val ATTRIBUTE_NAME = "RuntimeVisibleParameterAnnotations"
    }

}
