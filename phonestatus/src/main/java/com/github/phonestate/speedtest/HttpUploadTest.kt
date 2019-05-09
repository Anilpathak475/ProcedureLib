package com.github.phonestate.speedtest


import androidx.lifecycle.MutableLiveData
import com.github.phonestate.speedtest.HttpUploadTest.Companion.uploadedKByte
import java.io.DataOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

/**
 * @author erdigurbuz
 */
class HttpUploadTest : Thread() {

    var fileURL = ""
    var isFinished = false
        private set
    private var startTime: Long = 0
    var finalUploadRate = MutableLiveData<Double>()

    val instantUploadRate: Double
        get() {
            try {
                val bd = BigDecimal(uploadedKByte)
            } catch (ex: Exception) {
                return 0.0
            }

            if (uploadedKByte >= 0) {
                val now = System.currentTimeMillis()
                val elapsedTime = (now - startTime) / 1000.0
                return round(uploadedKByte / 1000.0 * 8 / elapsedTime)
            } else {
                return 0.0
            }
        }

    private fun round(value: Double): Double {
        var bd: BigDecimal
        try {
            bd = BigDecimal(value)
        } catch (ex: Exception) {
            return 0.0
        }

        bd = bd.setScale(2, RoundingMode.HALF_UP)
        return bd.toDouble()
    }

    override fun run() {
        try {
            val url = URL(fileURL)
            uploadedKByte = 0.0
            startTime = System.currentTimeMillis()

            val executor = Executors.newFixedThreadPool(4)
            for (i in 0..3) {
                executor.execute(HandlerUpload(url))
            }
            executor.shutdown()
            while (!executor.isTerminated) {
                try {
                    sleep(100)
                } catch (ignored: InterruptedException) {
                }

            }

            val now = System.currentTimeMillis()
            val uploadElapsedTime = (now - startTime) / 1000.0
            finalUploadRate.postValue(uploadedKByte / 1000.0 * 8 / uploadElapsedTime)

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        isFinished = true
    }

    companion object {
        internal var uploadedKByte = 0.0
    }
}

internal class HandlerUpload(private val url: URL) : Thread() {

    override fun run() {
        val buffer = ByteArray(150 * 1024)
        val startTime = System.currentTimeMillis()
        val timeout = 10

        while (true) {

            try {
                val conn = url.openConnection() as HttpURLConnection
                conn.doOutput = true
                conn.requestMethod = "POST"
                conn.setRequestProperty("Connection", "Keep-Alive")
                val dos = DataOutputStream(conn.outputStream)
                dos.write(buffer, 0, buffer.size)
                dos.flush()

                conn.responseCode

                uploadedKByte += buffer.size / 1024.0
                val endTime = System.currentTimeMillis()
                val uploadElapsedTime = (endTime - startTime) / 1000.0
                if (uploadElapsedTime >= timeout) {
                    break
                }

                dos.close()
                conn.disconnect()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }

        }
    }
}
