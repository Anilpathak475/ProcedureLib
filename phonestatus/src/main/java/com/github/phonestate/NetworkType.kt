

package com.github.phonestate


import androidx.annotation.IntDef

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@IntDef(
    NetworkType.WIFI_WIFIMAX,
    NetworkType.CELLULAR_4G,
    NetworkType.CELLULAR_3G,
    NetworkType.CELLULAR_2G,
    NetworkType.CELLULAR_UNIDENTIFIED_GEN,
    NetworkType.CELLULAR_UNKNOWN,
    NetworkType.UNKNOWN
)
@Retention(RetentionPolicy.CLASS)
annotation class NetworkType {
    companion object {

        const val UNKNOWN = 0
        const val WIFI_WIFIMAX = 1
        const val CELLULAR_UNKNOWN = 2
        const val CELLULAR_2G = 3
        const val CELLULAR_3G = 4
        const val CELLULAR_4G = 5
        const val CELLULAR_UNIDENTIFIED_GEN = 6
    }
}
