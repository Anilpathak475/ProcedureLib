package com.github.phonestate

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager
import android.util.Log
import com.github.phonestate.speedtest.HttpDownloadTest
import com.github.phonestate.speedtest.HttpUploadTest
import com.github.phonestate.speedtest.PingTest
import java.text.DecimalFormat
import java.util.*
import kotlin.properties.Delegates


@SuppressLint("MissingPermission")
class NetworkState(private val context: Context) {

    private val cm by lazy { context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

    private val activeNetwork by lazy { cm.activeNetworkInfo as NetworkInfo }
    private var speed: Long by Delegates.observable(0) { _, old, new -> }
    private val tempBlackList by lazy { HashSet<String>() }
    private val downloadTest by lazy {
        HttpDownloadTest()
    }
    val dec = DecimalFormat("#.##")

    private val uploadTest by lazy { HttpUploadTest() }
    fun getNetworkInfo(): NetworkState {
        return NetworkState(activeNetwork.isConnected, isConnectedWifi(), getNetworkType())
    }

    fun getNetworkSpeed(): String {
        val getSpeedTestHostsHandler = GetSpeedTestHostsHandler()
        downloadTest.finalDownloadRate.observeForever {
            Log.d(
                "Download Speed test",
                it.toString()
            )
        }
        uploadTest.finalUploadRate.observeForever {

            Log.d(
                "Upload Speed test",
                it.toString()
            )
        }


        getSpeedTestHostsHandler.finished.observeForever {
            val mapKey = getSpeedTestHostsHandler.getMapKey()
            val mapValue = getSpeedTestHostsHandler.getMapValue()
            val selfLat = getSpeedTestHostsHandler.getSelfLat()
            val selfLon = getSpeedTestHostsHandler.getSelfLon()
            var tmp = 19349458.0
            var dist = 0.0
            var findServerIndex = 0
            for (index in mapKey.keys) {
                if (tempBlackList.contains(mapValue[index]!![5])) {
                    continue
                }
                val source = Location("Source")
                source.latitude = selfLat
                source.longitude = selfLon

                val ls = mapValue[index]
                val dest = Location("Dest")
                dest.latitude = java.lang.Double.parseDouble(ls!![0])
                dest.longitude = java.lang.Double.parseDouble(ls[1])

                val distance = source.distanceTo(dest).toDouble()
                if (tmp > distance) {
                    tmp = distance
                    dist = distance
                    findServerIndex = index
                }
            }
            var uploadAddr = mapKey[findServerIndex]
            uploadAddr =
                uploadAddr!!.replace(
                    uploadAddr.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[uploadAddr.split(
                        "/".toRegex()
                    ).dropLastWhile { it.isEmpty() }.toTypedArray().size - 1], ""
                )
            val info = mapValue[findServerIndex]
            val distance = dist
            val pingTest = PingTest(info!![6].replace(":8080", ""), 6)
            downloadTest.fileURL = uploadAddr
            uploadTest.fileURL = uploadAddr
            Log.d(
                "Speed test ",
                String.format(
                    "Host Location: %s [Distance: %s km]",
                    info[2],
                    DecimalFormat("#.##").format(distance / 1000)
                )
            )
            pingTest.start()
            downloadTest.execute()
            uploadTest.start()
        }
        getSpeedTestHostsHandler.start()
        return ""
    }

    private fun getNetworkType(): String {
        val mTelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return when (mTelephonyManager.networkType) {
            TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN -> "2G"
            TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_HSPAP -> "3G"
            TelephonyManager.NETWORK_TYPE_LTE -> "4G"
            else -> "Unknown"
        }
    }

    private fun isConnectedWifi(): Boolean {
        return activeNetwork.isConnected && activeNetwork.type == ConnectivityManager.TYPE_WIFI
    }

    data class NetworkState(val isConnected: Boolean, val isWifi: Boolean, val networkType: String)
}