
package com.github.phonestate


/**
 * Easy device info class.
 */
object EasyDeviceInfo {

    /**
     * The Name.
     */
    val nameOfLib = "EasyDeviceInfo"

    /**
     * The constant debuggable.
     */
    var debuggable: Boolean = false

    /**
     * The Not found val.
     */
    var notFoundValue: String = "unknown"

    /**
     * Gets library version.
     *
     * @return the library version
     */
    val libraryVersion: String
        get() = EasyDeviceInfo.nameOfLib + " : v" + BuildConfig.VERSION_NAME + " [build-v" + BuildConfig.VERSION_CODE + ']'.toString()

    /**
     * Debug.
     */
    fun debug() {
        debuggable = true
    }

    /**
     * Instantiates a new Easy device info.
     *
     * @param notFoundVal the not found val
     * @param debugFlag   the debug flag
     */
    fun setConfigs(notFoundVal: String, debugFlag: Boolean) {
        EasyDeviceInfo.notFoundValue = notFoundVal
        debuggable = debugFlag
    }

    /**
     * Instantiates a new Easy device info.
     *
     * @param notFoundVal the not found val
     */
    fun setNotFoundVal(notFoundVal: String) {
        EasyDeviceInfo.notFoundValue = notFoundVal
    }
}
