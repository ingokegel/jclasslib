/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.nbmodule;

import java.util.*;
import java.awt.Toolkit;
import javax.swing.JEditorPane;
import javax.swing.text.*;
import org.openide.*;
import org.openide.cookies.OpenCookie;
import org.openide.nodes.*;
import org.openide.loaders.*;
import org.openide.util.*;
import org.openide.windows.*;
import org.openide.filesystems.*;
import org.openide.util.actions.CookieAction;
import org.openide.compiler.CompilerType;

import org.netbeans.modules.java.*;

import java.lang.reflect.InvocationTargetException;

/**
    Action to open a class file.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2002-02-16 10:17:39 $
*/
public class OpenAction extends CookieAction {

    public String getName () {
        return "View class file";
    }
    
    protected String iconResource () {
        return "/images/nbmodule.gif";
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
            TopComponent viewer = ClassFileViewer.getCachedClassFileViewer(fo);
            if (!viewer.isOpened()) {
                viewer.open();
            }
            viewer.requestFocus();
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
            FileSystem targetFs = getTargetFileSystem();
            if (targetFs == null) {
                try {
                    targetFs = foSource.getFileSystem();
                } catch (FileStateInvalidException ex) {
                    return null;
                }
            }
            
            String className = foSource.getPackageName('/');
            return targetFs.findResource(className + ".class");
        } else {
            return null;
        }
    }
        
    private FileSystem getTargetFileSystem() {

        Node projectNode = TopManager.getDefault().getPlaces().nodes().project();

        Node javaNode = projectNode.getChildren().findChild("Java Sources");
        Sheet.Set propSet = (Sheet.Set)javaNode.getPropertySets()[0];
        Node.Property compilerProperty = propSet.get("compiler");

        CompilerType compiler = null;
        try {
            compiler = (CompilerType)compilerProperty.getValue();
        } catch (IllegalAccessException ex) {
            return null;
        } catch (InvocationTargetException ex) {
            return null;
        }

        if (!(compiler instanceof JavaCompilerType)) {
            return null;
        }
        return ((JavaCompilerType)compiler).getTargetFileSystem();
        
    }
}

