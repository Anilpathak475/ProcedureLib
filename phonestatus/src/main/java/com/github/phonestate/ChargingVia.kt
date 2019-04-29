

package com.github.phonestate

import androidx.annotation.IntDef




@IntDef(ChargingVia.USB, ChargingVia.AC, ChargingVia.WIRELESS, ChargingVia.UNKNOWN_SOURCE)
annotation class ChargingVia {
    companion object {

        const val USB = 0
        const val AC = 1
        const val WIRELESS = 2
        const  val UNKNOWN_SOURCE = 3
    }
}
