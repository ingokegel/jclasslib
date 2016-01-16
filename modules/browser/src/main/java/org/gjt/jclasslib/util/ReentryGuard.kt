/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

class ReentryGuard {
    var inProgress = false
        private set

    fun execute(function : () -> Unit) {
        if (inProgress) {
            return
        }
        inProgress = true
        try {
            function()
        } finally {
            inProgress = false
        }
    }
}