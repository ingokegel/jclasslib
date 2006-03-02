/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.window;

/**
    Browser path component for an index value.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2006-03-02 19:23:10 $
*/
public class IndexHolder implements PathComponent {

    private int index = -1;

    /**
     * Constructor.
     * @param index the index.
     */
    public IndexHolder(int index) {
        this.index = index;
    }

    /**
     * Constructor.
     */
    public IndexHolder() {
    }

    /**
     * Get the index.
     * @return the index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set the index.
     * @param index the index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    public String toString() {
        return "IndexHolder[" + getIndex() + "]";
    }

}
