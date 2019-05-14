
package com.github.phonestate


import androidx.annotation.IntDef

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@IntDef(PhoneType.GSM, PhoneType.CDMA, PhoneType.NONE)
@Retention(RetentionPolicy.CLASS)
annotation class PhoneType {
    companion object {

        const val GSM = 0
        const val CDMA = 1
        const val NONE = 2
    }
}
