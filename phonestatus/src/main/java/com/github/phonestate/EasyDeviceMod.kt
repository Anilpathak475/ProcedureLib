/*
 * Copyright (C) 2016 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.phonestate

import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.content.res.Configuration.ORIENTATION_PORTRAIT
import android.telephony.TelephonyManager.PHONE_TYPE_CDMA
import android.telephony.TelephonyManager.PHONE_TYPE_GSM
import android.telephony.TelephonyManager.PHONE_TYPE_NONE

import android.Manifest
import android.Manifest.permission
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES

import android.telephony.TelephonyManager
import android.util.DisplayMetrics
import android.view.Display
import android.view.WindowManager
import androidx.annotation.RequiresPermission
import java.io.File
import java.util.Locale

/**
 * EasyDevice Mod Class
 */
class EasyDeviceMod
/**
 * Instantiates a new Easy  device mod.
 *
 * @param context the context
 */
    (private val context: Context) {

    private val tm: TelephonyManager

    /**
     * Gets board.
     *
     * @return the board
     */
    val board: String
        get() = CheckValidityUtil.checkValidData(Build.BOARD)

    /**
     * Gets bootloader.
     *
     * @return the bootloader
     */
    val bootloader: String
        get() = CheckValidityUtil.checkValidData(Build.BOOTLOADER)

    /**
     * Gets build brand.
     *
     * @return the build brand
     */
    val buildBrand: String
        get() = CheckValidityUtil.checkValidData(
            CheckValidityUtil.handleIllegalCharacterInResult(Build.BRAND)
        )

    /**
     * Gets build host.
     *
     * @return the build host
     */
    val buildHost: String
        get() = CheckValidityUtil.checkValidData(Build.HOST)

    /**
     * Gets build id.
     *
     * @return the build id
     */
    val buildID: String
        get() = CheckValidityUtil.checkValidData(Build.ID)

    /**
     * Gets build tags.
     *
     * @return the build tags
     */
    val buildTags: String
        get() = CheckValidityUtil.checkValidData(Build.TAGS)

    /**
     * Gets build time.
     *
     * @return the build time
     */
    val buildTime: Long
        get() = Build.TIME

    /**
     * Gets build user.
     *
     * @return the build user
     */
    val buildUser: String
        get() = CheckValidityUtil.checkValidData(Build.USER)

    /**
     * Gets build version codename.
     *
     * @return the build version codename
     */
    val buildVersionCodename: String
        get() = CheckValidityUtil.checkValidData(VERSION.CODENAME)

    /**
     * Gets build version incremental.
     *
     * @return the build version incremental
     */
    val buildVersionIncremental: String
        get() = CheckValidityUtil.checkValidData(VERSION.INCREMENTAL)

    /**
     * Gets build version release.
     *
     * @return the build version release
     */
    val buildVersionRelease: String
        get() = CheckValidityUtil.checkValidData(VERSION.RELEASE)

    /**
     * Gets build version sdk.
     *
     * @return the build version sdk
     */
    val buildVersionSDK: Int
        get() = VERSION.SDK_INT

    /**
     * Gets device.
     *
     * @return the device
     */
    val device: String
        get() = CheckValidityUtil.checkValidData(Build.DEVICE)

    /**
     * Gets display version.
     *
     * @return the display version
     */
    val displayVersion: String
        get() = CheckValidityUtil.checkValidData(Build.DISPLAY)

    /**
     * Gets fingerprint.
     *
     * @return the fingerprint
     */
    val fingerprint: String
        get() = CheckValidityUtil.checkValidData(Build.FINGERPRINT)

    /**
     * Gets hardware.
     *
     * @return the hardware
     */
    val hardware: String
        get() = CheckValidityUtil.checkValidData(Build.HARDWARE)

    /**
     * Gets IMEI number
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
     *
     * @return the imei
     */
    val imei: String
        @SuppressLint("HardwareIds")
        @RequiresPermission(permission.READ_PHONE_STATE)
        @Deprecated("")
        get() {
            var result: String? = null
            if (PermissionUtil.hasPermission(this.context, permission.READ_PHONE_STATE)) {
                result = this.tm.deviceId
            }

            return CheckValidityUtil.checkValidData(result)
        }

    /**
     * Gets language.
     *
     * @return the language
     */
    val language: String
        get() = CheckValidityUtil.checkValidData(Locale.getDefault().language)

    /**
     * Gets manufacturer.
     *
     * @return the manufacturer
     */
    val manufacturer: String
        get() = CheckValidityUtil.checkValidData(
            CheckValidityUtil.handleIllegalCharacterInResult(Build.MANUFACTURER)
        )

    /**
     * Gets model.
     *
     * @return the model
     */
    val model: String
        get() = CheckValidityUtil.checkValidData(
            CheckValidityUtil.handleIllegalCharacterInResult(Build.MODEL)
        )

    /**
     * Gets os codename.
     *
     * @return the os codename
     */
    val osCodename: String
        get() {
            val codename: String
            when (VERSION.SDK_INT) {
                VERSION_CODES.BASE -> codename = "First Android Version. Yay !"
                VERSION_CODES.BASE_1_1 -> codename = "Base Android 1.1"
                VERSION_CODES.CUPCAKE -> codename = "Cupcake"
                VERSION_CODES.DONUT -> codename = "Donut"
                VERSION_CODES.ECLAIR, VERSION_CODES.ECLAIR_0_1, VERSION_CODES.ECLAIR_MR1 ->

                    codename = "Eclair"
                VERSION_CODES.FROYO -> codename = "Froyo"
                VERSION_CODES.GINGERBREAD, VERSION_CODES.GINGERBREAD_MR1 -> codename = "Gingerbread"
                VERSION_CODES.HONEYCOMB, VERSION_CODES.HONEYCOMB_MR1, VERSION_CODES.HONEYCOMB_MR2 -> codename =
                    "Honeycomb"
                VERSION_CODES.ICE_CREAM_SANDWICH, VERSION_CODES.ICE_CREAM_SANDWICH_MR1 -> codename =
                    "Ice Cream Sandwich"
                VERSION_CODES.JELLY_BEAN, VERSION_CODES.JELLY_BEAN_MR1, VERSION_CODES.JELLY_BEAN_MR2 -> codename =
                    "Jelly Bean"
                VERSION_CODES.KITKAT -> codename = "Kitkat"
                VERSION_CODES.KITKAT_WATCH -> codename = "Kitkat Watch"
                VERSION_CODES.LOLLIPOP, VERSION_CODES.LOLLIPOP_MR1 -> codename = "Lollipop"
                VERSION_CODES.M -> codename = "Marshmallow"
                VERSION_CODES.N, VERSION_CODES.N_MR1 -> codename = "Nougat"
                VERSION_CODES.O -> codename = "O"
                else -> codename = EasyDeviceInfo.notFoundVal
            }
            return codename
        }

    /**
     * Gets os version.
     *
     * @return the os version
     */
    val osVersion: String
        get() = CheckValidityUtil.checkValidData(VERSION.RELEASE)

    /**
     * Gets phone no.
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
     *
     * @return the phone no
     */
    val phoneNo: String
        @SuppressLint("HardwareIds")
        @RequiresPermission(permission.READ_PHONE_STATE)
        get() {
            var result: String? = null
            if (PermissionUtil.hasPermission(this.context, permission.READ_PHONE_STATE) && tm.line1Number != null) {
                result = this.tm.line1Number
            }

            return CheckValidityUtil.checkValidData(result)
        }

    /**
     * Gets phone type.
     *
     * @return the phone type
     */
    val phoneType: Int
        @PhoneType
        get() {
            when (this.tm.phoneType) {
                PHONE_TYPE_GSM -> return PhoneType.GSM

                PHONE_TYPE_CDMA -> return PhoneType.CDMA
                PHONE_TYPE_NONE -> return PhoneType.NONE
                else -> return PhoneType.NONE
            }
        }

    /**
     * Gets product.
     *
     * @return the product
     */
    val product: String
        get() = CheckValidityUtil.checkValidData(Build.PRODUCT)

    /**
     * Gets radio ver.
     *
     * @return the radio ver
     */
    val radioVer: String
        get() {
            var result: String? = null
            if (VERSION.SDK_INT >= VERSION_CODES.ICE_CREAM_SANDWICH) {
                result = Build.getRadioVersion()
            }
            return CheckValidityUtil.checkValidData(result)
        }

    /**
     * Gets screen display id.
     *
     * @return the screen display id
     */
    val screenDisplayID: String
        get() {
            val wm = this.context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            if (wm != null) {
                val display = wm.defaultDisplay
                return CheckValidityUtil.checkValidData(display.displayId.toString())
            }
            return CheckValidityUtil.checkValidData("")
        }

    /**
     * Gets serial.
     *
     * @return the serial
     */
    val serial: String
        @SuppressLint("HardwareIds")
        @RequiresPermission(permission.READ_PHONE_STATE)
        get() {
            var result: String? = null
            if (VERSION.SDK_INT < VERSION_CODES.O) {
                result = Build.SERIAL
            } else {
                if (PermissionUtil.hasPermission(this.context, permission.READ_PHONE_STATE)) {
                    result = Build.getSerial()
                }
            }
            return CheckValidityUtil.checkValidData(result)
        }

    /**
     * Is Device rooted boolean.
     *
     * @return the boolean
     */
    val isDeviceRooted: Boolean
        get() {
            val su = "su"
            val locations = arrayOf(
                "/sbin/",
                "/system/bin/",
                "/system/xbin/",
                "/system/sd/xbin/",
                "/system/bin/failsafe/",
                "/data/local/xbin/",
                "/data/local/bin/",
                "/data/local/"
            )
            for (location in locations) {
                if (File(location + su).exists()) {
                    return true
                }
            }
            return false
        }

    init {
        this.tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }

    /**
     * Device type int.
     * Based on metric : https://design.google.com/devices/
     *
     * @param activity the activity
     * @return the int
     */

    @DeviceType
    fun getDeviceType(activity: Activity): Int {
        val metrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(metrics)

        val yInches = metrics.heightPixels / metrics.ydpi
        val xInches = metrics.widthPixels / metrics.xdpi
        val diagonalInches = Math.sqrt((xInches * xInches + yInches * yInches).toDouble())
        return if (diagonalInches > 10.1) {
            DeviceType.TV
        } else if (diagonalInches <= 10.1 && diagonalInches > 7) {
            DeviceType.TABLET
        } else if (diagonalInches <= 7 && diagonalInches > 6.5) {
            DeviceType.PHABLET
        } else if (diagonalInches <= 6.5 && diagonalInches >= 2) {
            DeviceType.PHONE
        } else {
            DeviceType.WATCH
        }
    }

    /**
     * Gets orientation.
     *
     * @param activity the activity
     * @return the orientation
     */
    @OrientationType
    fun getOrientation(activity: Activity): Int {
        when (activity.resources.configuration.orientation) {
            ORIENTATION_PORTRAIT -> return OrientationType.PORTRAIT
            ORIENTATION_LANDSCAPE -> return OrientationType.LANDSCAPE
            else -> return OrientationType.UNKNOWN
        }
    }
}
