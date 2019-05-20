
package com.github.phonestate

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.NameNotFoundException
import android.util.Log


/**
 * EasyApp Mod Class
 */
class EasyAppMod
/**
 * Instantiates a new Easy app mod.
 *
 * @param context the context
 */
    (private val context: Context) {

    /**
     * Gets activity name.
     *
     * @return the activity name
     */
    val activityName: String
        get() = CheckValidityUtil.checkValidData(this.context.javaClass.simpleName)

    /**
     * Gets app name.
     *
     * @return the app name
     */
    val appName: String
        get() {
            val result: String?
            val pm = this.context.packageManager
            var ai: ApplicationInfo? = null
            try {
                ai = pm.getApplicationInfo(this.context.packageName, 0)
            } catch (e: NameNotFoundException) {
                if (EasyDeviceInfo.debuggable) {
                    Log.d(EasyDeviceInfo.nameOfLib, NAME_NOT_FOUND_EXCEPTION, e)
                }
            }

            result = if (ai != null) pm.getApplicationLabel(ai) as String else null
            return CheckValidityUtil.checkValidData(result!!)
        }

    /**
     * Gets app version.
     *
     * @return the app version
     */
    val appVersion: String
        get() {
            var result: String? = null
            try {
                result = this.context.packageManager.getPackageInfo(this.context.packageName, 0).versionName
            } catch (e: NameNotFoundException) {
                if (EasyDeviceInfo.debuggable) {
                    Log.e(EasyDeviceInfo.nameOfLib, NAME_NOT_FOUND_EXCEPTION, e)
                }
            }

            return CheckValidityUtil.checkValidData(result!!)
        }

    /**
     * Gets app version code.
     *
     * @return the app version code
     */
    val appVersionCode: String
        get() {
            var result: String? = null
            try {
                result = this.context.packageManager.getPackageInfo(this.context.packageName, 0).versionCode.toString()
            } catch (e: NameNotFoundException) {
                if (EasyDeviceInfo.debuggable) {
                    Log.e(EasyDeviceInfo.nameOfLib, NAME_NOT_FOUND_EXCEPTION, e)
                }
            }

            return CheckValidityUtil.checkValidData(result!!)
        }

    /**
     * Gets package name.
     *
     * @return the package name
     */
    val packageName: String
        get() = CheckValidityUtil.checkValidData(this.context.packageName)

    /**
     * Gets store.
     *
     * @return the store
     */
    val store: String
        get() {
            val result = this.context.packageManager.getInstallerPackageName(this.context.packageName)
            return CheckValidityUtil.checkValidData(result)
        }

    /**
     * Check if the app with the specified packagename is installed or not
     *
     * @param packageName the package name
     * @return the boolean
     */
    fun isAppInstalled(packageName: String): Boolean {
        return context.packageManager.getLaunchIntentForPackage(packageName) != null
    }

    /**
     * Is permission granted boolean.
     *
     * @param permission the permission
     * @return the boolean
     */
    fun isPermissionGranted(permission: String): Boolean {
        return context.checkCallingPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    companion object {

        private const val NAME_NOT_FOUND_EXCEPTION = "Name Not Found Exception"
    }
}
