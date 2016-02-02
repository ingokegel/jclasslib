/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail.constants

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.browser.detail.FixedListDetailPane
import org.gjt.jclasslib.structures.Constant

import javax.swing.tree.TreePath

abstract class AbstractConstantInfoDetailPane<T: Constant>(services: BrowserServices) : FixedListDetailPane(services) {
    protected fun getConstant(treePath: TreePath): T {
        return constantClass.cast(getElement(treePath))
    }

    abstract val constantClass: Class<T>
}


