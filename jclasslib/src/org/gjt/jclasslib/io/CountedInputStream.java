/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io;

import java.io.*;

/**
    <tt>InputStream</tt> which counts the number of bytes read.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2002-02-27 16:47:43 $
*/
public class CountedInputStream extends FilterInputStream {

    private int bytesRead = 0;
    
    public CountedInputStream(InputStream in) {
        super(in);
    }
    
    public int read() throws IOException {
        int b = in.read();
        //if (b != -1) {
            bytesRead++;
        //}
        return b;
    }

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int offset, int len) throws IOException {
        int readCount = in.read(b, 0, b.length);
        bytesRead += readCount;
        return readCount;
        
    }
    
    public long skip(long n) throws IOException {
        long skipCount = in.skip(n);
        bytesRead += (int)skipCount;
        return skipCount;
    }

    // Marking invalidates bytesRead
    public boolean markSupported() {
        return false;
    }
   
    /**
        Get the number of bytes read.
        @return the number of bytes
     */
    public int getBytesRead() {
        return bytesRead;
    }
}
