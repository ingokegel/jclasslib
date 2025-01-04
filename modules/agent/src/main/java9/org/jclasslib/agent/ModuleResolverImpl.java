/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.jclasslib.agent;

@SuppressWarnings("unused")
public class ModuleResolverImpl implements ModuleResolver {
    @Override
    public String getModuleName(Class<?> c) {
        Module module = c.getModule();
        return module.isNamed() ? module.getName() : null;
    }
}
