/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io;

import java.io.*;

/**
    <tt>OutputStream</tt> which counts the number of bytes written.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.3 $ $Date: 2003-08-18 07:58:12 $
*/
public class CountedOutputStream extends FilterOutputStream {

    private int bytesWritten = 0;

    /**
        Constructor.
        @param out the output stream.
     */
    public CountedOutputStream(OutputStream out) {
        super(out);
    }
    
    public void write(int b) throws IOException {
    	out.write(b);
        bytesWritten++;
    }
   
    /**
        Get the number of bytes written.
        @return the number of bytes
     */
    public int getBytesWritten() {
        return bytesWritten;
    }
}
