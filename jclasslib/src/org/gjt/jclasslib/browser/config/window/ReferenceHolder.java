/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.config.window;

/**
    Browser path component for named class member (field or method).

    @author <a href="mailto:jclasslib@ej-technologies.com">Ingo Kegel</a>
    @version $Revision: 1.2 $ $Date: 2006-03-02 19:23:10 $
*/
public class ReferenceHolder implements PathComponent {

    private String name;
    private String type;

    /**
     * Constructor.
     * @param name the name of the reference.
     * @param type the type of the reference.
     */
    public ReferenceHolder(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Constructor.
     */
    public ReferenceHolder() {
    }

    /**
     * Get the name of the reference.
     * @return the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the reference.
     * @param name the name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the type of the reference.
     * @return the type.
     */
    public String getType() {
        return type;
    }

    /**
     * Set the type of the reference.
     * @param type the type.
     */
    public void setType(String type) {
        this.type = type;
    }

    public String toString() {
        return "ReferenceHolder[name=" + getName() + ";type=" + getType() + "]";
    }

}
