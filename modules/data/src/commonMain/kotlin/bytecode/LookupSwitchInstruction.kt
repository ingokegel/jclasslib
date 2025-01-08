/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license or (at your option) any later version.
*/

package org.gjt.jclasslib.bytecode

import org.gjt.jclasslib.io.CountingDataInput
import org.gjt.jclasslib.io.CountingDataOutput

/**
 * Describes the lookupswitch instruction.
 */
class LookupSwitchInstruction : PaddedInstruction(Opcode.LOOKUPSWITCH) {

    /**
     * Default offset for the branch of this instruction.
     */
    var defaultOffset: Int = 0

    /**
     * Match-offset pairs for the branches of this instruction as
     * a java.util.List of MatchOffsetPair
     * elements.
     * @return the list
     */
    var matchOffsetPairs: MutableList<MatchOffsetPair> = ArrayList()

    override val size: Int
        get() = super.size + 8 + 8 * matchOffsetPairs.size

    override fun read(input: CountingDataInput) {
        super.read(input)

        matchOffsetPairs.clear()

        defaultOffset = input.readInt()
        val numberOfPairs = input.readInt()

        repeat(numberOfPairs) {
            val match = input.readInt()
            val offset = input.readInt()

            matchOffsetPairs.add(MatchOffsetPair(match, offset))
        }

    }

    override fun write(output: CountingDataOutput) {
        super.write(output)

        output.writeInt(defaultOffset)

        val numberOfPairs = matchOffsetPairs.size
        output.writeInt(numberOfPairs)

        for (matchOffsetPair in matchOffsetPairs) {
            output.writeInt(matchOffsetPair.match)
            output.writeInt(matchOffsetPair.offset)
        }
    }

}
