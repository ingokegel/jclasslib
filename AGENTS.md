# AGENTS.md

This file provides guidance to AI coding assistants when working with code in this repository.

## Project Overview

jclasslib is a JVM class file viewer and editor. It includes a library for reading and writing JVM class files.

### Architecture

The project is organized as a multi-module Gradle build with the following structure:

- **modules/agent/**: Supports attaching to running JVM processes to read and write loaded class files. Subdivided into main and java9, java9 only contains helper code for supporting Java9+ JVMs. 
- **modules/data**: Contains the library for reading and writing JVM class files. This is a Kotlin Multi-Platform module. Most classes are in `commonMain`. `jvmMain` contains code for reading and writing class files.
- **modules/browser/**: Contains the UI for the JVM class file browser
- **modules/idea/**: Contains the plugin for IntelliJ IDEA
- **modules/installer/**: Contains resources for building installers for jclasslib

The root package for all source files is `org.gjt.jclasslib`. The entire project is written in Kotlin.
The directory hierarchy of class files omits the `org.gjt.jclasslib` prefix in all modules. 
Gradle build directories are located in `build/gradle/<module>`.

Key architectural components:
- `org.gjt.jclasslib.structures.ClassFile` in `commonMain` pf `modules/data`: Class representing a JVM class file.
- `org.gjt.jclasslib.io.Jimage` in `jvmMain` of `modules/data`: Functions for reading JRE class files from the JRT for Java9+
- `org.gjt.jclasslib.browser.BrowserApplication` in `modules/browser` : Main class for the class file browser

The class file browser consists of a tree on the left side showing the structure of a `ClassFile` object. Details for the 
selected node are shown in the detail panel on the right side. The UI for these panels is contained in the
`org.gjt.jclasslib.browser.detail.**` packages.

The currently configured class path and the open class files can be saved to a workspace. The code is located in the
`org.gjt.jclasslib.browser.config.**` packages.

## Build Commands

### Prerequisites
- Java 21+

### Essential Commands
```bash

# Test data module
./gradlew :data:jvmTest

# Build distribution
./gradlew dist

# Run a specific test class
./gradlew :data:jvmTest --tests "*ClassFileConsistencyTest"

# Run a specific test class with an additional JVM argument <ARG> for profiling
./gradlew :data:jvmTest --tests "*ClassFileConsistencyTest" -PprofilingJvmArg=<ARG>
```
