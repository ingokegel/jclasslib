/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.nbmodule;

import org.openide.filesystems.FileObject;
import org.openide.nodes.*;

/**
    Node for a <tt>ClassFileViewer</tt> component.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.4 $ $Date: 2003-08-18 07:57:52 $
*/
public class ClassFileNode extends AbstractNode {

    private FileObject fo;

    /**
        Constructor.
        @param fo the file object.
     */
    public ClassFileNode(FileObject fo) {
        super(Children.LEAF);
        this.fo = fo;
        setIconBase("/org/gjt/jclasslib/nbmodule/nbmodule");
        setName(fo.getName());
    }

    /**
     * Get the associated file object.
     * @return the file object.
     */
    public FileObject getFileObject() {
        return fo;
    }

    /**
     * Get the associated node handle.
     * @return the node handle.
     */
    public Node.Handle getHandle() {
        return new ClassFileNodeHandle(this);
    }

    /** Node handle for a <tt>ClassFileNode</tt>. */
    public static class ClassFileNodeHandle implements Node.Handle {
        private FileObject fo;

        /**
            Constructor.
            @param node the class file node.
         */
        public ClassFileNodeHandle(ClassFileNode node) {
            fo = node.fo;
        }
        
        public Node getNode() {
            return new ClassFileNode(fo);
        }
    }

}
