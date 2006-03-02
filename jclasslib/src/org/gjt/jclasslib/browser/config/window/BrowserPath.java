/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.window;

import java.util.LinkedList;

/**
    Description of the selected path in the tree of a <tt>BrowserComponent</tt>.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2006-03-02 19:23:10 $
*/
public class BrowserPath {

    private LinkedList pathComponents = new LinkedList();

    /**
     * Get the list of browser path {@link PathComponent component}s.
     * @return the list.
     */
    public LinkedList getPathComponents() {
        return pathComponents;
    }

    /**
     * Set the list of browser path components.
     * @param pathComponents the list.
     */
    public void setPathComponents(LinkedList pathComponents) {
        this.pathComponents = pathComponents;
    }

    /**
     * Add a single browser path component to this browser path.
     * @param pathComponent the new component.
     */
    public void addPathComponent(PathComponent pathComponent) {
        pathComponents.add(pathComponent);
    }

}
