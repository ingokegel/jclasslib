/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
 */

package org.netbeans.modules.jclasslib;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JToolBar;
import org.gjt.jclasslib.browser.BrowserComponent;
import org.gjt.jclasslib.browser.BrowserMDIFrame;
import org.gjt.jclasslib.browser.BrowserServices;
import org.gjt.jclasslib.browser.BrowserTreeNode;
import org.gjt.jclasslib.browser.config.window.BrowserPath;
import org.gjt.jclasslib.browser.config.window.CategoryHolder;
import org.gjt.jclasslib.browser.config.window.PathComponent;
import org.gjt.jclasslib.io.ClassFileReader;
import org.gjt.jclasslib.structures.ClassFile;
import org.gjt.jclasslib.structures.InvalidByteCodeException;
import org.netbeans.api.java.classpath.ClassPath;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.HtmlBrowser;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.windows.TopComponent;

/**
 * TopComponent wrapper around the jclasslib {@link BrowserComponent}. Based on
 * the original implementaion by Ingo Kegel.
 *
 * @author Martin Krauskopf
 */
public class BytecodeBrowser extends TopComponent implements BrowserServices {
    
    private static final BrowserPath DEFAULT_PATH;
    
    static {
        DEFAULT_PATH = new BrowserPath();
        DEFAULT_PATH.addPathComponent(new CategoryHolder(BrowserTreeNode.NODE_GENERAL));
    }
    
    private static Map/*<FileObject, TopComponent>*/ browsers = new HashMap/*<FileObject, TopComponent>*/();
    
    private FileObject classFO;
    private FileChangeAdapter classFCL;
    private ClassFile classFile;
    
    private Action actionBackward;
    private Action actionForward;
    private Action actionReload;
    
    private BrowserComponent browserComponent;
    
