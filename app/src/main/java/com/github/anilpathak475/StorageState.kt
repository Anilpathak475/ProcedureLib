package com.procedure.phonestate

import android.app.ActivityManager
import android.content.Context
import android.os.Environment
import android.os.StatFs


class StorageState(val context: Context) {
    private val error = "Not available"

    fun storageStatus(): Storage {
        return Storage(
            getTotalInternalMemorySize(),
            getAvailableInternalMemorySize(),
            getTotalExternalMemorySize(),
            getAvailableExternalMemorySize()
        )
    }

    fun memoryStatus(): Memory {
        val totalMemory = getMemorySize()
        val availableMemory = totalMemory / 0x100000L
        val percentAvail = (availableMemory / totalMemory) * 100.0
        return Memory(
            totalMemory,
            availableMemory,
            percentAvail
        )
    }

    private fun getMemorySize(): Long {
        val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        actManager.getMemoryInfo(memInfo)
        return memInfo.totalMem
    }

    data class Storage(
        val totalInternalMemory: String,
        val availableInternalMemory: String,
        val totalExternalMemory: String,
        val availableExternalMemory: String
    )

    data class Memory(
        val totalMemory: Long,
        val availableMemory: Long,
        val percentageAvailable: Double
    )

    private fun externalMemoryAvailable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    private fun getAvailableInternalMemorySize(): String {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val availableBlocks = stat.availableBlocksLong
        return formatSize(availableBlocks * blockSize)
    }

    private fun getTotalInternalMemorySize(): String {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSizeLong
        val totalBlocks = stat.blockCountLong
        return formatSize(totalBlocks * blockSize)
    }

    private fun getAvailableExternalMemorySize(): String {
        return when {
            externalMemoryAvailable() -> {
                val path = Environment.getExternalStorageDirectory()
                val stat = StatFs(path.path)
                val blockSize = stat.blockSizeLong
                val availableBlocks = stat.availableBlocksLong
                formatSize(availableBlocks * blockSize)
            }
            else -> error
        }
    }

    private fun getTotalExternalMemorySize(): String {
        return when {
            externalMemoryAvailable() -> {
                val path = Environment.getExternalStorageDirectory()
                val stat = StatFs(path.path)
                val blockSize = stat.blockSizeLong
                val totalBlocks = stat.blockCountLong
                formatSize(totalBlocks * blockSize)
            }
            else -> error
        }
    }

    private fun formatSize(size: Long): String {
        var dataSize = size
        var suffix: String? = null

        if (dataSize >= 1024) {
            suffix = "KB"
            dataSize /= 1024
            if (dataSize >= 1024) {
                suffix = "MB"
                dataSize /= 1024
            }
        }

        val resultBuffer = StringBuilder(java.lang.Long.toString(dataSize))

        var commaOffset = resultBuffer.length - 3
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',')
            commaOffset -= 3
        }

        if (suffix != null) resultBuffer.append(suffix)
        return resultBuffer.toString()
    }
}
