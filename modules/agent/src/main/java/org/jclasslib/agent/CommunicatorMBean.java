/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.jclasslib.agent;

import java.util.List;

public interface CommunicatorMBean {
    List<ClassDescriptor> getClasses();
    byte[] getClassFile(String fileName);
    ReplacementResult replaceClassFile(String fileName, byte[] bytes);
}
