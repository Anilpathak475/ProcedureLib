package com.procedure.phonestate

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.telephony.TelephonyManager


@SuppressLint("MissingPermission")
class NetworkState(private val context: Context) {
    private val cm by lazy { context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager }

    private val activeNetwork by lazy { cm.activeNetworkInfo as NetworkInfo }

    fun getNetworkInfo(): NetworkState {
        return NetworkState(activeNetwork.isConnected, isConnectedWifi(), getNetworkType())
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