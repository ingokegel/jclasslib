/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io;

import java.io.*;

/**
    Extends <tt>DataOutput</tt> to accomodate for a method to retrieve the number
    of bytes written.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2002-02-27 16:47:43 $
*/
public interface ByteCodeOutput extends DataOutput {

    /**
        Get the number of bytes written.
        @return the number of bytes
     */
    public int getBytesWritten();
    
}
