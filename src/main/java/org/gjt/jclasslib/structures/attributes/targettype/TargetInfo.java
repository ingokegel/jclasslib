/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes.targettype;

import org.gjt.jclasslib.structures.AbstractStructure;

/**
 * Base class for target infos in a <tt>TypeAnnotation</tt> structure.
 */
public abstract class TargetInfo extends AbstractStructure {

    protected TargetInfo() {
    }

    public abstract int getLength();
    public abstract String getVerbose();

    @Override
    protected String printAccessFlagsVerbose(int accessFlags) {
        return "";
    }
}
