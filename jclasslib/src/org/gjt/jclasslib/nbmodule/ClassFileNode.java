/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.nbmodule;

import org.openide.nodes.*;
import org.openide.filesystems.*;

/**
    Node for a <tt>ClassFileViewer</tt> component.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2002-02-27 16:47:43 $
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
