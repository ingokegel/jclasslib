/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.nbmodule;

import org.gjt.jclasslib.browser.config.window.BrowserPath;
import org.netbeans.modules.java.JavaCompilerType;
import org.netbeans.modules.java.settings.JavaSettings;
import org.openide.compiler.CompilerType;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.*;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.actions.CookieAction;

import javax.swing.*;

/**
    Action to open a class file.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.6 $ $Date: 2003-08-18 07:56:58 $
*/
public class OpenAction extends CookieAction {

    static FileSystem getTargetFileSystem(FileObject foSource) {

        JavaSettings javaSettings = (JavaSettings)Lookup.getDefault().lookup(JavaSettings.class);
        CompilerType compiler = javaSettings.getCompiler();

        if (!(compiler instanceof JavaCompilerType)) {
            return null;
        }
        FileSystem targetFs = ((JavaCompilerType)compiler).getTargetFileSystem();
        if (targetFs == null) {
            try {
                targetFs = foSource.getFileSystem();
            } catch (FileStateInvalidException ex) {
                return null;
            }
        }
        return targetFs;

    }

    static void openFileObject(FileObject fo, final BrowserPath browserPath) {

        final ClassFileViewer viewer = ClassFileViewer.getCachedClassFileViewer(fo);
        if (!viewer.isOpened()) {
            viewer.open();
        }
        SwingUtilities.invokeLater(
                new Runnable () {
                     public void run () {
                         viewer.getBrowserComponent().setBrowserPath(browserPath);
                         viewer.requestFocus();
                     }
                }
        );
    }

    public String getName () {
        return "View class file";
    }
    
    protected String iconResource () {
        return "/org/gjt/jclasslib/nbmodule/nbmodule.gif";
    }
    
    protected boolean enable(Node[] nodes) {
        return getClassFileObject(nodes) != null;
    }

    protected Class[] cookieClasses() {
        return new Class[] { OpenCookie.class };
    }

    protected int mode () {
        return MODE_EXACTLY_ONE;
    }
   
    protected void performAction (Node[] nodes) {

        FileObject fo = getClassFileObject(nodes);
        if (fo != null) {
            openFileObject(fo, null);
        }
    }

    public HelpCtx getHelpCtx () {
        return HelpCtx.DEFAULT_HELP;
    }
    
    private FileObject getClassFileObject(Node[] nodes) {

        if (nodes == null || nodes.length == 0) {
            return null;
        }
        
        DataObject dataObject = (DataObject)nodes[0].getCookie(DataObject.class);
        if (dataObject == null) {
            return null;
        } else {
            return lookupFileObject(dataObject);
        }
    }
    
    private FileObject lookupFileObject(DataObject dataObject) {

        FileObject foSource = dataObject.getPrimaryFile();
        if (foSource.hasExt("class")) {
            return foSource;

        } else if (foSource.hasExt("java")) {
            FileSystem targetFs = getTargetFileSystem(foSource);

            String className = foSource.getPackageName('/');
            return targetFs.findResource(className + ".class");
        } else {
            return null;
        }
    }
        
}

