/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package util

import com.formdev.flatlaf.extras.FlatSVGIcon
import com.install4j.runtime.beans.LightOrDarkColor
import java.awt.Color

class CustomSvgIcon : FlatSVGIcon {
    constructor(name: String, width: Int, height: Int) : super(name, width, height)
    constructor(name: String) : super(name)
    constructor(icon: FlatSVGIcon) : super(icon)

    var materialIcon: Boolean = false

    fun materialIcon(): CustomSvgIcon {
        return changeMaterialColor(DEFAULT_MATERIAL_COLOR)
    }

    fun changeMaterialColor(color: Color): CustomSvgIcon {
        return changeMaterialColor(color, color)
    }

    fun changeMaterialColor(lightColor: Color, darkColor: Color): CustomSvgIcon {
        val copy = CustomSvgIcon(this)
        copy.colorFilter = ColorFilter().add(MATERIAL_FOREGROUND, lightColor, darkColor)
        copy.materialIcon = false
        return copy
    }

    fun changeMarkerColor(color: Color?): CustomSvgIcon {
        val copy = CustomSvgIcon(this)
        val colorFilter = copy.getColorFilter()
        if (colorFilter is ColorFilter) {
            colorFilter.add(MATERIAL_MARKER, color, color)
        } else {
            copy.colorFilter = ColorFilter().add(MATERIAL_MARKER, color, color)
        }
        return copy
    }

    companion object {
        val MATERIAL_FOREGROUND: Color = Color(31, 31, 31)
        val MATERIAL_MARKER: Color = Color(238, 0, 0)
        val DEFAULT_MATERIAL_COLOR: LightOrDarkColor = LightOrDarkColor(Color(80, 80, 80), Color(200, 200, 200))
    }
}