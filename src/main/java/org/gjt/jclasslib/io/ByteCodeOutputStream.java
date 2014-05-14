/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io;

import java.io.DataOutputStream;
import java.io.OutputStream;

/**
    <tt>DataOutputStream</tt> which extends <tt>ByteCodeOutput</tt>.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class ByteCodeOutputStream extends DataOutputStream
                                  implements ByteCodeOutput
{

    /**
        Constructor.
        @param out the output stream.
     */
    public ByteCodeOutputStream(OutputStream out) {
        super(new CountedOutputStream(out));
    }
    
    public int getBytesWritten() {
        return ((CountedOutputStream)out).getBytesWritten();
    }
}
