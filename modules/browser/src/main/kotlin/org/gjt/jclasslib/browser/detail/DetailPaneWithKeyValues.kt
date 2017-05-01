/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser.detail

import net.miginfocom.swing.MigLayout
import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.DetailPane
import org.gjt.jclasslib.structures.AttributeInfo
import javax.swing.tree.TreePath

abstract class DetailPaneWithKeyValues<T : AttributeInfo>(elementClass: Class<T>, services: BrowserServices) : DetailPane<T>(elementClass, services) {
    private val keyValueDetailPanel: KeyValueDetailPane<T>? = createKeyValueDetailPane()

    open fun createKeyValueDetailPane(): KeyValueDetailPane<T>? = null

    override fun setupComponent() {
        layout = MigLayout("insets 0, wrap", "[grow, fill]")
        if (keyValueDetailPanel != null) {
            keyValueDetailPanel.displayComponent // initializes detail pane
            add(keyValueDetailPanel, "wrap unrel") // Do not use scroll panel wrapper here
        }
    }

    override fun show(treePath: TreePath) {
        keyValueDetailPanel?.show(treePath)
    }
}