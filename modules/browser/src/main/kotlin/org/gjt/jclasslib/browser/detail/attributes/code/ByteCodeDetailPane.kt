/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.attributes.code

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.attributes.CodeAttributeDetailPane
import org.gjt.jclasslib.browser.detail.attributes.code.ByteCodeDocument.OffsetLink
import org.gjt.jclasslib.browser.detail.attributes.code.ByteCodeDocument.SpecLink
import org.gjt.jclasslib.browser.detail.attributes.document.AttributeDocument
import org.gjt.jclasslib.browser.detail.attributes.document.DocumentDetailPane
import org.gjt.jclasslib.structures.ClassFile
import org.gjt.jclasslib.structures.attributes.CodeAttribute
import javax.swing.text.StyleContext

class ByteCodeDetailPane(services: BrowserServices, private val codeAttributeDetailPane: CodeAttributeDetailPane) : DocumentDetailPane<CodeAttribute, ByteCodeDocument>(CodeAttribute::class.java, ByteCodeDocument::class.java, services) {

    init {
        name = "Bytecode"
    }

    override fun createDocument(styles: StyleContext, attribute: CodeAttribute, classFile: ClassFile): ByteCodeDocument =
            ByteCodeDocument(styles, attribute, services.classFile)

    override fun offsetToPosition(offset: Int) = attributeDocument.getPosition(offset)

    override fun linkTriggered(link: AttributeDocument.Link) {
        super.linkTriggered(link)
        when (link) {
            is OffsetLink -> {
                scrollToOffset(link.targetOffset)
                updateHistory(link.targetOffset)
            }
            is SpecLink -> {
                services.showURL(link.opcode.docUrl)
            }
        }
    }

    override fun makeVisible() {
        super.makeVisible()
        codeAttributeDetailPane.selectByteCodeDetailPane()
    }
}
