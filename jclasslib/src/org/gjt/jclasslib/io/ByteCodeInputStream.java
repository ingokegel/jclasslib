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
    @version $Revision: 1.3 $ $Date: 2003-07-08 14:04:28 $
*/
public class ByteCodeInputStream extends DataInputStream
                                 implements ByteCodeInput
{

    public ByteCodeInputStream(InputStream in) {
        super(new CountedInputStream(in));
    }
    
    public int getBytesRead() {
        return ((CountedInputStream)in).getBytesRead();
    }
    
}
