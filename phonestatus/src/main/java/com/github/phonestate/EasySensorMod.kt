

package com.github.phonestate

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager

/**
 * The type Easy sensor mod.
 */
class EasySensorMod
/**
 * Instantiates a new Easy sensor mod.
 *
 * @param context the context
 */
    (context: Context) {

    private val sensorManager: SensorManager

    /**
     * Gets all sensors.
     *
     * @return the all sensors
     */
    val allSensors: List<Sensor>
        get() = this.sensorManager.getSensorList(Sensor.TYPE_ALL)

    init {
        this.sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
}
