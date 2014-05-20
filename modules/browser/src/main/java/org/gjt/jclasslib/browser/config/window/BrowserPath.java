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
*/
public class BrowserPath {

    private LinkedList<PathComponent> pathComponents = new LinkedList<PathComponent>();

    /**
     * Get the list of browser path {@link PathComponent component}s.
     * @return the list.
     */
    public LinkedList<PathComponent> getPathComponents() {
        return pathComponents;
    }

    /**
     * Add a single browser path component to this browser path.
     * @param pathComponent the new component.
     */
    public void addPathComponent(PathComponent pathComponent) {
        pathComponents.add(pathComponent);
    }

}
