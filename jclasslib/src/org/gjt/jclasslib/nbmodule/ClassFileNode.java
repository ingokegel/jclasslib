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
    @version $Revision: 1.3 $ $Date: 2003-07-08 14:04:28 $
*/
public class ClassFileNode extends AbstractNode {

    private FileObject fo;

    public ClassFileNode(FileObject fo) {
        super(Children.LEAF);
        this.fo = fo;
        setIconBase("/images/nbmodule");
        setName(fo.getName());
    }
    
    public FileObject getFileObject() {
        return fo;
    }
    
    public Node.Handle getHandle() {
        return new ClassFileNodeHandle(this);
    }

    /** Node handle for a <tt>ClassFileNode</tt>. */
    public static class ClassFileNodeHandle implements Node.Handle {
        private FileObject fo;
        
        public ClassFileNodeHandle(ClassFileNode node) {
            fo = node.fo;
        }
        
        public Node getNode() {
            return new ClassFileNode(fo);
        }
    }

}
