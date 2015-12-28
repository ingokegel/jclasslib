/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io;

import java.io.DataInputStream;
import java.io.InputStream;

/**
    <tt>DataInputStream</tt> which extends <tt>ByteCodeInput</tt>.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ByteCodeInputStream extends DataInputStream
                                 implements ByteCodeInput
{

    /**
        Constructor.
        @param in the input stream.
     */
    public ByteCodeInputStream(InputStream in) {
        super(new CountedInputStream(in));
    }
    
    public int getBytesRead() {
        return ((CountedInputStream)in).getBytesRead();
    }
    
}
