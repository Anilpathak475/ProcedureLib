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
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build

import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import androidx.annotation.RequiresPermission

import java.util.ArrayList
import java.util.Locale

/**
 * EasySim Mod Class
 */
class EasySimMod
/**
 * Instantiates a new Easy  sim mod.
 *
 * @param context the context
 */
    (private val context: Context) {

    private val tm: TelephonyManager?

    /**
     * Gets active multi sim info.
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
     *
     * @return the active multi sim info
     */
    val activeMultiSimInfo: List<SubscriptionInfo>
        @RequiresPermission(permission.READ_PHONE_STATE)
        get() {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1 && PermissionUtil.hasPermission(
                    context, permission.READ_PHONE_STATE
                )
            ) {
                val tempActiveSub = SubscriptionManager.from(context).activeSubscriptionInfoList
                return if (tempActiveSub == null || tempActiveSub.isEmpty()) {
                    ArrayList(0)
                } else {
                    tempActiveSub
                }
            } else {
                if (EasyDeviceInfo.debuggable) {
                    Log.w(
                        EasyDeviceInfo.nameOfLib,
                        "Device is running on android version that does not support multi sim functionality!"
                    )
                }
            }
            return ArrayList(0)
        }

    /**
     * Gets carrier.
     *
     * @return the carrier
     */
    val carrier: String
        get() {
            var result: String? = null
            if (tm != null && tm.phoneType != TelephonyManager.PHONE_TYPE_CDMA) {
                result = this.tm.networkOperatorName.toLowerCase(Locale.getDefault())
            }
            return CheckValidityUtil.checkValidData(
                CheckValidityUtil.handleIllegalCharacterInResult(result!!)!!
            )
        }

    /**
     * Gets country.
     *
     * @return the country
     */
    val country: String
        get() {
            val result: String
            if (tm != null && tm.simState == TelephonyManager.SIM_STATE_READY) {
                result = this.tm.simCountryIso.toLowerCase(Locale.getDefault())
            } else {
                val locale = Locale.getDefault()
                result = locale.country.toLowerCase(locale)
            }
            return CheckValidityUtil.checkValidData(
                CheckValidityUtil.handleIllegalCharacterInResult(result)!!
            )
        }

    /**
     * Gets imsi.
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
     *
     * @return the imsi
     */
    val imsi: String
        @SuppressLint("HardwareIds")
        @RequiresPermission(permission.READ_PHONE_STATE)
        get() {
            var result: String? = null
            if (tm != null && PermissionUtil.hasPermission(this.context, permission.READ_PHONE_STATE)) {
                result = this.tm.subscriberId
            }

            return CheckValidityUtil.checkValidData(result!!)
        }

    /**
     * Gets number of active sim.
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
     *
     * @return the number of active sim
     */
    val numberOfActiveSim: Int
        @RequiresPermission(permission.READ_PHONE_STATE)
        get() = this.activeMultiSimInfo.size

    /**
     * Gets SIM serial number.
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
     *
     * @return the sim serial
     */
    val simSerial: String
        @SuppressLint("HardwareIds")
        @RequiresPermission(permission.READ_PHONE_STATE)
        get() {
            var result: String? = null
            if (tm != null && PermissionUtil.hasPermission(this.context, permission.READ_PHONE_STATE)) {
                result = this.tm.simSerialNumber
            }
            return CheckValidityUtil.checkValidData(result!!)
        }

    /**
     * Is multi sim.
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
     *
     * @return the boolean
     */
    val isMultiSim: Boolean
        @RequiresPermission(permission.READ_PHONE_STATE)
        get() = activeMultiSimInfo.size > 1

    /**
     * Is sim network locked.
     *
     * @return the boolean
     */
    val isSimNetworkLocked: Boolean
        get() = tm != null && tm.simState == TelephonyManager.SIM_STATE_NETWORK_LOCKED

    init {
        this.tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
    }
}
