/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.browser

import com.apple.eawt.Application
import java.awt.Desktop
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy

object MacEventHandler {
    fun init() {
        try {
            Application.getApplication().apply {
                setAboutHandler { getActiveBrowserFrame()?.aboutAction?.invoke()}
            }
        } catch (e: LinkageError) {
            val desktop = Desktop.getDesktop()
            val aboutHandlerClass = Class.forName("java.awt.desktop.AboutHandler")
            val proxy = Proxy.newProxyInstance(aboutHandlerClass.classLoader, arrayOf(aboutHandlerClass), Java9MacAboutHandler)
            desktop::class.java.getMethod("setAboutHandler", aboutHandlerClass)?.invoke(desktop, proxy)
        }
    }
}

object Java9MacAboutHandler : InvocationHandler {
    override fun invoke(proxy: Any?, method: Method?, args: Array<Any?>?) {
        getActiveBrowserFrame()?.aboutAction?.invoke()
    }
}
