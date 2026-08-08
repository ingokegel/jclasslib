/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license or (at your option) any later version.
 */

package org.gjt.jclasslib.testutil

import kotlinx.dom.build.addElement
import kotlinx.dom.createDocument
import kotlinx.dom.parseXml
import kotlinx.dom.writeXmlString
import org.gjt.jclasslib.browser.FrameContent
import org.gjt.jclasslib.browser.config.BrowserConfig
import java.io.StringWriter

fun workspaceToXml(config: BrowserConfig, frameContent: FrameContent? = null): String {
    val writer = StringWriter()
    createDocument().addElement("workspace") {
        config.saveWorkspace(this)
        frameContent?.saveWorkspace(this)
    }.writeXmlString(writer, false)
    return writer.toString()
}

fun workspaceFromXml(xml: String, config: BrowserConfig, frameContent: FrameContent? = null) {
    val element = parseXml(xml.byteInputStream()).documentElement
    config.readWorkspace(element)
    frameContent?.readWorkspace(element)
}
