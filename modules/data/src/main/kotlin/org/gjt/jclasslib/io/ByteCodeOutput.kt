/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io

import java.io.DataOutput

/**
 * Extends DataOutput to accommodate for a method to retrieve the number
 * of bytes written.
 */
interface ByteCodeOutput : DataOutput {

    /**
     * Get the number of bytes written.
     */
    val bytesWritten: Int

}
