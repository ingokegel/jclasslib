/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.mdi;

import javax.swing.filechooser.FileFilter;
import java.io.*;

/**
    Configurable file filter for a <tt>JFileChooser</tt>.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2002-02-27 16:47:43 $
*/
public class BasicFileFilter extends FileFilter {

    private String[] extensions;
    private String description;

    public BasicFileFilter(String[] extensions, String description) {

        this.extensions = extensions;
        this.description = description;
    }

    public BasicFileFilter(String extension, String description) {

        this(new String[] {extension}, description);
    }

    public boolean accept(File file) {

        if (extensions == null)
            return true;

        for (int i = 0; i < extensions.length; i++) {
            if (file.isDirectory() || file.getName().endsWith(extensions[i]))
                return true;
        }
        return false;
    }

    public String getDescription() {
        return description;
    }
}

