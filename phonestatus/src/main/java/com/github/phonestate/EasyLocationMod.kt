
package com.github.phonestate

import android.Manifest
import android.Manifest.permission
import android.content.Context
import android.location.Location
import android.location.LocationManager
import androidx.annotation.RequiresPermission


/**
 * EasyLocation Mod Class
 *
 * You need to declare the below permission in the manifest file to use this properly
 *
 * For Network based location
 * <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"></uses-permission>
 *
 * For more accurate location updates via GPS and network both
 * <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"></uses-permission>
 */

class EasyLocationMod
/**
 * Instantiates a new Easy location mod.
 *
 * @param context the context
 */
    (context: Context) {

    private val hasCoarseLocationPermission: Boolean = PermissionUtil.hasPermission(context, permission.ACCESS_COARSE_LOCATION)

    private val hasFineLocationPermission: Boolean = PermissionUtil.hasPermission(context, permission.ACCESS_FINE_LOCATION)

    private var lm: LocationManager? = null

    /**
     * Get lat long double [ ].
     *
     * @return the double [ ]
     */
    val latLong: DoubleArray
        @RequiresPermission(anyOf = [permission.ACCESS_COARSE_LOCATION, permission.ACCESS_FINE_LOCATION])
        get() {
            val gps = DoubleArray(2)
            gps[0] = 0.0
            gps[1] = 0.0

            if (this.hasCoarseLocationPermission && this.lm!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                val lastKnownLocation = this.lm!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                if (lastKnownLocation != null) {
                    gps[0] = lastKnownLocation.latitude
                    gps[1] = lastKnownLocation.longitude
                }
            } else if (this.hasFineLocationPermission) {
                val isGPSEnabled = this.lm!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
                val isNetworkEnabled = this.lm!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

                var lastKnownLocationNetwork: Location? = null
                var lastKnownLocationGps: Location? = null
                var betterLastKnownLocation: Location? = null

                if (isNetworkEnabled) {
                    lastKnownLocationNetwork = this.lm!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                }

                if (isGPSEnabled) {
                    lastKnownLocationGps = this.lm!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                }
                if (lastKnownLocationGps != null && lastKnownLocationNetwork != null) {
                    betterLastKnownLocation = this.getBetterLocation(lastKnownLocationGps, lastKnownLocationNetwork)
                }

                if (betterLastKnownLocation == null) {
                    betterLastKnownLocation = this.lm!!.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
                }

                if (betterLastKnownLocation != null) {
                    gps[0] = betterLastKnownLocation.latitude
                    gps[1] = betterLastKnownLocation.longitude
                }
            }
            return gps
        }

    init {

        if (this.hasCoarseLocationPermission || this.hasFineLocationPermission) {
            this.lm = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }

    private fun getBetterLocation(location1: Location, location2: Location): Location {
        return if (location1.accuracy >= location2.accuracy) location1 else location2
    }
}
