/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.jclasslib.agent;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class Communicator implements CommunicatorMBean {
    private final Instrumentation instrumentation;
    private final ModuleResolver moduleResolver = createModuleResolver();

    public Communicator(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }

    @Override
    public List<ClassDescriptor> getClasses() {
        return Arrays.stream(instrumentation.getAllLoadedClasses())
                .filter(c -> !c.isArray())
                .map(c -> new ClassDescriptor(c.getName(), moduleResolver.getModuleName(c)))
                .collect(Collectors.toList());
    }

    @Override
    public byte[] getClassFile(String fileName) {
        String className = fileName.replace('/', '.');
        return findClass(className)
                .map(this::getClassFile)
                .orElse(null);
    }

    @Override
    public ReplacementResult replaceClassFile(String fileName, byte[] bytes) {
        String className = fileName.replace('/', '.');
        Class<?> c = findClass(className).orElse(null);
        if (c != null) {
            try {
                instrumentation.redefineClasses(new ClassDefinition(c, bytes));
                return ReplacementResult.SUCCESS;
            } catch (Throwable e) {
                return new ReplacementResult(e.getMessage());
            }
        }
        return new ReplacementResult("The class could not be found");
    }

    @SuppressWarnings("rawtypes")
    private Optional<Class> findClass(String className) {
        return Arrays.stream(instrumentation.getAllLoadedClasses())
                .filter(c -> c.getName().equals(className))
                .findFirst();
    }

    private byte[] getClassFile(Class<?> c) {
        ReadClassFileTransformer transformer = new ReadClassFileTransformer();
        try {
            instrumentation.addTransformer(transformer, true);
            instrumentation.retransformClasses(c);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        } finally {
            instrumentation.removeTransformer(transformer);
        }
        return transformer.bytes;
    }

    private ModuleResolver createModuleResolver() {
        try {
            if (!System.getProperty("java.version").startsWith("1.")) {
                return (ModuleResolver)Class.forName("org.jclasslib.agent.ModuleResolverImpl").getConstructor().newInstance();
            }
        } catch (Exception t) {
            t.printStackTrace();
        }
        return new NoModuleResolverImpl();
    }

    private static class ReadClassFileTransformer implements ClassFileTransformer {
        byte[] bytes;

        @Override
        public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
            if (classBeingRedefined != null) {
                this.bytes = classfileBuffer;
            }
            return null;
        }
    }

}
