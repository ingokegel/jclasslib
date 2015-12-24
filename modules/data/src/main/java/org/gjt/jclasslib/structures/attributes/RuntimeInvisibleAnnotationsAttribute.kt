/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.structures.attributes

/**
 * Describes an  RuntimeInvisibleAnnotations attribute structure.

 * @author [Vitor Carreira](mailto:vitor.carreira@gmail.com)
 */
class RuntimeInvisibleAnnotationsAttribute : RuntimeAnnotationsAttribute() {


    override fun debug(message: String) {
        super.debug("$message RuntimeInvisibleAnnotations attribute with ${runtimeAnnotations.size} entries")
    }

    companion object {
        /**
         * Name of the attribute as in the corresponding constant pool entry.
         */
        val ATTRIBUTE_NAME = "RuntimeInvisibleAnnotations"
    }
}
