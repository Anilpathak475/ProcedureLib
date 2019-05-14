
package com.github.phonestate

import android.content.Context
import android.media.AudioManager
import android.media.AudioManager.*
import android.os.Build
import android.os.Environment
import android.os.SystemClock
import java.text.SimpleDateFormat
import java.util.*

/**
 * EasyConfig Mod Class
 */
class EasyConfigMod
/**
 * Instantiates a new Easy config mod.
 *
 * @param context the context
 */
    (private val context: Context) {

    /**
     * Gets date from milliseconds
     *
     * @return the date
     */
    val currentDate: Date
        get() = Date(System.currentTimeMillis())

    /**
     * Gets Device Ringer Mode.
     *
     * @return Device Ringer Mode
     */
    //do nothing
    val deviceRingerMode: Int
        @RingerMode
        get() {
            var ringerMode = RingerMode.NORMAL
            val audioManager = this.context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            if (audioManager != null) {
                when (audioManager.ringerMode) {
                    RINGER_MODE_NORMAL -> ringerMode = RingerMode.NORMAL
                    RINGER_MODE_SILENT -> ringerMode = RingerMode.SILENT
                    RINGER_MODE_VIBRATE -> ringerMode = RingerMode.VIBRATE
                    else -> {
                    }
                }
            }

            return ringerMode
        }

    /**
     * Gets formatted date.
     *
     * @return the formatted date
     */
    val formattedDate: String
        get() {
            val dateInstance = SimpleDateFormat.getDateInstance()
            return dateInstance.format(Calendar.getInstance().time)
        }

    /**
     * Gets formatted time.
     *
     * @return the formatted time
     */
    val formattedTime: String
        get() {
            val timeInstance = SimpleDateFormat.getTimeInstance()
            return timeInstance.format(Calendar.getInstance().time)
        }

    /**
     * Gets formatted up time.
     *
     * @return the formatted up time
     */
    val formattedUpTime: String
        get() {
            val timeInstance = SimpleDateFormat.getTimeInstance()
            return timeInstance.format(java.lang.Long.valueOf(SystemClock.uptimeMillis()))
        }

    /**
     * Gets time.
     *
     * @return the time
     */
    val time: Long
        get() = System.currentTimeMillis()

    /**
     * Gets up time.
     *
     * @return the up time
     */
    val upTime: Long
        get() = SystemClock.uptimeMillis()

    /**
     * Is running on emulator boolean.
     *
     * @return the boolean
     */
    val isRunningOnEmulator: Boolean
        get() {
            val isGenyMotion = (Build.MANUFACTURER.contains("Genymotion")
                    || Build.PRODUCT.contains("vbox86p")
                    || Build.DEVICE.contains("vbox86p")
                    || Build.HARDWARE.contains("vbox86"))
            val isGenericEmulator = (Build.BRAND.contains("generic")
                    || Build.DEVICE.contains("generic")
                    || Build.PRODUCT.contains("sdk")
                    || Build.HARDWARE.contains("goldfish"))

            return isGenericEmulator || isGenyMotion
        }

    /**
     * Checks if the device has sd card
     *
     * @return the boolean
     */
    fun hasSdCard(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }
}

