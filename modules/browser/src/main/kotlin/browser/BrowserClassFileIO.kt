/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.config.classpath.ClasspathJrtEntry
import org.gjt.jclasslib.io.ClassFileReader
import org.gjt.jclasslib.io.ClassFileWriter
import org.gjt.jclasslib.io.getJrtInputStream
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.util.AlertType
import org.gjt.jclasslib.util.GUIHelper
import java.awt.Window
import java.io.EOFException
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.file.FileSystems
import java.util.jar.JarFile
import kotlin.io.path.Path
import kotlin.io.path.copyTo
import kotlin.io.path.createTempFile
import kotlin.io.path.deleteIfExists

fun readClassFile(fileName: String, frame: BrowserFrame, suppressEOF: Boolean = false): ClassFile {
    try {
        return when {
            fileName.startsWith(ClasspathJrtEntry.JRT_PREFIX) -> {
                ClassFileReader.readFromInputStream(getJrtInputStream(fileName.removePrefix(ClasspathJrtEntry.JRT_PREFIX), File(frame.config.jreHome)), suppressEOF)
            }
            fileName.contains('!') -> {
                val (jarFileName, classFileName) = splitJarFileName(fileName)
                JarFile(jarFileName).use { jarFile ->
                    val jarEntry = jarFile.getJarEntry(classFileName)
                    if (jarEntry != null) {
                        ClassFileReader.readFromInputStream(jarFile.getInputStream(jarEntry), suppressEOF)
                    } else {
                        throw IOException("The jar entry $classFileName was not found")
                    }
                }
            }
            else -> {
                ClassFileReader.readFromFile(File(fileName), suppressEOF)
            }
        }
    } catch (ex: FileNotFoundException) {
        throw IOException("The file $fileName was not found")
    } catch (ex: EOFException) {
        if (GUIHelper.showOptionDialog(
                        frame,
                        getString("message.eof.title"),
                        getString("message.eof", fileName),
                        GUIHelper.YES_NO_OPTIONS,
                        AlertType.QUESTION
                ) == 0
        ) {
            return readClassFile(fileName, frame, suppressEOF = true)
        } else {
            ex.printStackTrace()
            throw IOException("An (expected) EOF occurred while reading $fileName")
        }
    } catch (ex: IOException) {
        ex.printStackTrace()
        throw IOException("An error occurred while reading $fileName")
    } catch (ex: Exception) {
        ex.printStackTrace()
        throw IOException("The file $fileName does not seem to contain a class file")
    }
}

fun writeClassFile(classFile: ClassFile, fileName: String, parentWindow: Window?, directoryChooser: () -> File?): Boolean =
    when {
        fileName.startsWith(ClasspathJrtEntry.JRT_PREFIX) -> {
            if (GUIHelper.showOptionDialog(
                            parentWindow,
                            getString("message.jrt.read.only.title"),
                            getString("message.jrt.read.only", fileName),
                            GUIHelper.YES_NO_OPTIONS,
                            AlertType.QUESTION
                    ) == 0
            ) {
                val directory = directoryChooser()
                if (directory != null) {
                    val alternativePath = File(directory, classFile.simpleClassName + ".class").path
                    writeClassFile(classFile, alternativePath, parentWindow, directoryChooser)
                } else {
                    false
                }
            } else {
                false
            }
        }
        fileName.startsWith(jarPrefix) -> {
            writeClassFile(classFile, fileName.substringAfter(jarPrefix), parentWindow, directoryChooser)
        }
        fileName.contains('!') -> {
            val tempOutputFile = createTempFile("jclasslib")
            try {
                ClassFileWriter.writeToFile(tempOutputFile, classFile)
                val (jarFileName, classFileName) = splitJarFileName(fileName)
                FileSystems.newFileSystem(Path(jarFileName), null).use { fs ->
                    tempOutputFile.copyTo(fs.getPath(classFileName), overwrite = true)
                }
            } finally {
                tempOutputFile.deleteIfExists()
            }
            true
        }
        else -> {
            ClassFileWriter.writeToFile(File(fileName), classFile)
            true
        }
    }

private fun splitJarFileName(fileName: String): Pair<String, String> = fileName.split("!", limit = 2).zipWithNext().first()

private const val jarPrefix = "jar://"