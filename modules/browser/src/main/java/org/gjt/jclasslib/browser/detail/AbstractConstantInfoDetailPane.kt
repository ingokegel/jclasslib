/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.browser.detail

import org.gjt.jclasslib.browser.BrowserServices
import org.gjt.jclasslib.structures.Constant
import java.awt.GridBagConstraints
import java.awt.Insets

abstract class AbstractConstantInfoDetailPane<T : Constant>(constantClass: Class<T>, services: BrowserServices) : TypedDetailPane<T>(constantClass, services) {
    protected fun addClassElementOpener() {
        if (services.canOpenClassFiles()) {
            val classElementOpener = ClassElementOpener(this)
            add(classElementOpener, gc() {
                weightx = 1.0
                anchor = GridBagConstraints.WEST
                insets = Insets(5, 10, 0, 10)
                gridx = 0
                gridwidth = 3
            })
            nextLine()
            showHandlers.add { constant ->
                classElementOpener.setConstant(constant)
            }
        }
    }
}


