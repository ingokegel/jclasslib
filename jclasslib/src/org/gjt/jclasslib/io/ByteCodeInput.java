/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io;

import java.io.*;

/**
    Extends <tt>DataInput</tt> to accomodate for a method to retrieve the number
    of bytes read.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:21 $
*/
public interface ByteCodeInput extends DataInput {

    /**
        Get the number of bytes read.
        @return the number of bytes
     */
    public int getBytesRead();
    
}
