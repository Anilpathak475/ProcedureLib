
package com.github.phonestate


import androidx.annotation.IntDef
import com.github.phonestate.BatteryHealth.Companion.GOOD
import com.github.phonestate.BatteryHealth.Companion.HAVING_ISSUES


@IntDef(GOOD, HAVING_ISSUES)
annotation class BatteryHealth {
    companion object {
        const val HAVING_ISSUES = 0
        const val GOOD = 1
    }
}
