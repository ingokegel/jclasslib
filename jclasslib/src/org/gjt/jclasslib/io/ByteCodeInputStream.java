/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io;

import java.io.*;

/**
    <tt>DataInputStream</tt> which extends <tt>ByteCodeInput</tt>.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2002-02-27 16:47:43 $
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
