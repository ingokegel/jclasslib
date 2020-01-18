/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package org.gjt.jclasslib.util

import util.LightOrDarkColor
import java.awt.Color
import javax.swing.Icon

enum class TreeIcon {CLOSED, OPEN, LEAF}

var treeRowHeight : Int = 0
var tableRowHeight : Int = 0
var documentFontFamily : String? = null
var documentFontSize : Int = 12

var treeIcons: Map<TreeIcon, Icon> = emptyMap()

enum class ColorKey {LINK, ACTIVE_LINK, VALUE}

var colors: Map<ColorKey, Color> = emptyMap()
var darkMode = false

fun getLinkColor() = colors[ColorKey.LINK] ?: LightOrDarkColor(Color(0, 128, 0), Color(60, 140, 60))
fun getActiveLinkColor() = colors[ColorKey.ACTIVE_LINK] ?: LightOrDarkColor(Color(196, 0, 0), Color(255, 80, 80))
fun getValueColor() = colors[ColorKey.VALUE] ?: LightOrDarkColor(Color(128, 0, 0), Color(190, 80, 80))