/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.classpath;

import javax.swing.tree.DefaultTreeModel;
import java.io.File;

/**
    Classpath entry for a directory.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 08:10:15 $
*/
public class ClasspathDirectoryEntry extends ClasspathEntry {

    public FindResult findClass(String className) {

        File file = getFile();
        if (file == null) {
            return null;
        }
        File classFile = new File(file, className.replace('.', '/') + ".class");
        if (classFile.exists() && classFile.canRead()) {
            FindResult findResult = new FindResult(this, classFile.getPath());
            return findResult;
        }

        return null;
    }

    public void mergeClassesIntoTree(DefaultTreeModel model, boolean reset) {

        File directory = getFile();
        if (directory == null) {
            return;
        }

        ClassTreeNode rootNode = (ClassTreeNode)model.getRoot();
        mergeDirectory(directory, rootNode, model, reset);

    }

    private void mergeDirectory(File directory, ClassTreeNode parentNode, DefaultTreeModel model, boolean reset) {

        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                ClassTreeNode directoryNode = addOrFindNode(file.getName(), parentNode, true, model, reset);
                mergeDirectory(file, directoryNode, model, reset);
                if ((directoryNode.getChildCount() == 0)) {
                    int deletionIndex = parentNode.getIndex(directoryNode);
                    parentNode.remove(directoryNode);
                    if (!reset) {
                        model.nodesWereRemoved(parentNode, new int[] {deletionIndex}, new Object[] {directoryNode});
                    }
                }
            } else if (file.getName().toLowerCase().endsWith(CLASSFILE_SUFFIX)) {
                addOrFindNode(stripClassSuffix(file.getName()), parentNode, false, model, reset);
            }
        }

    }

}
