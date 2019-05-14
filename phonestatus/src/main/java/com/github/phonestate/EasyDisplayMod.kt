
package com.github.phonestate

import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import android.util.DisplayMetrics
import android.view.Display
import android.view.MotionEvent
import android.view.WindowManager

/**
 * EasyDisplay Mod Class
 */
class EasyDisplayMod
/**
 * Instantiates a new Easy display mod.
 *
 * @param context the context
 */
    (private val context: Context) {

    private val display: Display?

    /**
     * Gets density.
     *
     * @return the density
     */
    //do nothing
    val density: String
        get() {
            var densityStr: String? = null
            val density = this.context.resources.displayMetrics.densityDpi
            when (density) {
                DisplayMetrics.DENSITY_LOW -> densityStr = "LDPI"
                DisplayMetrics.DENSITY_MEDIUM -> densityStr = "MDPI"
                DisplayMetrics.DENSITY_TV -> densityStr = "TVDPI"
                DisplayMetrics.DENSITY_HIGH -> densityStr = "HDPI"
                DisplayMetrics.DENSITY_XHIGH -> densityStr = "XHDPI"
                DisplayMetrics.DENSITY_400 -> densityStr = "XMHDPI"
                DisplayMetrics.DENSITY_XXHIGH -> densityStr = "XXHDPI"
                DisplayMetrics.DENSITY_XXXHIGH -> densityStr = "XXXHDPI"
                else -> {
                }
            }
            return CheckValidityUtil.checkValidData(densityStr!!)
        }

    val layoutDirection: Int
        get() = if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
            this.context.resources.configuration
                .layoutDirection
        else
            this.context.resources.configuration.screenLayout

    val orientation: Int
        get() = this.context.resources.configuration.orientation

    val physicalSize: Float
        get() {
            val metrics = DisplayMetrics()

            if (display != null) {
                this.display.getMetrics(metrics)
                val x = StrictMath.pow((metrics.widthPixels / metrics.xdpi).toDouble(), 2.0).toFloat()
                val y = StrictMath.pow((metrics.heightPixels / metrics.ydpi).toDouble(), 2.0).toFloat()
                return Math.sqrt((x + y).toDouble()).toFloat()
            }
            return 0.0f
        }

    val refreshRate: Float
        get() = this.display!!.refreshRate

    /**
     * Gets resolution.
     *
     * @return the resolution
     */
    val resolution: String
        get() {
            val metrics = DisplayMetrics()
            if (display != null) {
                this.display.getMetrics(metrics)
                return CheckValidityUtil.checkValidData(metrics.heightPixels.toString() + "x" + metrics.widthPixels)
            }
            return CheckValidityUtil.checkValidData("")
        }

    val isScreenRound: Boolean
        get() = if (VERSION.SDK_INT >= Build.VERSION_CODES.M)
            this.context.resources.configuration
                .isScreenRound
        else
            false

    init {

        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        this.display = wm.defaultDisplay
    }

    /**
     * Get display xy coordinates int [ ].
     *
     * @param event the event
     * @return the int [ ]
     */
    fun getDisplayXYCoordinates(event: MotionEvent): IntArray {
        val coordinates = IntArray(2)
        coordinates[0] = 0
        coordinates[1] = 0
        if (event.action == MotionEvent.ACTION_DOWN) {
            coordinates[0] = event.x.toInt()     // X Coordinates
            coordinates[1] = event.y.toInt()     // Y Coordinates
        }
        return coordinates
    }
}
