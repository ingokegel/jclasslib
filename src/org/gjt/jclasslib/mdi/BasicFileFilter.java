/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.mdi;

import javax.swing.filechooser.FileFilter;
import java.io.File;

/**
    Configurable file filter for a <tt>JFileChooser</tt>.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.4 $ $Date: 2003-08-18 07:59:50 $
*/
public class BasicFileFilter extends FileFilter {

    private String[] extensions;
    private String description;

    /**
        Constructor.
        @param extensions the file extensions.
        @param description the description.
     */
    public BasicFileFilter(String[] extensions, String description) {

        this.extensions = extensions;

        StringBuffer buffer = new StringBuffer(description);
        buffer.append(" (");
        for (int i = 0; i < extensions.length; i++) {
            if (i > 0) {
                buffer.append(", ");
            }
            buffer.append("*.");
            buffer.append(extensions[i]);
        }
        buffer.append(")");

        this.description = buffer.toString();
    }

    /**
        Constructor.
        @param extension the file extension.
        @param description the description.
     */
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
        return description + "";
    }
}

