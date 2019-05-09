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

import android.os.Build
import android.os.Build.VERSION


/**
 * EasyCpu Mod Class
 */
class EasyCpuMod {

    /**
     * Gets string supported 32 bit abis.
     *
     * @return the string supported 32 bit abis
     */
    val stringSupported32bitABIS: String
        get() {
            var result: String? = null

            if (VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val supportedABIS = Build.SUPPORTED_32_BIT_ABIS

                val supportedABIString = StringBuilder()
                if (supportedABIS.size > 0) {
                    for (abis in supportedABIS) {
                        supportedABIString.append(abis).append('_')
                    }
                    supportedABIString.deleteCharAt(supportedABIString.lastIndexOf("_"))
                } else {
                    supportedABIString.append("")
                }

                result = supportedABIString.toString()
            }

            return CheckValidityUtil.checkValidData(
                CheckValidityUtil.handleIllegalCharacterInResult(result!!)!!
            )
        }

    /**
     * Gets string supported 64 bit abis.
     *
     * @return the string supported 64 bit abis
     */
    val stringSupported64bitABIS: String
        get() {
            var result: String? = null
            if (VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val supportedABIS = Build.SUPPORTED_64_BIT_ABIS

                val supportedABIString = StringBuilder()
                if (supportedABIS.isNotEmpty()) {
                    for (abis in supportedABIS) {
                        supportedABIString.append(abis).append('_')
                    }
                    supportedABIString.deleteCharAt(supportedABIString.lastIndexOf("_"))
                } else {
                    supportedABIString.append("")
                }
                result = supportedABIString.toString()
            }
            return CheckValidityUtil.checkValidData(
                CheckValidityUtil.handleIllegalCharacterInResult(result!!)!!
            )
        }

    /**
     * Gets string supported abis.
     *
     * @return the string supported abis
     */
    val stringSupportedABIS: String
        get() {
            var result: String? = null
            if (VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val supportedABIS = Build.SUPPORTED_ABIS
                val supportedABIString = StringBuilder()
                if (supportedABIS.isNotEmpty()) {
                    for (abis in supportedABIS) {
                        supportedABIString.append(abis).append('_')
                    }
                    supportedABIString.deleteCharAt(supportedABIString.lastIndexOf("_"))
                } else {
                    supportedABIString.append("")
                }
                result = supportedABIString.toString()
            }
            return CheckValidityUtil.checkValidData(
                CheckValidityUtil.handleIllegalCharacterInResult(result!!)!!
            )
        }

    /**
     * Get supported 32 bit abis string [ ].
     *
     * @return the string [ ]
     */
    val supported32bitABIS: Array<String>
        get() {
            var result = arrayOf(EasyDeviceInfo.notFoundValue)
            if (VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                result = Build.SUPPORTED_32_BIT_ABIS
            }
            return CheckValidityUtil.checkValidListData(result)
        }

    /**
     * Get supported 64 bit abis string [ ].
     *
     * @return the string [ ]
     */
    val supported64bitABIS: Array<String>
        get() {
            var result = arrayOf(EasyDeviceInfo.notFoundValue)
            if (VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                result = Build.SUPPORTED_64_BIT_ABIS
            }
            return CheckValidityUtil.checkValidListData(result)
        }

    /**
     * Get supported abis string [ ].
     *
     * @return the string [ ]
     */
    val supportedABIS: Array<String>
        get() {
            var result = arrayOf(EasyDeviceInfo.notFoundValue)
            if (VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                result = Build.SUPPORTED_ABIS
            }
            return CheckValidityUtil.checkValidListData(result)
        }
}
