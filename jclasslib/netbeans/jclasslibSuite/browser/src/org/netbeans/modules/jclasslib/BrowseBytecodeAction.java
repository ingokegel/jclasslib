/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.netbeans.modules.jclasslib;

import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.Action;
import org.netbeans.api.java.classpath.ClassPath;
import org.netbeans.api.project.FileOwnerQuery;
import org.netbeans.api.queries.FileBuiltQuery;
import org.netbeans.spi.project.ActionProvider;
import org.netbeans.spi.project.ui.support.FileSensitiveActions;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.ContextAwareAction;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.CookieAction;
import org.openide.util.lookup.Lookups;

/**
 * Implement Browser Bytecode action which may be called on both <em>.java</em>
 * file and <em>.class</em> files. In the case it is invoked upon a .java file
 * it will check whether it is compiled or not and eventually try to carefully
 * compile.
 *
 * @author Martin Krauskopf
 */
public final class BrowseBytecodeAction extends CookieAction {
    
    protected void performAction(Node[] activatedNodes) {
        // may be called either on .class file or .java source file
        DataObject fileDO = (DataObject) activatedNodes[0].getCookie(DataObject.class);
        FileObject compiledClazz = fileDO.getPrimaryFile();
        if ("class".equals(compiledClazz.getExt())) { // NOI18N
            // called on .clazz file
            BytecodeBrowser.openFileObject(compiledClazz, null);
        } else if ("java".equals(compiledClazz.getExt())) { // NOI18N
            // called on source file
            final FileObject sourceFile = fileDO.getPrimaryFile();
            // XXX check whether the sourceFile is compilable before proceed
            final FileBuiltQuery.Status status = FileBuiltQuery.getStatus(sourceFile);
            if (status == null) {
                // cannot be used, try to find class file
                compiledClazz = getCompiledClazz(sourceFile);
                if (compiledClazz == null) {
                    showCannotFind();
                } else {
                    showMessage(".class file was found. But it is not possible " + // XXX I18N
                            "to check whether it is up-to-date."); // XXX I18N
                    BytecodeBrowser.openFileObject(compiledClazz, null);
                }
                return;
            }
            if (!status.isBuilt()) {
                tryToCompile(fileDO); // trigger compilation asynchronously
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        int i = 20000 / 100; // wait 20s
                        while (!status.isBuilt() && i-- != 0) {
                            try {
                                Thread.sleep(100); // Compiling
                            } catch (InterruptedException e) {
                                assert false : e;
                            }
                        }
                        final FileObject compiledClazz = getCompiledClazz(sourceFile);
                        if (compiledClazz == null) {
                            // Best to exit silently(?). Probably the user already
                            // knows what happened. Presumably compilation fails.
                            return;
                        }
                        EventQueue.invokeLater(new Runnable() {
                            public void run() {
                                BytecodeBrowser.openFileObject(compiledClazz, null);
                            }
                        });
                    }
                });
            } else { // already built
                compiledClazz = getCompiledClazz(sourceFile);
                if (compiledClazz == null) {
                    showCannotFind();
                } else {
                    BytecodeBrowser.openFileObject(compiledClazz, null);
                }
                
            }
        } else {
            showMessage("Must be invoked upon .class or .java file."); // XXX I18N
        }
    }
    
    private static FileObject getCompiledClazz(final FileObject sourceFile) {
        ClassPath sourceCP = ClassPath.getClassPath(sourceFile, ClassPath.SOURCE);
        assert sourceCP != null;
        ClassPath cp = ClassPath.getClassPath(sourceFile, ClassPath.EXECUTE);
        assert cp != null;
        return findResource(cp, sourceCP.getResourceName(sourceFile, '/', false) + ".class"); // NOI18N
    }
    
    /** #72573: Workarounding bug in ClassPath. */
    static FileObject findResource(
            final ClassPath cp, final String resource) {
        PropertyChangeListener pcl = new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                // #72573
            }
        };
        cp.addPropertyChangeListener(pcl);
        FileObject result = cp.findResource(resource); // NOI18N
        cp.removePropertyChangeListener(pcl);
        return result;
    }
    
    private void tryToCompile(final DataObject sourceFile) {
        ContextAwareAction caaCompile = (ContextAwareAction) FileSensitiveActions.fileCommandAction(
                ActionProvider.COMMAND_COMPILE_SINGLE, "Compile Single", null); // NOI18N
        Action compile = caaCompile.createContextAwareInstance(Lookups.fixed(new Object[] {
            FileOwnerQuery.getOwner(sourceFile.getPrimaryFile()),
            sourceFile
        }));
        compile.actionPerformed(null);
    }
    
    protected int mode() {
        return CookieAction.MODE_EXACTLY_ONE;
    }
    
    public String getName() {
        return NbBundle.getMessage(BrowseBytecodeAction.class, "CTL_BrowseBytecodeAction");
    }
    
    protected Class[] cookieClasses() {
        return new Class[] {
            DataObject.class // .class and .java
        };
    }
    
    protected String iconResource() {
        return "org/netbeans/modules/jclasslib/bytecode.gif"; // NOI18N
    }
    
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    private static void showMessage(String message) {
        NotifyDescriptor.Message nd = new NotifyDescriptor.Message(message);
        DialogDisplayer.getDefault().notify(nd);
    }
    
    private static void showCannotFind() {
        showMessage("Cannot find associated .class file. You may try to locate " + // XXX I18N
                "it manually (e.g.) in the Files view and invoke 'Browse " + // XXX I18N
                "Bytecode' directly from its context menu."); // XXX I18N
    }
}

