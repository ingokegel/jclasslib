/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.nbmodule;

import org.gjt.jclasslib.browser.*;
import org.gjt.jclasslib.browser.config.window.BrowserPath;
import org.gjt.jclasslib.io.ClassFileReader;
import org.gjt.jclasslib.structures.ClassFile;
import org.openide.*;
import org.openide.filesystems.*;
import org.openide.filesystems.FileSystem;
import org.openide.nodes.Node;
import org.openide.text.CloneableEditorSupport;
import org.openide.windows.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.io.*;
import java.util.HashMap;

/**
    Parent component for a class file browser in Netbeans.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.9 $ $Date: 2004-02-10 16:06:56 $
*/
public class ClassFileViewer extends TopComponent
                             implements BrowserServices
{

    private static HashMap fileObjectToClassFileViewer = new HashMap();

    private static final String VERSION = "1.2";

    private FileObject fo;
    private ClassFileNode node;
    private boolean initialized = false;
    private ClassFile classFile;

    private Action actionBackward;
    private Action actionForward;
    private Action actionReload;

    private BrowserComponent browserComponent;

    /**
        Retrieve an already opened <tt>ClassFileViewer</tt> or create
        a new one if necessary.
        @param fo the <tt>FileObject</tt> for which to create a
                  <tt>ClassFileViewer</tt>
        @return the <tt>ClassFileViewer</tt>
     */
    public static ClassFileViewer getCachedClassFileViewer(FileObject fo) {

        if (fileObjectToClassFileViewer.containsKey(fo)) {
            return (ClassFileViewer)fileObjectToClassFileViewer.get(fo);
        } else {
            ClassFileViewer viewer = new ClassFileViewer(fo);
            fileObjectToClassFileViewer.put(fo, viewer);
            return viewer;
        }
    }

    /**
     * Constructor.
     */
    public ClassFileViewer() {
        setCloseOperation(CLOSE_EACH);
    }

    private ClassFileViewer(FileObject fo) {

        this();
        this.fo = fo;
        node = new ClassFileNode(fo);
        setActivatedNodes(new Node[] {node});

    }

    public boolean canClose (Workspace workspace, boolean last) {

        fileObjectToClassFileViewer.remove(fo);
        return true;
    }

    public Image getIcon() {

        if (node != null) {
            return node.getIcon(BeanInfo.ICON_COLOR_16x16);
        } else {
            return null;
        }

    }

    public void open(Workspace ws) {

        init();
        if (ws == null) {
            ws = WindowManager.getDefault().getCurrentWorkspace();
        }
        Mode mode = ws.findMode(CloneableEditorSupport.EDITOR_MODE);
        if (mode != null) {
            mode.dockInto(this);
        }
        super.open(ws);
    }

    public void writeExternal (ObjectOutput out)
        throws IOException
    {
        if (out == null) {
            return;
        }
        if (node == null) {
            out.writeBoolean(false);
            return;
        }
        out.writeBoolean(true);
        out.writeUTF(VERSION);
        out.writeObject(node.getHandle());

        super.writeExternal(out);
    }

    public void readExternal (ObjectInput in)
        throws IOException, ClassNotFoundException
    {
        if (in == null) {
            return;
        }
        boolean valid = in.readBoolean();
        if (!valid) {
            return;
        }
        in.readUTF(); // version string
        Node.Handle handle = (Node.Handle)in.readObject();
        super.readExternal(in);

        node = (ClassFileNode)handle.getNode();
        fo = node.getFileObject();
        setActivatedNodes(new Node[] {node});
    }

    // Browser services

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

    public void openClassFile(String className, BrowserPath browserPath) {

        FileSystem targetFs = OpenAction.getTargetFileSystem(fo);
        String classFileName = className.replace('.', '/') + ".class";
        FileObject targetFo = targetFs.findResource(classFileName);
        if (targetFo == null) {
            targetFo = Repository.getDefault().findResource(classFileName);
        }
        if (targetFo != null) {
            OpenAction.openFileObject(targetFo, browserPath);
        } else {
            NotifyDescriptor desc = new NotifyDescriptor.Message("The class " + className + " could not be found.", NotifyDescriptor.INFORMATION_MESSAGE);
            TopManager.getDefault().notify(desc);
        }
    }

    public boolean canOpenClassFiles() {
        return true;
    }

    public void activate() {
        // not applicable
    }

    private synchronized void init () {

        if (initialized) {
            return;
        }

        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(new Runnable () {
                                            public void run () {
                                                init();
                                            }
                                       });
            return;
        }

        if (fo != null) {
            setName(fo.getName());
        }

        if (fo != null && !readClassFile()) {
            this.close();
            return;
        }

        setupActions();
        setupComponent();

        initialized = true;
    }

    private boolean readClassFile() {
        try {
            classFile =
                ClassFileReader.readFromInputStream(fo.getInputStream());
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(
                ErrorManager.EXCEPTION,
                ex);
            return false;
        }
        return true;
    }

    private void setupActions() {

        actionBackward = new DefaultAction("Backward", BrowserMDIFrame.loadIcon("browser_backward_small.png"));
        actionBackward.putValue(Action.SHORT_DESCRIPTION, "Move backward in the navigation history");
        actionBackward.setEnabled(false);

        actionForward = new DefaultAction("Forward", BrowserMDIFrame.loadIcon("browser_forward_small.png"));
        actionForward.putValue(Action.SHORT_DESCRIPTION, "Move forward in the navigation history");
        actionForward.setEnabled(false);

        actionReload = new DefaultAction("Reload", BrowserMDIFrame.loadIcon("reload_small.png"));
        actionReload.putValue(Action.SHORT_DESCRIPTION, "Reload class file");
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

    private class DefaultAction extends AbstractAction {

        private DefaultAction(String name, Icon icon) {
            super(name, icon);
        }

        public void actionPerformed(ActionEvent ev) {

            if (this == actionBackward) {
                browserComponent.getHistory().historyBackward();
            } else if (this == actionForward) {
                browserComponent.getHistory().historyForward();
            } else if (this == actionReload) {
                readClassFile();
                browserComponent.rebuild();
            }
        }
    }

}
