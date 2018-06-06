/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import java.awt.Color
import javax.swing.Icon

enum class TreeIcon {CLOSED, OPEN, LEAF}

var treeRowHeight : Int = 0

var treeIcons: Map<TreeIcon, Icon> = emptyMap()

enum class ColorKey {LINK, ACTIVE_LINK, VALUE}

var colors: Map<ColorKey, Color> = emptyMap()

fun getLinkColor() = colors[ColorKey.LINK] ?: Color(0, 128, 0)
fun getActiveLinkColor() = colors[ColorKey.ACTIVE_LINK] ?: Color(196, 0, 0)
fun getValueColor() = colors[ColorKey.VALUE] ?: Color(128, 0, 0)