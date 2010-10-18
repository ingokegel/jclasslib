/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath;

import javax.swing.tree.DefaultTreeModel;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
    Classpath entry for an archive.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2006-02-15 09:09:49 $
*/
public class ClasspathArchiveEntry extends ClasspathEntry {

    public FindResult findClass(String className) {

        File file = getFile();
        if (file == null) {
            return null;
        }
        className = className.replace('.', '/') + ".class";
        try {
            JarFile jarFile = new JarFile(file);
            JarEntry entry = jarFile.getJarEntry(className);
            if (entry != null) {
                FindResult findResult = new FindResult(this, file.getPath() + "!" + className);
                return findResult;
            }
        } catch (IOException e) {
        }

        return null;
    }

    public void mergeClassesIntoTree(DefaultTreeModel model, boolean reset) {

        File archive = getFile();
        if (archive == null) {
            return;
        }

        try {
            JarFile jarFile = new JarFile(archive);
            Enumeration en = jarFile.entries();
            while (en.hasMoreElements()) {
                JarEntry entry = (JarEntry)en.nextElement();
                if (!entry.isDirectory() && entry.getName().toLowerCase().endsWith(CLASSFILE_SUFFIX)) {
                    addEntry((stripClassSuffix(entry.getName())), model, reset);
                }
            }
        } catch (IOException ex) {
        }
    }

    private void addEntry(String path, DefaultTreeModel model, boolean reset) {

        String[] pathComponents = path.replace('\\', '/').split("/");
        ClassTreeNode currentNode = (ClassTreeNode)model.getRoot();
        for (int i = 0; i < pathComponents.length; i++) {
            String pathComponent = pathComponents[i];
            currentNode = addOrFindNode(pathComponent, currentNode, i < pathComponents.length - 1, model, reset);
        }
    }

}
