/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.idea

import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.service

@Service
class ByteCodePluginService : Disposable {
    override fun dispose() {
    }

    companion object {
        val instance: ByteCodePluginService
            get() = requireNotNull(ApplicationManager.getApplication().service())
    }
}