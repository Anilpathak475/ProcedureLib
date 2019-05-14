
package com.github.phonestate

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
