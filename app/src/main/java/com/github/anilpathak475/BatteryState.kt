package com.procedure.phonestate

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Parcel
import android.os.Parcelable

class BatteryState(context: Context) {
    private val batteryStatus: Intent? = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let { it ->
        context.registerReceiver(null, it)
    }

    fun getBatteryStats(): BatteryStats {
        val status: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) ?: -1
        val isCharging: Boolean = status == BatteryManager.BATTERY_STATUS_CHARGING
                || status == BatteryManager.BATTERY_STATUS_FULL
        val chargePlug: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) ?: -1
        val usbCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
        val acCharge: Boolean = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
        val batteryPct: Float? = batteryStatus?.let { intent ->
            val level: Int = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            (level / scale.toFloat()) * 100
        }
        return BatteryStats(status, isCharging, chargePlug, usbCharge, acCharge, batteryPct!!)
    }

    data class BatteryStats(
        val status: Int,
        val isCharging: Boolean,
        val chargePlug: Int,
        val usbCharge: Boolean,
        val acCharge: Boolean,
        val percentage: Float
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readInt(),
            parcel.readByte() != 0.toByte(),
            parcel.readByte() != 0.toByte(),
            parcel.readFloat()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeInt(status)
            parcel.writeByte(if (isCharging) 1 else 0)
            parcel.writeInt(chargePlug)
            parcel.writeByte(if (usbCharge) 1 else 0)
            parcel.writeByte(if (acCharge) 1 else 0)
            parcel.writeFloat(percentage)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<BatteryStats> {
            override fun createFromParcel(parcel: Parcel): BatteryStats {
                return BatteryStats(parcel)
            }

            override fun newArray(size: Int): Array<BatteryStats?> {
                return arrayOfNulls(size)
            }
        }
    }

}