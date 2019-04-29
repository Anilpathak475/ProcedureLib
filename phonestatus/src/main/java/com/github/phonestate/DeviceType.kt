
package com.github.phonestate


import androidx.annotation.IntDef
import com.github.phonestate.DeviceType.Companion.PHABLET
import com.github.phonestate.DeviceType.Companion.PHONE
import com.github.phonestate.DeviceType.Companion.TABLET
import com.github.phonestate.DeviceType.Companion.TV
import com.github.phonestate.DeviceType.Companion.WATCH

import java.lang.annotation.RetentionPolicy
import kotlin.annotation.Retention

@IntDef(DeviceType.WATCH, DeviceTypePHONE, PHABLET, TABLET, TV)
@Retention(AnnotationTarget.CLASS)
annotation class DeviceType {
    companion object {

        const val WATCH = 0
        const val PHONE = 1
        const val PHABLET = 2
        const val TABLET = 3
        const val TV = 4
    }
}
