/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/
package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.AnnotationData

class AnnotationDetailPane(services: BrowserServices) : TypedDetailPane<AnnotationData>(AnnotationData::class.java, services) {
    override fun addLabels() {
        addConstantPoolLink("Type:") { annotationData -> annotationData.typeIndex }
        addDetail("Number of entries:") { annotationData -> annotationData.elementValuePairEntries.size.toString() }
    }
}
