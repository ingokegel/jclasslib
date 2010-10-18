/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config;

import org.gjt.jclasslib.browser.config.classpath.*;
import org.gjt.jclasslib.mdi.MDIConfig;

import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
    Workspace configuration object.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2003-08-21 14:40:45 $
*/
public class BrowserConfig implements ClasspathComponent {

    private MDIConfig mdiConfig;
    private List classpath = new ArrayList();
    private Set mergedEntries = new HashSet();
    private Set changeListeners = new HashSet();

    /**
     * Get the associated MDI configuration object.
     * @return the <tt>MDIConfig</tt> object.
     */
    public MDIConfig getMDIConfig() {
        return mdiConfig;
    }

    /**
     * Set the associated MDI configuration object.
     * @param mdiConfig the <tt>MDIConfig</tt> object.
     */
    public void setMDIConfig(MDIConfig mdiConfig) {
        this.mdiConfig = mdiConfig;
    }

    /**
     * Get the list of <tt>ClasspathEntry</tt> objects that define the classpath.
     * @return the list
     */
    public List getClasspath() {
        return classpath;
    }

    /**
     * Set the list of <tt>ClasspathEntry</tt> objects that define the classpath.
     * @param classpath the list
     */
    public void setClasspath(List classpath) {
        this.classpath = classpath;
    }

    public void addClasspathChangeListener(ClasspathChangeListener listener) {
        changeListeners.add(listener);
    }

    public void removeClasspathChangeListener(ClasspathChangeListener listener) {
        changeListeners.remove(listener);
    }

    /**
     * Add a classpath entry for a directory.
     * Has no effect of the classpath entry is already present.
     * @param directoryName the name of the directory.
     */
    public void addClasspathDirectory(String directoryName) {
        ClasspathDirectoryEntry entry = new ClasspathDirectoryEntry();
        entry.setFileName(directoryName);
        if (classpath.indexOf(entry) < 0) {
            classpath.add(entry);
            fireClasspathChanged(false);
        }
    }

    /**
     * Add a classpath entry for an archive.
     * Has no effect of the classpath entry is already present.
     * @param archiveName the path of he archive.
     */
    public void addClasspathArchive(String archiveName) {
        ClasspathArchiveEntry entry = new ClasspathArchiveEntry();
        entry.setFileName(archiveName);
        if (classpath.indexOf(entry) < 0) {
            classpath.add(entry);
            fireClasspathChanged(false);
        }
    }

    /**
     * Add a classpath entry.
     * Has no effect of the classpath entry is already present.
     * @param entry the entry.
     */
    public void addClasspathEntry(ClasspathEntry entry) {
        if (classpath.indexOf(entry) < 0) {
            classpath.add(entry);
            fireClasspathChanged(false);
        }
    }

    /**
     * Remove a classpath entry.
     * @param entry the entry.
     */
    public void removeClasspathEntry(ClasspathEntry entry) {
        if (classpath.remove(entry)) {
            fireClasspathChanged(true);
        }
    }

    /**
     * Add the <tt>rt.jar</tt> archive of the JRE used by the bytecode browser to the classpath.
     */
    public void addRuntimeLib() {

        String fileName = String.class.getResource("String.class").toExternalForm();
        Matcher matcher = Pattern.compile("jar:file:/(.*)!.*").matcher(fileName);
        if (matcher.matches()) {
            String path = matcher.group(1);
            if (path.indexOf(':') == -1) {
                path = "/" + path;
            }
            addClasspathArchive(new File(path).getPath());
            fireClasspathChanged(false);
        }
    }

    public FindResult findClass(String className) {

        Iterator it = classpath.iterator();
        while (it.hasNext()) {
            ClasspathEntry entry = (ClasspathEntry)it.next();
            FindResult findResult = entry.findClass(className);
            if (findResult != null) {
                return findResult;
            }
        }
        return null;
    }

    public void mergeClassesIntoTree(DefaultTreeModel model, boolean reset) {

        Iterator it = classpath.iterator();
        while (it.hasNext()) {
            ClasspathEntry entry = (ClasspathEntry)it.next();
            if (reset || !mergedEntries.contains(entry)) {
                entry.mergeClassesIntoTree(model, reset);
                mergedEntries.add(entry);
            }
        }
    }

    private void fireClasspathChanged(boolean removal) {
        Iterator it = changeListeners.iterator();
        ClasspathChangeEvent event = new ClasspathChangeEvent(this, removal);
        while (it.hasNext()) {
            ClasspathChangeListener listener = (ClasspathChangeListener)it.next();
            listener.classpathChanged(event);
        }
    }

}
