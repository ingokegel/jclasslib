/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath;

import javax.swing.tree.DefaultTreeModel;

/**
    A logical component of the classpath.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 08:10:15 $
*/
public interface ClasspathComponent {

    /**
     * Find a class by name within thsi classpath component.
     * @param className the name of the class
     * @return the <tt>FindResult</tt> object. <tt>null</tt> if no class could be found.
     */
    public FindResult findClass(String className);

    /**
     * Merge all classes in this classpath component into the supplied tree.
     * @param model the tree model.
     * @param reset whether this is an incremental operation or part of a reset.
     *              For a reset, no change events will be fired on the tree model.
     */
    public void mergeClassesIntoTree(DefaultTreeModel model, boolean reset);

    /**
     * Add a <tt>ClasspathChangeListener</tt>.
     * @param listener the listener
     */
    public void addClasspathChangeListener(ClasspathChangeListener listener);

    /**
     * Remove a <tt>ClasspathChangeListener</tt>.
     * @param listener the listener
     */
    public void removeClasspathChangeListener(ClasspathChangeListener listener);
}
