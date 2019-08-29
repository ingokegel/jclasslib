/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.structures

/**
 * Base class for all stateful constant pool entries in the constants package.
 * @property classFile The class file of which this structure is part of
 */
abstract class AbstractConstant(protected val classFile: ClassFile) : Constant()