
package com.github.phonestate

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Build.VERSION
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import androidx.annotation.RequiresPermission
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.SocketException
import java.util.*

/**
 * EasyNetwork Mod Class
 */
class EasyNetworkMod
/**
 * Instantiates a new Easy  network mod.
 *
 * @param context the context
 */
    (private val context: Context) {

    /**
     * Gets ip address v4.
     *
     * @return the ip address
     */
    val iPv4Address: String
        get() {
            var result: String? = null
            try {
                val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    val addrs = Collections.list(intf.inetAddresses)
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress) {
                            val sAddr = addr.hostAddress.toUpperCase(Locale.getDefault())
                            val isIPv4 = addr is Inet4Address
                            if (isIPv4) {
                                result = sAddr
                            }
                        }
                    }
                }
            } catch (e: SocketException) {
                if (EasyDeviceInfo.debuggable) {
                    Log.e(EasyDeviceInfo.nameOfLib, EasyNetworkMod.SOCKET_EXCEPTION, e)
                }
            }

            return CheckValidityUtil.checkValidData(result!!)
        }

    /**
     * Gets ip address v6.
     *
     * @return the ip address
     */
    // drop ip6 port suffix
    val iPv6Address: String
        get() {
            var result: String? = null
            try {
                val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
                for (intf in interfaces) {
                    val addrs = Collections.list(intf.inetAddresses)
                    for (addr in addrs) {
                        if (!addr.isLoopbackAddress) {
                            val sAddr = addr.hostAddress.toUpperCase(Locale.getDefault())
                            val isIPv4 = addr is Inet4Address
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%')
                                result = if (delim < 0) sAddr else sAddr.substring(0, delim)
                            }
                        }
                    }
                }
            } catch (e: SocketException) {
                if (EasyDeviceInfo.debuggable) {
                    Log.e(EasyDeviceInfo.nameOfLib, EasyNetworkMod.SOCKET_EXCEPTION, e)
                }
            }

            return CheckValidityUtil.checkValidData(result!!)
        }

    /**
     * Gets network type.
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
     * <uses-permission android:name="android.permission.INTERNET"></uses-permission>
     *
     * @return the network type
     */
    // Unknown
    // Cellular Data 2G
    // Cellular Data 3G
    // Cellular Data 4G
    // Cellular Data Unknown Generation
    val networkType: Int
        @RequiresPermission(allOf = [permission.ACCESS_NETWORK_STATE, permission.INTERNET])
        @NetworkType
        get() {
            var result = NetworkType.UNKNOWN
            if (PermissionUtil.hasPermission(this.context, permission.ACCESS_NETWORK_STATE)) {
                val cm = this.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                if (cm != null) {
                    val activeNetwork = cm.activeNetworkInfo
                    if (activeNetwork == null) {
                        result = NetworkType.UNKNOWN
                    } else if (activeNetwork.type == ConnectivityManager.TYPE_WIFI || activeNetwork.type == ConnectivityManager.TYPE_WIMAX) {
                        result = NetworkType.WIFI_WIFIMAX
                    } else if (activeNetwork.type == ConnectivityManager.TYPE_MOBILE) {
                        val manager = this.context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

                        if (manager != null && manager.simState == TelephonyManager.SIM_STATE_READY) {
                            when (manager.networkType) {
                                TelephonyManager.NETWORK_TYPE_UNKNOWN -> result = NetworkType.CELLULAR_UNKNOWN
                                TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_IDEN, TelephonyManager.NETWORK_TYPE_1xRTT -> result =
                                    NetworkType.CELLULAR_2G
                                TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_EVDO_B -> result =
                                    NetworkType.CELLULAR_3G
                                TelephonyManager.NETWORK_TYPE_LTE -> result = NetworkType.CELLULAR_4G
                                else -> result = NetworkType.CELLULAR_UNIDENTIFIED_GEN
                            }
                        }
                    }
                }
            }
            return result
        }

    /**
     * Gets BSSID of Connected WiFi
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
     *
     * @return Return the basic service set identifier (BSSID) of the current access point.
     */
    val wifiBSSID: String
        @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.ACCESS_NETWORK_STATE])
        get() {
            var result: String? = null
            if (PermissionUtil.hasPermission(this.context, permission.ACCESS_WIFI_STATE)) {
                val cm = this.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                if (cm != null) {
                    val networkInfo = cm.activeNetworkInfo
                    if (networkInfo == null) {
                        result = null
                    }

                    if (networkInfo != null && networkInfo.isConnected) {
                        val wifiManager =
                            this.context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                        if (wifiManager != null) {
                            val connectionInfo = wifiManager.connectionInfo
                            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.ssid)) {
                                result = connectionInfo.bssid
                            }
                        }
                    }
                }
            }
            return CheckValidityUtil.checkValidData(result!!)
        }

    /**
     * Gets Link Speed of Connected WiFi
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
     *
     * @return link speed
     */
    val wifiLinkSpeed: String
        @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.ACCESS_NETWORK_STATE])
        get() {
            var result: String? = null
            if (PermissionUtil.hasPermission(this.context, permission.ACCESS_WIFI_STATE)) {
                val cm = this.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = cm.activeNetworkInfo
                if (networkInfo == null) {
                    result = null
                }

                if (networkInfo != null && networkInfo.isConnected) {
                    val wifiManager =
                        this.context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val connectionInfo = wifiManager.connectionInfo
                    if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.ssid)) {
                        result = connectionInfo.linkSpeed.toString() + " Mbps"
                    }
                }
            }
            return CheckValidityUtil.checkValidData(result!!)
        }

    /**
     * Gets WiFi MAC Address
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
     *
     * @return the wifi mac
     */
    // Hardware ID are restricted in Android 6+
    // https://developer.android.com/about/versions/marshmallow/android-6.0-changes.html#behavior-hardware-id
    val wifiMAC: String
        @SuppressLint("HardwareIds")
        @RequiresPermission(permission.ACCESS_WIFI_STATE)
        get() {
            var result = "02:00:00:00:00:00"
            if (PermissionUtil.hasPermission(this.context, permission.ACCESS_WIFI_STATE)) {
                if (VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    var interfaces: Enumeration<NetworkInterface>? = null
                    try {
                        interfaces = NetworkInterface.getNetworkInterfaces()
                    } catch (e: SocketException) {
                        if (EasyDeviceInfo.debuggable) {
                            Log.e(EasyDeviceInfo.nameOfLib, EasyNetworkMod.SOCKET_EXCEPTION, e)
                        }
                    }

                    while (interfaces != null && interfaces.hasMoreElements()) {
                        val networkInterface = interfaces.nextElement()

                        var addr: ByteArray? = ByteArray(0)
                        try {
                            addr = networkInterface.hardwareAddress
                        } catch (e: SocketException) {
                            if (EasyDeviceInfo.debuggable) {
                                Log.e(EasyDeviceInfo.nameOfLib, EasyNetworkMod.SOCKET_EXCEPTION, e)
                            }
                        }

                        if (addr == null || addr.size == 0) {
                            continue
                        }

                        val buf = StringBuilder()
                        for (b in addr) {
                            buf.append(String.format("%02X:", java.lang.Byte.valueOf(b)))
                        }
                        if (buf.length > 0) {
                            buf.deleteCharAt(buf.length - 1)
                        }
                        val mac = buf.toString()
                        val wifiInterfaceName = "wlan0"
                        result = if (wifiInterfaceName == networkInterface.name) mac else result
                    }
                } else {
                    val wm = this.context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    if (wm != null) {
                        result = wm.connectionInfo.macAddress
                    }
                }
            }
            return CheckValidityUtil.checkValidData(result)
        }

    /**
     * Gets SSID of Connected WiFi
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
     *
     * @return Returns the service set identifier (SSID) of the current 802.11 network
     */
    val wifiSSID: String
        @RequiresPermission(allOf = [permission.ACCESS_WIFI_STATE, permission.ACCESS_NETWORK_STATE])
        get() {
            var result: String? = null
            if (PermissionUtil.hasPermission(this.context, permission.ACCESS_WIFI_STATE)) {
                val cm = this.context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = cm.activeNetworkInfo
                if (networkInfo == null) {
                    result = null
                }

                if (networkInfo != null && networkInfo.isConnected) {
                    val wifiManager =
                        this.context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                    val connectionInfo = wifiManager.connectionInfo
                    if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.ssid)) {
                        result = connectionInfo.ssid
                    }
                }
            }
            return CheckValidityUtil.checkValidData(result!!)
        }

    /**
     * Is network available boolean.
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
     * <uses-permission android:name="android.permission.INTERNET"></uses-permission>
     *
     * @return the boolean
     */
    val isNetworkAvailable: Boolean
        @RequiresPermission(allOf = [permission.ACCESS_NETWORK_STATE, permission.INTERNET])
        get() {
            if (PermissionUtil.hasPermission(
                    this.context,
                    permission.INTERNET
                ) && PermissionUtil.hasPermission(this.context, permission.ACCESS_NETWORK_STATE)
            ) {
                val cm = this.context.applicationContext
                    .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val netInfo = cm.activeNetworkInfo
                return netInfo != null && netInfo.isConnected
            }
            return false
        }

    /**
     * @return true if a Wi-Fi Aware compatible chipset is present in the device.
     * @see [https://developer.android.com/guide/topics/connectivity/wifi-aware.html](https://developer.android.com/guide/topics/connectivity/wifi-aware.html)
     */
    val isWifiAwareAvailable: Boolean
        get() = VERSION.SDK_INT >= Build.VERSION_CODES.O && this.context.packageManager
            .hasSystemFeature(PackageManager.FEATURE_WIFI_AWARE)

    /**
     * Is wifi enabled.
     *
     * @return the boolean
     */
    val isWifiEnabled: Boolean
        get() {
            var wifiState = false

            val wifiManager = this.context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiState = wifiManager.isWifiEnabled
            return wifiState
        }

    companion object {

        private val SOCKET_EXCEPTION = "Socket Exception"
    }
}