    static void openFileObject(FileObject clazz, final BrowserPath browserPath) {
        final BytecodeBrowser browser = BytecodeBrowser.getBytecodeBrowser(clazz);
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                // When opening first time and browser path is not set, set
                // it do default. Otherwise set new or leave it as it is (null does nothing)
                BrowserPath path = browser.isOpened() || browserPath != null
                        ? browserPath : DEFAULT_PATH;
                if (!browser.isOpened()) {
                    browser.open();
                }
                browser.requestActive();
                setBrowserPathSafely(browser, path);
            }
        });
    }
    
    public static BytecodeBrowser getBytecodeBrowser(final FileObject classFO) {
        assert classFO != null;
        BytecodeBrowser browser = (BytecodeBrowser) browsers.get(classFO);
        if (browser == null) {
            browser = new BytecodeBrowser(classFO);
            browsers.put(classFO, browser);
        }
        return browser;
    }
    
    private BytecodeBrowser(final FileObject classFO) {
        super(Lookups.singleton(classFO));
        this.classFO = classFO;
    }
    
    private void reload() {
        readClassFile();
        browserComponent.rebuild();
    }
    
    protected void componentOpened() {
        setName(classFO.getName());
        setIcon(Utilities.loadImage("org/netbeans/modules/jclasslib/bytecode.gif"));
        if (!readClassFile()) {
            this.close();
            return;
        }
        
        setupActions();
        setupComponent();
        
        classFCL = new FileChangeAdapter() {
            public void fileChanged(FileEvent fe) {
                BytecodeBrowser.this.reload();
            }
            public void fileDeleted(FileEvent fe) {
                removeClassFileChangeListener();
                EventQueue.invokeLater(new Runnable() {
                    public void run() {
                        BytecodeBrowser.this.close();
                    }
                });
            }
        };
        classFO.addFileChangeListener(classFCL);
    }
    
    protected void componentClosed() {
        removeClassFileChangeListener();
        browsers.remove(classFO);
    }
    
    private synchronized void removeClassFileChangeListener() {
        if (classFCL != null) {
            classFO.removeFileChangeListener(classFCL);
            classFCL = null;
        }
    }
    
    public int getPersistenceType() {
        return TopComponent.PERSISTENCE_NEVER;
    }
    
    /* Browser services */
    
    public ClassFile getClassFile() {
        return classFile;
    }
    
    public BrowserComponent getBrowserComponent() {
        return browserComponent;
    }
    
    public Action getActionBackward() {
        return actionBackward;
    }
    
    public Action getActionForward() {
        return actionForward;
    }
    
    public void openClassFile(String className, final BrowserPath browserPath) {
        String classFileName = className.replace('.', '/') + ".class"; // NOI18N
        ClassPath cp = ClassPath.getClassPath(classFO, ClassPath.EXECUTE);
        assert cp != null;
        FileObject compiledClazz = BrowseBytecodeAction.findResource(cp, classFileName);
        if (compiledClazz == null) {
            ClassPath bootCP = ClassPath.getClassPath(classFO, ClassPath.BOOT);
            assert bootCP != null;
            compiledClazz = BrowseBytecodeAction.findResource(bootCP, classFileName);
        }
        if (compiledClazz == null) {
            String message = "The class " + className + " could not be found."; // XXX I18N
            NotifyDescriptor.Message nd = new NotifyDescriptor.Message(message);
            DialogDisplayer.getDefault().notify(nd);
        } else {
            BytecodeBrowser.openFileObject(compiledClazz, browserPath);
        }
    }
    
    public boolean canOpenClassFiles() {
        return true;
    }
    
    public void activate() {
        // not applicable
    }
    
    private boolean readClassFile() {
        boolean readed = false;
        try {
            InputStream is = classFO.getInputStream();
            classFile = ClassFileReader.readFromInputStream(is);
            readed = true;
        } catch (InvalidByteCodeException e) {
            ErrorManager.getDefault().annotate(e, "Class file " + FileUtil.getFileDisplayName(classFO) + " seems to be invalid."); // XXX I18N
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, e);
        } catch (IOException ex) {
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, ex);
        }
        return readed;
    }
    
    private void setupActions() {
        actionBackward = new DefaultAction("Backward", BrowserMDIFrame.loadIcon("browser_backward_small.png")); // XXX I18N
        actionBackward.putValue(Action.SHORT_DESCRIPTION, "Move backward in the navigation history"); // XXX I18N
        actionBackward.setEnabled(false);
        
        actionForward = new DefaultAction("Forward", BrowserMDIFrame.loadIcon("browser_forward_small.png")); // XXX I18N
        actionForward.putValue(Action.SHORT_DESCRIPTION, "Move forward in the navigation history"); // XXX I18N
        actionForward.setEnabled(false);
        
        actionReload = new DefaultAction("Reload", BrowserMDIFrame.loadIcon("reload_small.png")); // XXX I18N
        actionReload.putValue(Action.SHORT_DESCRIPTION, "Reload class file"); // XXX I18N
        actionReload.setEnabled(true);
    }
    
    private void setupComponent() {
        setLayout(new BorderLayout());
        browserComponent = new BrowserComponent(this);
        add(buildToolbar(), BorderLayout.NORTH);
        add(browserComponent, BorderLayout.CENTER);
    }
    
    private JToolBar buildToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.add(actionBackward);
        toolBar.add(actionForward);
        toolBar.addSeparator();
        toolBar.add(actionReload);
        toolBar.setFloatable(false);
        return toolBar;
    }
    
    /**
     * Since composing of {@link BrowserPath} is not safe yet, let's use this
     * temporary workaround to not bother users too much in case the browser
     * path is corrupted.
     */
    private static void setBrowserPathSafely(
            final BytecodeBrowser browser, final BrowserPath browserPath) {
        try {
            browser.getBrowserComponent().setBrowserPath(browserPath);
        } catch (Exception e) {
            ErrorManager.getDefault().log(ErrorManager.WARNING, "Cannot set browser path automatically.");
            if (browserPath != null) {
                ErrorManager.getDefault().log(ErrorManager.WARNING, " browserPath: ");
                for (Iterator it = browserPath.getPathComponents().iterator(); it.hasNext();) {
                    PathComponent pc = (PathComponent) it.next();
                    ErrorManager.getDefault().log(ErrorManager.WARNING, "  path component: \"" + pc + "\"");
                }
            }
        }
    }
    
    public void showURL(final String urlSpec) {
        try {
            URL url = new URL(urlSpec);
            HtmlBrowser.URLDisplayer.getDefault().showURL(url);
        } catch (MalformedURLException e) {
            ErrorManager.getDefault().notify(ErrorManager.WARNING, e);
        }
    }
    
    private class DefaultAction extends AbstractAction {
        
        DefaultAction(String name, Icon icon) {
            super(name, icon);
        }
        
        public void actionPerformed(ActionEvent ev) {
            if (this == actionBackward) {
                browserComponent.getHistory().historyBackward();
            } else if (this == actionForward) {
                browserComponent.getHistory().historyForward();
            } else if (this == actionReload) {
                BytecodeBrowser.this.reload();
            } else {
                assert false : "Unknown event: " + ev;
            }
        }
        
    }
    
}
