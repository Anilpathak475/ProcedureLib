
package com.github.phonestate

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Environment.*
import android.os.StatFs
import android.util.Log
import java.io.IOException
import java.io.RandomAccessFile

/**
 * EasyMemory Mod Class
 *
 * Deprecation warning suppressed since it is handled in the code
 */
class EasyMemoryMod
/**
 * Instantiates a new Easy memory mod.
 *
 * @param context the context
 */
    (private val context: Context) {

    /**
     * Gets available external memory size.
     *
     * @return the available external memory size
     */
    val availableExternalMemorySize: Long
        get() {
            if (this.externalMemoryAvailable()) {
                val path = getExternalStorageDirectory()
                val stat = StatFs(path.path)
                val blockSize: Long
                val availableBlocks: Long
                if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
                    blockSize = stat.blockSizeLong
                    availableBlocks = stat.availableBlocksLong
                } else {
                    blockSize = stat.blockSize.toLong()
                    availableBlocks = stat.availableBlocks.toLong()
                }
                return availableBlocks * blockSize
            } else {
                return 0
            }
        }

    /**
     * Gets available internal memory size.
     *
     * @return the available internal memory size
     */
    val availableInternalMemorySize: Long
        get() {
            val path = getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize: Long
            val availableBlocks: Long
            if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.blockSizeLong
                availableBlocks = stat.availableBlocksLong
            } else {
                blockSize = stat.blockSize.toLong()
                availableBlocks = stat.availableBlocks.toLong()
            }
            return availableBlocks * blockSize
        }

    /**
     * Gets total external memory size.
     *
     * @return the total external memory size
     */
    val totalExternalMemorySize: Long
        get() {
            if (this.externalMemoryAvailable()) {
                val path = getExternalStorageDirectory()
                val stat = StatFs(path.path)
                val blockSize: Long
                val totalBlocks: Long
                if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
                    blockSize = stat.blockSizeLong
                    totalBlocks = stat.blockCountLong
                } else {
                    blockSize = stat.blockSize.toLong()
                    totalBlocks = stat.blockCount.toLong()
                }
                return totalBlocks * blockSize
            } else {
                return 0
            }
        }

    /**
     * Gets total internal memory size.
     *
     * @return the total internal memory size
     */
    val totalInternalMemorySize: Long
        get() {
            val path = getDataDirectory()
            val stat = StatFs(path.path)
            val blockSize: Long
            val totalBlocks: Long
            if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.blockSizeLong
                totalBlocks = stat.blockCountLong
            } else {
                blockSize = stat.blockSize.toLong()
                totalBlocks = stat.blockCount.toLong()
            }
            return totalBlocks * blockSize
        }

    /**
     * Gets total ram.
     *
     * @return the total ram
     */
    val totalRAM: Long
        get() {
            var totalMemory: Long = 0
            if (Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN) {
                val mi = ActivityManager.MemoryInfo()
                val activityManager = this.context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                if (activityManager != null) {
                    activityManager.getMemoryInfo(mi)
                    totalMemory = mi.totalMem
                }
            } else {
                var reader: RandomAccessFile? = null
                val load: String
                try {
                    reader = RandomAccessFile("/proc/meminfo", "r")
                    load = reader.readLine().replace("\\D+".toRegex(), "")
                    totalMemory = Integer.parseInt(load).toLong()
                } catch (e: IOException) {
                    if (EasyDeviceInfo.debuggable) {
                        Log.e(EasyDeviceInfo.nameOfLib, EasyMemoryMod.IO_EXCEPTION, e)
                    }
                } finally {
                    if (reader != null) {
                        try {
                            reader.close()
                        } catch (e: IOException) {
                            if (EasyDeviceInfo.debuggable) {
                                Log.e(EasyDeviceInfo.nameOfLib, EasyMemoryMod.IO_EXCEPTION, e)
                            }
                        }

                    }
                }
            }
            return totalMemory
        }

    /**
     * Convert to gb float.
     *
     * @param valInBytes the val in bytes
     * @return the float
     */
    fun convertToGb(valInBytes: Long): Float {
        return valInBytes.toFloat() / (EasyMemoryMod.BYTEFACTOR * EasyMemoryMod.BYTEFACTOR * EasyMemoryMod.BYTEFACTOR)
    }

    /**
     * Convert to kb float.
     *
     * @param valInBytes the val in bytes
     * @return the float
     */
    fun convertToKb(valInBytes: Long): Float {
        return valInBytes.toFloat() / EasyMemoryMod.BYTEFACTOR
    }

    /**
     * Convert to mb float.
     *
     * @param valInBytes the val in bytes
     * @return the float
     */
    fun convertToMb(valInBytes: Long): Float {
        return valInBytes.toFloat() / (EasyMemoryMod.BYTEFACTOR * EasyMemoryMod.BYTEFACTOR)
    }

    /**
     * Convert to tb float.
     *
     * @param valInBytes the val in bytes
     * @return the float
     */
    fun convertToTb(valInBytes: Long): Float {
        return valInBytes.toFloat() / (EasyMemoryMod.BYTEFACTOR * EasyMemoryMod.BYTEFACTOR * EasyMemoryMod.BYTEFACTOR
                * EasyMemoryMod.BYTEFACTOR)
    }

    private fun externalMemoryAvailable(): Boolean {
        return getExternalStorageState() == MEDIA_MOUNTED
    }

    companion object {

        private val IO_EXCEPTION = "IO Exception"

        private val BYTEFACTOR = 1024
    }
}
