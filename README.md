# jclasslib bytecode viewer

## Purpose

jclasslib bytecode viewer is a tool that visualizes all aspects of compiled Java class files and the contained bytecode. In addition, it contains a library that enables developers to read and write Java class files and bytecode.

## License

jclasslib bytecode viewer is released under the [GPL, Version 2.0](https://www.gnu.org/licenses/gpl-2.0.html).


## Download

Installers can be downloaded from [bintray](https://bintray.com/ingokegel/generic/jclasslib/_latestVersion).

## Changes in 4.1

**Features**

New attributes: StackMapTable, BootstrapMethods, MethodParameters, RuntimeParameterAnnotations

**Bugs**

Writing was broken for certain attributes containing annotations

## Changes in 4.0

**Features**

* Support for Java 8 (class files compiled with -target 1.8)
* Show major class file version as verbose text in the "General information"
* Added display for CONSTANT_MethodHandle_info and CONSTANT_MethodType_info constant pool entries
* File extension handling for .class and .jcw files
* Dragging .class and .jcw files into the main window will open them
* Code base was changed to use Java language features up to Java 6. Opcodes and access flags are now implemented as 
enums instead of interfaces. This has caused some incompatible changes to the API. 

**Bugs**

* The iinc instruction was read incorrectly for negative arguments
* The CONSTANT_InvokeDynamic_info constant pool entry was missing

## Changes in 3.1

**Features**

* Support for Java 7 (class files compiled with -target 1.7)
* Windows 7 compatible installers and launchers
* The detail panels for "Fields" and "Methods" now have "Copy to clipboard" buttons to copy all signatures to the system clipboard
* At the bottom of the bytecode display there is now a drop-down list with all used opcodes and a button to show the corresponding official documentation in the Java language specification
* Offsets are now verified and invalid branch instructions are tagged in the bytecode display


**Bugs**

* Wide branch instructions were not displayed correctly in the bytecode
* Negative values of bipush instruction were printed as 256-value

## Changes in 3.0

This release brings full compatibility with Java 1.5

* Displays new access flags in Java 1.5:
    * ACC_ENUM
    * ACC_ANNOTATION
    * ACC_BRIDGE
    * ACC_VARARGS
* Displays new attributes in Java 1.5:
    * RuntimeVisibleAnnotations
    * RuntimeInvisibleAnnotations
    * RuntimeVisibleParameterAnnotations
    * RuntimeInvisibleParameterAnnotations
    * EnclosingMethod
    * Signature
    * LocalVariableTypeTable
    * Various bug fixes in the GUI

