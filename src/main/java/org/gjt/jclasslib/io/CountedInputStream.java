/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
    <tt>InputStream</tt> which counts the number of bytes read.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
*/
public class CountedInputStream extends FilterInputStream {

    private int bytesRead = 0;

    /**
        Constructor.
        @param in the input stream.
     */
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
