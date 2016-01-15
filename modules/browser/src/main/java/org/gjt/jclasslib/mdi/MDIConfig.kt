/*
    This library is free software; you can redistribute it and/or
    modify it under the terms of the GNU General Public
    License as published by the Free Software Foundation; either
    version 2 of the license, or (at your option) any later version.
*/

package org.gjt.jclasslib.mdi

import org.gjt.jclasslib.browser.config.window.WindowState

class MDIConfig {

    var internalFrameDescs: List<InternalFrameDesc> = listOf()
    var activeFrameDesc: InternalFrameDesc? = null

    class InternalFrameDesc {
        var initParam: WindowState? = null
        var x: Int = 0
        var y: Int = 0
        var width: Int = 0
        var height: Int = 0
        var isMaximized: Boolean = false
        var isIconified: Boolean = false
    }

}
