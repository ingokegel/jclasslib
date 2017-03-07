# Module data

This module contains the code that can reads and write Java bytecode.

# Package org.gjt.jclasslib.bytecode

This package contains classes which are wrappers for opcode instructions contained
in the `code` field of a `Code` attribute structure. These classes are not
generated when reading the [org.gjt.jclasslib.structures.ClassFile] structure via
the [org.gjt.jclasslib.io.ClassFileReader], they have to be created through the
[org.gjt.jclasslib.io.ByteCodeReader] and converted back to bytecode with the
[org.gjt.jclasslib.io.ByteCodeWriter].


# Package org.gjt.jclasslib.io

This package contains classes which convert between the class file format and
the representations defined in the packages [org.gjt.jclasslib.structures]
and [org.gjt.jclasslib.bytecode].


# Package org.gjt.jclasslib.structures

This package contains classes mirroring the data structures of the class file format.

Most data structures mirror those defined in the 
[Java virtual machine specification](http://java.sun.com/docs/books/vmspec/).
Attributes have been grouped in the [org.gjt.jclasslib.structures.attributes] subpackage, 
constant pool entries are found in the [org.gjt.jclasslib.structures.constants] subpackage.


# Package org.gjt.jclasslib.structures.attributes

This package contains classes for attribute data structures which extend 
[org.gjt.jclasslib.structures.AttributeInfo] and their substructures.

 
# Package org.gjt.jclasslib.structures.attributes.targettype

This package contains classes that represent target infos for type annotations.

 
# Package org.gjt.jclasslib.structures.constants
 
This package contains classes for constant pool data structures which extend 
[org.gjt.jclasslib.structures.Constant].


# Package org.gjt.jclasslib.structures.elementvalues

This package contains classes for constant pool data structures which describe element values.
