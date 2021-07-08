/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.jclasslib.agent;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;

public class AgentMain {
    public static final String MBEAN_NAME = "org.jclasslib:name=agent";

    public static void premain(String args, Instrumentation instrumentation) throws Exception {
        init(instrumentation);
    }

    public static void agentmain(String args, Instrumentation instrumentation) throws Exception {
        init(instrumentation);
    }

    private static void init(Instrumentation instrumentation) throws Exception {
        MBeanServer server = ManagementFactory.getPlatformMBeanServer();
        ObjectName objectName = new ObjectName(MBEAN_NAME);
        if (!server.isRegistered(objectName)) {
            server.registerMBean(new Communicator(instrumentation), objectName);
        }
    }
}
