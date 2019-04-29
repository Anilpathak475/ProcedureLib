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

import android.Manifest
import android.Manifest.permission
import android.accounts.Account
import android.accounts.AccountManager
import android.annotation.SuppressLint
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Build.VERSION
import android.provider.Settings
import android.provider.Settings.Secure

import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.annotation.RequiresPermission

import java.util.UUID

/**
 * EasyId Mod Class
 */
class EasyIdMod
/**
 * Instantiates a new Easy id mod.
 *
 * @param context the context
 */
    (private val context: Context) {

    /**
     * Get google email accounts
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.GET_ACCOUNTS"></uses-permission>
     *
     * @return the string [ ]
     */
    val accounts: Array<String>
        @RequiresPermission(permission.GET_ACCOUNTS)
        @Deprecated("")
        get() {
            var result: Array<String>? = null
            if (VERSION.SDK_INT < Build.VERSION_CODES.O && PermissionUtil.hasPermission(
                    this.context,
                    permission.GET_ACCOUNTS
                )
            ) {
                val accounts = AccountManager.get(this.context).getAccountsByType("com.google")
                result = arrayOfNulls(accounts.size)
                for (i in accounts.indices) {
                    result[i] = accounts[i].name
                }
            }
            return CheckValidityUtil.checkValidData(result)
        }

    /**
     * Gets android id.
     *
     * @return the android id
     */
    val androidID: String
        @SuppressLint("HardwareIds")
        @Deprecated("")
        get() = CheckValidityUtil.checkValidData(
            Secure.getString(this.context.contentResolver, Secure.ANDROID_ID)
        )

    /**
     * Returns the GSFID.
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="com.google.android.providers.gsf.permission.READ_GSERVICES"></uses-permission>
     *
     * @return the gsfid
     */
    val gsfid: String
        @RequiresPermission("com.google.android.providers.gsf.permission.READ_GSERVICES")
        get() {
            val uri = Uri.parse("content://com.google.android.gsf.gservices")
            val idKey = "android_id"

            val params = arrayOf(idKey)
            val c = this.context.contentResolver.query(uri, null, null, params, null)

            if (c == null) {
                return EasyDeviceInfo.notFoundVal
            } else if (!c.moveToFirst() || c.columnCount < 2) {
                c.close()
                return EasyDeviceInfo.notFoundVal
            }

            try {
                val gsfID = java.lang.Long.toHexString(java.lang.Long.parseLong(c.getString(1)))
                c.close()
                return gsfID
            } catch (e: NumberFormatException) {
                c.close()
                return EasyDeviceInfo.notFoundVal
            }

        }

    /**
     * Gets psuedo unique id.
     *
     * @return the psuedo unique id
     */
    // If all else fails, if the user does have lower than API 9 (lower
    // than Gingerbread), has reset their phone or 'Secure.ANDROID_ID'
    // returns 'null', then simply the ID returned will be solely based
    // off their Android device information. This is where the collisions
    // can happen.
    // Try not to use DISPLAY, HOST or ID - these items could change.
    // If there are collisions, there will be overlapping data
    // Only devices with API >= 9 have android.os.Build.SERIAL
    // http://developer.android.com/reference/android/os/Build.html#SERIAL
    // If a user upgrades software or roots their phone, there will be a duplicate entry
    // Go ahead and return the serial for api => 9
    // String needs to be initialized
    // some value
    // Finally, combine the values we have found by using the UUID class to create a unique identifier
    val pseudoUniqueID: String
        get() {
            var devIDShort = "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10

            devIDShort += if (VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                Build.SUPPORTED_ABIS[0].length % 10
            else
                Build.CPU_ABI.length % 10

            devIDShort += Build.DEVICE.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 + Build.PRODUCT.length % 10
            var serial: String
            try {
                serial = Build::class.java.getField("SERIAL").get(null).toString()
                return UUID(devIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
            } catch (e: Exception) {
                if (EasyDeviceInfo.debuggable) {
                    Log.e(EasyDeviceInfo.nameOfLib, "getPseudoUniqueID: ", e)
                }
                serial = "ESYDV000"
            }

            return UUID(devIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
        }

    /**
     * Gets ua.
     *
     * @return the ua
     */
    val ua: String
        get() {
            val systemUa = System.getProperty("http.agent")
            val result: String
            result = if (VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                WebSettings.getDefaultUserAgent(this.context)
                        + "__" + systemUa
            else
                WebView(this.context).settings.userAgentString + "__" + systemUa
            return CheckValidityUtil.checkValidData(result)
        }
}
