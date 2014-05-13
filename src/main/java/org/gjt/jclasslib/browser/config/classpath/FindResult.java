/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath;

/**
    Result of a search operation on the classpath.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 08:10:15 $
*/
public class FindResult {

    private ClasspathEntry classpathEntry;
    private String fileName;

    /**
     * Constructor.
     * @param classpathEntry the classpath entry in which the class has been found.
     * @param fileName the file name of the found class.
     */
    public FindResult(ClasspathEntry classpathEntry, String fileName) {
        this.classpathEntry = classpathEntry;
        this.fileName = fileName;
    }

    /**
     * Get the classpath entry in which the class has been found.
     * @return the classpath entry.
     */
    public ClasspathEntry getClasspathEntry() {
        return classpathEntry;
    }

    /**
     * Get the file name of the found class.
     * @return the file name.
     */
    public String getFileName() {
        return fileName;
    }

}
