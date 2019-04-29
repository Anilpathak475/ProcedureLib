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
import android.annotation.TargetApi
import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import android.os.Build.VERSION
import androidx.annotation.RequiresPermission


/**
 * The type Easy fingerprint mod.
 */
class EasyFingerprintMod
/**
 * Instantiates a new Easy fingerprint mod.
 *
 * You need to declare the below permission in the manifest file to use this properly
 *
 * <uses-permission android:name="android.permission.USE_FINGERPRINT"></uses-permission>
 *
 * @param context the context
 */
@TargetApi(23)
@RequiresPermission(permission.USE_FINGERPRINT)
constructor(context: Context) {

    private var fingerprintManager: FingerprintManager? = null

    /**
     * Is fingerprint sensor present boolean.
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.USE_FINGERPRINT"></uses-permission>
     *
     * @return the boolean
     */
    val isFingerprintSensorPresent: Boolean
        @SuppressLint("NewApi")
        @RequiresPermission(permission.USE_FINGERPRINT)
        get() = fingerprintManager != null && this.fingerprintManager!!.isHardwareDetected

    init {
        if (VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            this.fingerprintManager = context.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        }
    }

    /**
     * Are fingerprints enrolled boolean.
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.USE_FINGERPRINT"></uses-permission>
     *
     * @return the boolean
     */
    @SuppressLint("NewApi")
    @RequiresPermission(permission.USE_FINGERPRINT)
    fun areFingerprintsEnrolled(): Boolean {
        return fingerprintManager != null && this.fingerprintManager!!.hasEnrolledFingerprints()
    }
}
