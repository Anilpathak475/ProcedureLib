
package com.github.phonestate

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log


/**
 * Permission Util Class
 */
internal object PermissionUtil {

    /**
     * Has permission method.
     *
     * @param context    the context
     * @param permission the permission
     * @return the boolean
     */
    fun hasPermission(context: Context, permission: String): Boolean {
        val permGranted = context.checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
        if (EasyDeviceInfo.debuggable && !permGranted) {
            Log.e(EasyDeviceInfo.nameOfLib, ">\t$permission")
            Log.w(
                EasyDeviceInfo.nameOfLib,
                "\nPermission not granted/missing!\nMake sure you have declared the permission in your manifest file (and granted it on API 23+).\n"
            )
        }
        return permGranted
    }
}// private constructor for utility class
