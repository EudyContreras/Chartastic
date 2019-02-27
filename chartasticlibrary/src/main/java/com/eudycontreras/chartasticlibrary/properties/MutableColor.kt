package com.eudycontreras.chartasticlibrary.properties

import androidx.annotation.ColorInt
import com.eudycontreras.chartasticlibrary.global.AndroidColor


/**
 * Created by eudycontreras.
 */

data class MutableColor(
    private var alpha: Int = 255,
    private var red: Int = 0,
    private var green: Int = 0,
    private var blue: Int = 0
): Color() {

    constructor(@ColorInt color: Int) : this(){
        this.alpha = AndroidColor.alpha(color)
        this.red = AndroidColor.red(color)
        this.green = AndroidColor.green(color)
        this.blue = AndroidColor.blue(color)
    }

    constructor(color: MutableColor): this(){
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
    ): MutableColor {
        this.alpha = alpha
        this.red = red
        this.green = green
        this.blue = blue
        colorChanged = true
        return this
    }

    fun updateAlpha(alpha: Int): MutableColor {
        this.alpha = alpha
        colorChanged = true
        return this
    }

    fun updateAlpha(alpha: Float): MutableColor {
        this.alpha = Math.round(255f * alpha)
        colorChanged = true
        return this
    }

    override fun getOpacity(): Float {
        return (alpha/255f)
    }

    fun subtractAlpha(amount: Float): MutableColor {
        val color = MutableColor(this)
        color.alpha -= (Math.round(amount * 255))
        return color
    }

    fun adjust(amount: Float): MutableColor {
        val color = MutableColor(this)
        color.red = (this.red * amount).toInt()
        color.green = (this.green * amount).toInt()
        color.blue = (this.blue * amount).toInt()

        color.red = clamp(color.red)
        color.green = clamp(color.green)
        color.blue = clamp(color.blue)
        return color
    }

    fun addAlpha(amount: Float): MutableColor {
        val alpha = this.alpha + (Math.round(amount * 255))
        return this.copy(alpha = alpha)
    }

    fun subtractAlpha(amount: Int): MutableColor {
        val alpha = this.alpha - amount
        return this.copy(alpha = alpha)
    }

    fun addAlpha(amount: Int): MutableColor {
        val alpha = this.alpha + amount
        return this.copy(alpha = alpha)
    }

    fun addRed(amount: Int): MutableColor {
        red = clamp(red + amount)
        return this
    }

    fun addGreen(amount: Int): MutableColor {
        green = clamp(green + amount)
        return this
    }

    fun addBlue(amount: Int): MutableColor {
        blue = clamp(blue + amount)
        return this
    }

    fun subtractRed(amount: Int): MutableColor {
        red = clamp(red - amount)
        return this
    }

    fun subtractGreen(amount: Int): MutableColor {
        green = clamp( green - amount)
        return this
    }

    fun subtractBlue(amount: Int): MutableColor {
        blue = clamp(blue - amount)
        return this
    }

    override fun getAlpha() = alpha

    override fun getRed() = red

    override fun getGreen() = green

    override fun getBlue() = blue

    fun setColor(color: MutableColor) {
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

    override fun toColor(): Int {
        return if (mTempColor == -1) {
            mTempColor = AndroidColor.argb(alpha, red, green, blue)
            mTempColor
        } else {
            if (colorChanged) {
                mTempColor = AndroidColor.argb(alpha, red, green, blue)
                colorChanged = false
                mTempColor
            } else {
                mTempColor
            }
        }
    }

    companion object {

        val White  = MutableColor(255, 255, 255, 255)
        val Black  = MutableColor(255, 0, 0, 0)
        val Red  = MutableColor(255, 255, 0, 0)
        val Green  = MutableColor(255, 0, 255, 0)
        val Blue  = MutableColor(255, 0, 0, 255)

        val Default: MutableColor = MutableColor()

        fun adjustAlpha(color: MutableColor, factor: Float) {
            color.updateAlpha((Math.round(color.alpha * factor)))
        }

        fun adjustAlpha(@ColorInt color: Int, factor: Float): Int {
            val alpha = Math.round(AndroidColor.alpha(color) * factor)
            val red = AndroidColor.red(color)
            val green = AndroidColor.green(color)
            val blue = AndroidColor.blue(color)

            return AndroidColor.argb(alpha, red, green, blue)
        }

        fun interpolateColor(start: MutableColor, end: MutableColor, amount: Float, result: MutableColor) {
            result.setColor(start)
            result.updateColor(
                red = ((start.red + (end.red - start.red) * amount).toInt()),
                green = ((start.green + (end.green - start.green) * amount).toInt()),
                blue = ((start.blue + (end.blue - start.blue) * amount).toInt())
            )
        }

        fun rgb(red: Int, green: Int, blue: Int): MutableColor{
            return MutableColor(
                alpha = 255,
                red = red,
                green = green,
                blue = blue
            )
        }

        fun rgba(red: Int, green: Int, blue: Int, alpha: Int): MutableColor{
            return MutableColor(
                alpha = alpha,
                red = red,
                green = green,
                blue = blue
            )
        }

        fun rgba(red: Int, green: Int, blue: Int, alpha: Float): MutableColor{
            return MutableColor(
                alpha = (alpha * 255).toInt(),
                red = red,
                green = green,
                blue = blue
            )
        }

        fun toColor(@ColorInt color: Int): MutableColor {
            return MutableColor(color)
        }

        fun fromColor(color: Int) : MutableColor {
            return MutableColor(color)
        }

        fun fromColor(color: Color) : MutableColor {
            return MutableColor(color.getAlpha(), color.getRed(), color.getGreen(), color.getBlue())
        }

        fun fromHexString(color: String): MutableColor {
            return MutableColor(AndroidColor.parseColor(color))
        }

        fun random(): MutableColor {
            return MutableColor(
                (190 until 191).random(),
                (0 until 205).random(),
                (0 until 205).random(),
                (0 until 205).random()
            )
        }
    }
}