/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes

import org.gjt.jclasslib.structures.InvalidByteCodeException

/**
 * Represents the available stack frame types in a stack map entry attribute.
 */
@Suppress("NOT_DOCUMENTED")
enum class StackFrameType(private val boundary: Int) {
    SAME(63), //0-63
    SAME_LOCALS_1_STACK_ITEM(127), //64-127
    SAME_LOCALS_1_STACK_ITEM_EXT(247), //247
    CHOP(250), //248-250
    SAME_EXT(251), //251
    APPEND(254), //252-254
    FULL(255); //255


    companion object {

        /**
         * Get the stack frame type from the associated bytecode.
         * @throws InvalidByteCodeException if the bytecode is invalid
         */
        @Throws(InvalidByteCodeException::class)
        fun getFromTag(tag: Int): StackFrameType {
            if (tag < 0) {
                throw InvalidByteCodeException("StackMapFrame tag must be positive")
            }
            if (tag <= SAME.boundary) {
                return SAME
            } else if (tag <= SAME_LOCALS_1_STACK_ITEM.boundary) {
                return SAME_LOCALS_1_STACK_ITEM
            } else if (tag == SAME_LOCALS_1_STACK_ITEM_EXT.boundary) {
                return SAME_LOCALS_1_STACK_ITEM_EXT
            } else if (tag > CHOP.boundary - 3 && tag <= CHOP.boundary) {
                return CHOP
            } else if (tag == SAME_EXT.boundary) {
                return SAME_EXT
            } else if (tag > APPEND.boundary - 3 && tag <= APPEND.boundary) {
                return APPEND
            } else if (tag == FULL.boundary) {
                return FULL
            } else {
                throw InvalidByteCodeException("Unsupported StackMapFrame tag: $tag")
            }
        }
    }

}
