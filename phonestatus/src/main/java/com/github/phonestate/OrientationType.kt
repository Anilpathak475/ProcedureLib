
package com.github.phonestate


import androidx.annotation.IntDef

import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

@IntDef(OrientationType.PORTRAIT, OrientationType.LANDSCAPE, OrientationType.UNKNOWN)
@Retention(RetentionPolicy.CLASS)
annotation class OrientationType {
    companion object {

        const val PORTRAIT = 0
        const val LANDSCAPE = 1
        const val UNKNOWN = 2
    }
}
