/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.window;

/**
    Browser path component for a node category.

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.1 $ $Date: 2003-08-18 08:10:15 $
*/
public class CategoryHolder implements PathComponent {

    private String category;

    /**
     * Constructor.
     * @param category the category.
     */
    public CategoryHolder(String category) {
        this.category = category;
    }

    /**
     * Constructor.
     */
    public CategoryHolder() {
    }

    /**
     * Get the category.
     * @return the category.
     */
    public String getCategory() {
        return category;
    }

    /**
     * Set the category.
     * @param category the category.
     */
    public void setCategory(String category) {
        this.category = category;
    }
}
