/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.jclasslib.agent;

import java.io.Serializable;

public class ClassDescriptor implements Serializable {
    private final String className;
    private final String moduleName;

    public ClassDescriptor(String className, String moduleName) {
        this.className = className;
        this.moduleName = moduleName;
    }

    public String getClassName() {
        return className;
    }

    public String getModuleName() {
        return moduleName;
    }
}
