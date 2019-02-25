package com.eudycontreras.chartasticlibrary.properties

import androidx.annotation.ColorInt
import com.eudycontreras.chartasticlibrary.global.AndroidColor


/**
 * Created by eudycontreras.
 */

data class Color(
    private var alpha: Int = 255,
    private var red: Int = 0,
    private var green: Int = 0,
    private var blue: Int = 0
) {

    private var mColorChanged: Boolean = false
    private var mTempColor: Int = -1

    private var colorChanged: Boolean
        get() = mColorChanged
        set(value) {mColorChanged = value}

    constructor(@ColorInt color: Int) : this(){
        this.alpha = AndroidColor.alpha(color)
        this.red = AndroidColor.red(color)
        this.green = AndroidColor.green(color)
        this.blue = AndroidColor.blue(color)
    }

    constructor(color: Color): this(){
        this.alpha = color.alpha
        this.red = color.red
        this.green = color.green
        this.blue = color.blue
    }

    fun updateColor(
        alpha: Int = 0,
        red: Int = 0,
        green: Int = 0,
        blue: Int = 0
    ) {
        this.alpha = alpha
        this.red = red
        this.green = green
        this.blue = blue
        colorChanged = true
    }

    fun updateAlpha(alpha: Int) {
        this.alpha = alpha
        colorChanged = true
    }

    fun updateAlpha(alpha: Float) {
        this.alpha = Math.round(255f * alpha)
        colorChanged = true
    }

    fun getOpacity(): Float {
        return (alpha.toFloat()/255f)
    }

    fun subtractAlpha(amount: Float): Color {
        val color = Color(this)
        color.alpha -= (Math.round(amount * 255))
        return color
    }

    fun adjust(amount: Float): Color {
        val color = Color(this)
        color.red = (this.red * amount).toInt()
        color.green = (this.green * amount).toInt()
        color.blue = (this.blue * amount).toInt()

        color.red = clamp(color.red)
        color.green = clamp(color.green)
        color.blue = clamp(color.blue)
        return color
    }

    fun addAlpha(amount: Float): Color {
        val alpha = this.alpha + (Math.round(amount * 255))
        return this.copy(alpha = alpha)
    }

    fun subtractAlpha(amount: Int): Color {
        val alpha = this.alpha - amount
        return this.copy(alpha = alpha)
    }

    fun addAlpha(amount: Int): Color {
        val alpha = this.alpha + amount
        return this.copy(alpha = alpha)
    }

    fun addRed(amount: Int): Color {
        red = clamp(red + amount)
        return this
    }

    fun addGreen(amount: Int): Color {
        green = clamp(green + amount)
        return this
    }

    fun addBlue(amount: Int): Color {
        blue = clamp(blue + amount)
        return this
    }

    fun subtractRed(amount: Int): Color {
        red = clamp(red - amount)
        return this
    }

    fun subtractGreen(amount: Int): Color {
        green = clamp( green - amount)
        return this
    }

    fun subtractBlue(amount: Int): Color {
        blue = clamp(blue - amount)
        return this
    }

    fun getAlpha() = alpha

    fun getRed() = red

    fun getGreen() = green

    fun getBlue() = blue

    fun setColor(color: Color) {
        this.alpha = color.alpha
        this.red = color.red
        this.green = color.green
        this.blue = color.blue

        colorChanged = true
    }

    fun setColor(red: Int, green: Int, blue: Int, alpha: Int) {
        this.alpha = alpha
        this.red = red
        this.green = green
        this.blue = blue

        colorChanged = true
    }

    fun toColor(): Int {
        return if (mTempColor == -1) {
            mTempColor = AndroidColor.argb(alpha, red, green, blue)
            mTempColor
        } else {
            if (mColorChanged) {
                mTempColor = AndroidColor.argb(alpha, red, green, blue)
                colorChanged = false
                mTempColor
            } else {
                mTempColor
            }
        }
    }

    private fun clamp(color: Int): Int {
        return when {
            color > 255 -> 255
            color < 0 -> 0
            else -> color
        }
    }
    companion object {

        val White  = Color(255, 255, 255, 255)
        val Black  = Color(255, 0, 0, 0)
        val Red  = Color(255, 255, 0, 0)
        val Green  = Color(255, 0, 255, 0)
        val Blue  = Color(255, 0, 0, 255)

        val Default: Color = Color()

        fun colorDecToHex(r: Int, g: Int, b: Int): Int {
            return AndroidColor.parseColor(colorDecToHexString(r, g, b))
        }

        fun colorDecToHex(a: Int, r: Int, g: Int, b: Int): Int {
            return AndroidColor.parseColor(colorDecToHexString(a, r, g, b))
        }

        fun colorDecToHexString(r: Int, g: Int, b: Int): String {
            return colorDecToHexString(255, r, g, b)
        }

        fun colorDecToHexString(a: Int, r: Int, g: Int, b: Int): String {
            var red = Integer.toHexString(r)
            var green = Integer.toHexString(g)
            var blue = Integer.toHexString(b)
            var alpha = Integer.toHexString(a)

            if (red.length == 1) {
                red = "0$red"
            }
            if (green.length == 1) {
                green = "0$green"
            }
            if (blue.length == 1) {
                blue = "0$blue"
            }
            if (alpha.length == 1) {
                alpha = "0$alpha"
            }

            return "#$alpha$red$green$blue"
        }

        fun adjustAlpha(color: Color, factor: Float) {
            color.updateAlpha((Math.round(color.alpha * factor)))
        }

        fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
            val alpha = Math.round(AndroidColor.alpha(color) * factor)
            val red = AndroidColor.red(color)
            val green = AndroidColor.green(color)
            val blue = AndroidColor.blue(color)

            return AndroidColor.argb(alpha, red, green, blue)
        }

        fun interpolateColor(start: Color, end: Color, amount: Float, result: Color) {
            result.setColor(start)
            result.updateColor(
                red = ((start.red + (end.red - start.red) * amount).toInt()),
                green = ((start.green + (end.green - start.green) * amount).toInt()),
                blue = ((start.blue + (end.blue - start.blue) * amount).toInt())
            )
        }

        fun rgb(red: Int, green: Int, blue: Int): Color{
            return Color(
                alpha = 255,
                red = red,
                green = green,
                blue = blue
            )
        }

        fun rgba(red: Int, green: Int, blue: Int, alpha: Int): Color{
            return Color(
                alpha = alpha,
                red = red,
                green = green,
                blue = blue
            )
        }

        fun rgba(red: Int, green: Int, blue: Int, alpha: Float): Color{
            return Color(
                alpha = (alpha * 255).toInt(),
                red = red,
                green = green,
                blue = blue
            )
        }

        fun toColor(@ColorInt color: Int): Color {
            return Color(color)
        }

        fun fromColor(color: Color) : Color {
            return Color(color.alpha, color.red, color.green, color.blue)
        }

        fun fromHexString(color: String): Color {
            return Color(AndroidColor.parseColor(color))
        }

        fun random(): Color {
            return Color(
                (190 until 191).random(),
                (0 until 205).random(),
                (0 until 205).random(),
                (0 until 205).random()
            )
        }
    }
}