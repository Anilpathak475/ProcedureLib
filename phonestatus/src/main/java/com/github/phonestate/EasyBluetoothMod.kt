
package com.github.phonestate

import android.Manifest.permission
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings.Secure
import androidx.annotation.RequiresPermission


/**
 * EasyBluetooth Mod Class
 */
class EasyBluetoothMod
/**
 * Instantiates a new Easy bluetooth mod.
 *
 * @param context the context
 */
    (private val context: Context) {

    /**
     * Gets Bluetooth MAC Address
     *
     * You need to declare the below permission in the manifest file to use this properly
     *
     * <uses-permission android:name="android.permission.BLUETOOTH"></uses-permission>
     *
     * @return the bluetooth mac
     */
    // Hardware ID are restricted in Android 6+
    // https://developer.android.com/about/versions/marshmallow/android-6.0-changes.html#behavior-hardware-id
    // Getting bluetooth mac via reflection for devices with Android 6+
    val bluetoothMAC: String
        @SuppressLint("HardwareIds")
        @RequiresPermission(permission.BLUETOOTH)
        @Deprecated("")
        get() {
            var result = "00:00:00:00:00:00"
            if (PermissionUtil.hasPermission(this.context, permission.BLUETOOTH)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                    result = Secure.getString(
                        this.context.contentResolver,
                        "bluetooth_address"
                    )
                } else {
                    val bta = BluetoothAdapter.getDefaultAdapter()
                    result = if (bta != null) bta.address else result
                }
            }
            return CheckValidityUtil.checkValidData(result)
        }

    /**
     * Has Bluetooth LE
     *
     * @return true if the device has a Bluetooth LE compatible chipset
     */
    fun hasBluetoothLe(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 && this.context.packageManager.hasSystemFeature(
            PackageManager.FEATURE_BLUETOOTH_LE
        )
    }

    /**
     * Has Bluetooth LE advertising
     *
     * @return true if the device has Bluetooth LE advertising features
     */
    @RequiresPermission(permission.BLUETOOTH)
    fun hasBluetoothLeAdvertising(): Boolean {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && this.hasBluetoothLe()
                && BluetoothAdapter.getDefaultAdapter().isMultipleAdvertisementSupported)
    }
}
