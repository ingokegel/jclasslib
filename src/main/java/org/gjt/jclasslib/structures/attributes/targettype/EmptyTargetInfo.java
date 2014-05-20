/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures.attributes.targettype;

/**
 * Target info for a <tt>TypeAnnotation</tt> structure with empty content.
 */
public class EmptyTargetInfo extends TargetInfo {

    @Override
    public int getLength() {
        return 0;
    }

    @Override
    public String getVerbose() {
        return "<none>";
    }
}
