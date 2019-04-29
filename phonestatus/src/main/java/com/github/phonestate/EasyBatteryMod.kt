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

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

/**
 * EasyBattery Mod Class
 */
@BatteryHealth
class EasyBatteryMod
/**
 * Instantiates a new Easy battery mod.
 *
 * @param context the context
 */
    (private val context: Context) {

    /**
     * Gets battery health.
     *
     * @return the battery health
     */
    val batteryHealth: Int
        @BatteryHealth
        get() {
            var health = BatteryHealth.HAVING_ISSUES
            val batteryStatus = this.batteryStatusIntent
            if (batteryStatus != null) {
                health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, 0)
                health =
                    if (health == BatteryManager.BATTERY_HEALTH_GOOD) BatteryHealth.GOOD else BatteryHealth.HAVING_ISSUES
            }
            return health
        }

    /**
     * Gets battery percentage.
     *
     * @return the battery percentage
     */
    val batteryPercentage: Int
        get() {
            var percentage = 0
            val batteryStatus = this.batteryStatusIntent
            if (batteryStatus != null) {
                val level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
                val scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
                percentage = (level / scale.toFloat() * 100).toInt()
            }

            return percentage
        }

    /**
     * Gets battery technology.
     *
     * @return the battery technology
     */
    val batteryTechnology: String
        get() = CheckValidityUtil.checkValidData(
            this.batteryStatusIntent!!.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)
        )

    /**
     * Gets battery temprature.
     *
     * @return the battery temprature
     */
    val batteryTemperature: Float
        get() {
            var temp = 0.0f
            val batteryStatus = this.batteryStatusIntent
            if (batteryStatus != null) {
                temp = (batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0) / 10.0).toFloat()
            }
            return temp
        }

    /**
     * Gets battery voltage.
     *
     * @return the battery voltage
     */
    val batteryVoltage: Int
        get() {
            var volt = 0
            val batteryStatus = this.batteryStatusIntent
            if (batteryStatus != null) {
                volt = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, 0)
            }
            return volt
        }

    /**
     * Gets charging source.
     *
     * @return the charging source
     */
    val chargingSource: Int
        @ChargingVia
        get() {
            val batteryStatus = this.batteryStatusIntent
            val chargePlug = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)

            when (chargePlug) {
                BatteryManager.BATTERY_PLUGGED_AC -> return ChargingVia.AC
                BatteryManager.BATTERY_PLUGGED_USB -> return ChargingVia.USB
                BatteryManager.BATTERY_PLUGGED_WIRELESS -> return ChargingVia.WIRELESS
                else -> return ChargingVia.UNKNOWN_SOURCE
            }
        }

    /**
     * Is battery present boolean.
     *
     * @return the boolean
     */
    val isBatteryPresent: Boolean
        get() = batteryStatusIntent!!.extras != null && this.batteryStatusIntent!!.extras!!
            .getBoolean(BatteryManager.EXTRA_PRESENT)

    /**
     * Is device charging boolean.
     *
     * @return is battery charging boolean
     */
    val isDeviceCharging: Boolean
        get() {
            val batteryStatus = this.batteryStatusIntent
            val status = batteryStatus!!.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            return status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL
        }

    private val batteryStatusIntent: Intent?
        get() {
            val batFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            return this.context.registerReceiver(null, batFilter)
        }
}