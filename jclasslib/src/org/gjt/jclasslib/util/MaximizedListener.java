/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.util;

/**
    Listens for (internal) frames to be maximized. There is no standard
    listener for <tt>JInternalFrame</tt>s which transmits this information.
 
    @author <a href="mailto:jclasslib@gmx.net">Ingo Kegel</a>
    @version $Revision: 1.1.1.1 $ $Date: 2001-05-14 16:49:20 $
*/
public interface MaximizedListener {

    public void frameMaximized(MaximizedEvent event);
}
