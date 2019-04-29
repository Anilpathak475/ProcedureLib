

package com.github.phonestate


/**
 * Check Validity Util Class
 */
internal object CheckValidityUtil {

    /**
     * Check valid data string.
     *
     * @param data the data
     * @return the string
     */
    fun checkValidData(data: String): String {
        var tempData: String? = data
        if (tempData == null || tempData.isEmpty()) {
            tempData = EasyDeviceInfo.notFoundVal
        }
        return tempData
    }

    /**
     * Check valid data string [ ].
     *
     * @param data the data
     * @return the string [ ]
     */
    fun checkValidData(data: Array<String>): Array<String> {
        var tempData: Array<String>? = data
        if (tempData == null || tempData.isEmpty()) {
            tempData = arrayOf(EasyDeviceInfo.notFoundVal)
        }
        return tempData
    }

    /**
     * Handle illegal character in result string.
     *
     * @param result the result
     * @return the string
     */
    fun handleIllegalCharacterInResult(result: String): String? {
        var tempResult: String? = result
        if (tempResult != null && tempResult.contains(" ")) {
            tempResult = tempResult.replace(" ".toRegex(), "_")
        }
        return tempResult!!
    }
}// private constructor for utility class
