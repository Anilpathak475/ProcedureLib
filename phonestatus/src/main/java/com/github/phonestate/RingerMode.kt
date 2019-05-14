
package com.github.phonestate


import androidx.annotation.IntDef

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@IntDef(RingerMode.SILENT, RingerMode.NORMAL, RingerMode.VIBRATE)
@Retention(RetentionPolicy.CLASS)
annotation class RingerMode {
    companion object {

        const val SILENT = 0
        const val NORMAL = 1
        const val VIBRATE = 2
    }
}
