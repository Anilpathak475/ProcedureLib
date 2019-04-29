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

import com.github.anilpathak475.BuildConfig

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
