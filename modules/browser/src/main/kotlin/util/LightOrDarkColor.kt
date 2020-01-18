/*
 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public
 License as published by the Free Software Foundation; either
 version 2 of the license, or (at your option) any later version.
 */

package util

import com.formdev.flatlaf.FlatLaf
import java.awt.Color
import java.awt.PaintContext
import java.awt.Rectangle
import java.awt.RenderingHints
import java.awt.color.ColorSpace
import java.awt.geom.AffineTransform
import java.awt.geom.Rectangle2D
import java.awt.image.ColorModel
import javax.swing.UIManager

class LightOrDarkColor(lightColor: Color, private val darkColor: Color) : Color(lightColor.rgb) {

    private fun isDark() : Boolean {
        val lookAndFeel = UIManager.getLookAndFeel()
        return if (lookAndFeel is FlatLaf) {
            lookAndFeel.isDark
        } else {
            false
        }
    }

    override fun getRed(): Int {
        return if (isDark()) darkColor.red else super.getRed()
    }

    override fun getGreen(): Int {
        return if (isDark()) darkColor.green else super.getGreen()
    }

    override fun getBlue(): Int {
        return if (isDark()) darkColor.blue else super.getBlue()
    }

    override fun getAlpha(): Int {
        return if (isDark()) darkColor.alpha else super.getAlpha()
    }

    override fun getRGB(): Int {
        return if (isDark()) darkColor.rgb else super.getRGB()
    }

    override fun getRGBComponents(compArray: FloatArray?): FloatArray {
        return if (isDark()) darkColor.getRGBComponents(compArray) else super.getRGBComponents(compArray)
    }

    override fun getRGBColorComponents(compArray: FloatArray?): FloatArray {
        return if (isDark()) darkColor.getRGBColorComponents(compArray) else super.getRGBColorComponents(compArray)
    }

    override fun getComponents(compArray: FloatArray?): FloatArray {
        return if (isDark()) darkColor.getComponents(compArray) else super.getComponents(compArray)
    }

    override fun getColorComponents(compArray: FloatArray?): FloatArray {
        return if (isDark()) darkColor.getColorComponents(compArray) else super.getColorComponents(compArray)
    }

    override fun getComponents(cspace: ColorSpace, compArray: FloatArray?): FloatArray {
        return if (isDark()) darkColor.getComponents(cspace, compArray) else super.getComponents(cspace, compArray)
    }

    override fun getColorComponents(cspace: ColorSpace, compArray: FloatArray?): FloatArray {
        return if (isDark()) darkColor.getColorComponents(cspace, compArray) else super.getColorComponents(cspace, compArray)
    }

    override fun getColorSpace(): ColorSpace {
        return if (isDark()) darkColor.colorSpace else super.getColorSpace()
    }

    @Synchronized
    override fun createContext(cm: ColorModel?, r: Rectangle?, r2d: Rectangle2D?, xform: AffineTransform?, hints: RenderingHints?): PaintContext {
        return if (isDark()) darkColor.createContext(cm, r, r2d, xform, hints) else super.createContext(cm, r, r2d, xform, hints)
    }

    override fun getTransparency(): Int {
        return if (isDark()) darkColor.transparency else super.getTransparency()
    }
}