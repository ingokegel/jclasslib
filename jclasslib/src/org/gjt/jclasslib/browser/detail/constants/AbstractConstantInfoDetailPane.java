/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants;

import org.gjt.jclasslib.browser.*;
import org.gjt.jclasslib.browser.detail.*;

import javax.swing.tree.*;

/**
    Base class for all constant pool entry detail panes.
 
    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.3 $ $Date: 2002-02-27 16:47:42 $
*/
public abstract class AbstractConstantInfoDetailPane extends FixedListDetailPane {

    /** Message which is diplayed if the constant pool entry is invalid */
    protected static final String MESSAGE_INVALID_CONSTANT_POOL_ENTRY = "invalid constant pool entry";

    public AbstractConstantInfoDetailPane(BrowserServices services) {
        super(services);
    }
    
    /**
        Get the constant pool index corrensponding to a selection in 
        <tt>BrowserTreePane</tt>.
        @param treePath the tree path
        @return the index
     */
    protected int constantPoolIndex(TreePath treePath) {
        return getIndex(treePath);
    }
    
}


