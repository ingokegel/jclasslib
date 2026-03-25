/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.usages

import org.gjt.jclasslib.browser.BrowserBundle.getString
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.GlobalBrowserServices
import org.gjt.jclasslib.io.ClassFileReadMode
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.attributes.RuntimeInvisibleAnnotationsAttribute
import org.gjt.jclasslib.structures.attributes.RuntimeVisibleAnnotationsAttribute
import org.gjt.jclasslib.structures.constants.ConstantClassInfo
import org.gjt.jclasslib.util.AlertType
import org.gjt.jclasslib.util.ProgressDialog
import org.gjt.jclasslib.util.alertFacade
import org.gjt.jclasslib.util.getParentWindow
import java.awt.Window

fun findImplementingClasses(className: String, services: BrowserServices) {
    val parentWindow = services.browserComponent.getParentWindow()
    val includeJdk = isJdkClassName(className)
    val matchingClasses = findMatchingClassFiles(services, includeJdk, parentWindow, ClassFileReadMode.SKIP_ATTRIBUTES, getString("searching.implementations")) { classFile ->
        (classFile.superClass > 0 && classFile.superClassName == className) ||
                classFile.interfaces.any { idx ->
                    classFile.getConstantPoolEntry(idx, ConstantClassInfo::class).name == className
                }
    }
    if (matchingClasses.isNotEmpty()) {
        val items = matchingClasses.map { MatchingClassName(it) }
        val selected = showClassListDialog(items, parentWindow, getString("found.implementing.classes.title"), getString("multiple.implementing.classes.info"))
        for (item in selected) {
            services.openClassFile(item.className, null)
        }
    } else {
        alertFacade.showMessage(parentWindow, getString("no.implementations.found"), AlertType.INFORMATION)
    }
}

fun findAnnotatedElements(annotationClassName: String, services: BrowserServices) {
    val descriptor = "L$annotationClassName;"
    val includeJdk = isJdkClassName(annotationClassName)
    val parentWindow = services.browserComponent.getParentWindow()
    val matchingClasses = findMatchingClassFiles(services, includeJdk, parentWindow, ClassFileReadMode.FULL, getString("searching.annotated.elements")) { classFile ->
        hasAnnotation(classFile, classFile, descriptor)
    }
    if (matchingClasses.isNotEmpty()) {
        val items = matchingClasses.map { MatchingClassName(it) }
        val selected = showClassListDialog(items, parentWindow, getString("found.annotated.elements.title"), getString("multiple.implementing.classes.info"))
        for (item in selected) {
            services.openClassFile(item.className, null)
        }
    } else {
        alertFacade.showMessage(parentWindow, getString("no.annotated.elements.found"), AlertType.INFORMATION)
    }
}

private class MatchingClassName(val className: String) {
    override fun toString() = className.replace('/', '.')
}

private fun findMatchingClassFiles(
    services: GlobalBrowserServices,
    includeJdk: Boolean,
    parentWindow: Window?,
    readMode: ClassFileReadMode,
    progressMessage: String,
    predicate: (ClassFile) -> Boolean
): List<String> {
    val classNames = mutableListOf<String>()
    ProgressDialog(parentWindow, progressMessage) {
        services.scanClassFiles(includeJdk, readMode = readMode) { classFile, _ ->
            if (predicate(classFile)) {
                classNames.add(classFile.thisClassName)
            }
        }
    }.apply {
        isVisible = true
    }
    return classNames
}

private fun hasAnnotation(classFile: ClassFile, container: org.gjt.jclasslib.structures.AttributeContainer, descriptor: String): Boolean {
    val visibleAnns = container.findAttribute(RuntimeVisibleAnnotationsAttribute::class)
    val invisibleAnns = container.findAttribute(RuntimeInvisibleAnnotationsAttribute::class)
    return listOfNotNull(visibleAnns, invisibleAnns).any { attr ->
        attr.runtimeAnnotations.any { annotation ->
            classFile.getConstantPoolUtf8Entry(annotation.typeIndex).string == descriptor
        }
    }
}
