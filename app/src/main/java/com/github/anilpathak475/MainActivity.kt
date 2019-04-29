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

package com.github.anilpathak475

import android.Manifest
import android.Manifest.permission
import android.R.layout
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.hardware.Sensor
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.telephony.SubscriptionInfo
import android.telephony.SubscriptionManager
import android.util.ArrayMap
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.github.phonestate.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

    internal var adapter: ArrayAdapter<String>? = null

    @SuppressLint("MissingPermission", "SupportAnnotationUsage")
    @TargetApi(VERSION_CODES.LOLLIPOP_MR1)
    protected fun onCreate(savedInstanceState: Bundle) {
        super.onCreate(savedInstanceState)
        this.setContentView(R.layout.activity_main)

        //Data Array List of Info Object
        val data = ArrayList<String>()

        //Add Data
        val deviceDataMap = ArrayMap<String, String>()

        // Setup the value to be returned when result is either not found or invalid/null
        EasyDeviceInfo.setNotFoundVal("na")
        // Enable Debugging when in Debug build
        if (BuildConfig.DEBUG) {
            EasyDeviceInfo.debug()
        }

        // Library Info
        data.add(EasyDeviceInfo.libraryVersion)

        // ID Mod
        val easyIdMod = EasyIdMod(this)

        val emailIds = easyIdMod.accounts
        val emailString = StringBuilder()
        if (emailIds!!.isNotEmpty()) {
            for (e in emailIds!!) {
                emailString.append(e).append('\n')
            }
        } else {
            emailString.append('-')
        }

        // Config Mod
        val easyConfigMod = EasyConfigMod(this)
        deviceDataMap["Time (ms)"] = String(easyConfigMod.time)
        deviceDataMap.put("Formatted Time (24Hrs)", easyConfigMod.getFormattedTime())
        deviceDataMap.put("UpTime (ms)", String.valueOf(easyConfigMod.getUpTime()))
        deviceDataMap.put("Formatted Up Time (24Hrs)", easyConfigMod.getFormattedUpTime())
        deviceDataMap.put("Date", String.valueOf(easyConfigMod.getCurrentDate()))
        deviceDataMap.put("Formatted Date", easyConfigMod.getFormattedDate())
        deviceDataMap.put("SD Card available", String.valueOf(easyConfigMod.hasSdCard()))
        deviceDataMap.put("Running on emulator", String.valueOf(easyConfigMod.isRunningOnEmulator()))

        @RingerMode val ringermode = easyConfigMod.getDeviceRingerMode()
        when (ringermode) {
            RingerMode.NORMAL -> deviceDataMap.put(this.getString(string.ringer_mode), "normal")
            RingerMode.VIBRATE -> deviceDataMap.put(this.getString(string.ringer_mode), "vibrate")
            RingerMode.SILENT -> deviceDataMap.put(this.getString(string.ringer_mode), "silent")
            else -> deviceDataMap.put(this.getString(string.ringer_mode), "silent")
        }

        // Fingerprint Mod
        val easyFingerprintMod = EasyFingerprintMod(this)
        deviceDataMap.put(
            "Is Fingerprint Sensor present?",
            String.valueOf(easyFingerprintMod.isFingerprintSensorPresent())
        )
        deviceDataMap.put(
            "Are fingerprints enrolled",
            String.valueOf(easyFingerprintMod.areFingerprintsEnrolled())
        )

        // Sensor Mod
        val easySensorMod = EasySensorMod(this)
        val list = easySensorMod.getAllSensors()
        for (s in list) {
            if (s != null) {
                val stringBuilder = ("\nVendor : "
                        + s!!.getVendor()
                        + '\n'.toString()
                        + "Version : "
                        + s!!.getVersion()
                        + '\n'.toString()
                        + "Power : "
                        + s!!.getPower()
                        + '\n'.toString()
                        + "Resolution : "
                        + s!!.getResolution()
                        + '\n'.toString()
                        + "Max Range : "
                        + s!!.getMaximumRange())
                deviceDataMap.put("Sensor Name - " + s!!.getName(), stringBuilder)
            } else {
                deviceDataMap.put("Sensor", "N/A")
            }
        }

        // SIM Mod
        val easySimMod = EasySimMod(this)
        deviceDataMap.put("IMSI", easySimMod.getIMSI())
        deviceDataMap.put("SIM Serial Number", easySimMod.getSIMSerial())
        deviceDataMap.put("Country", easySimMod.getCountry())
        deviceDataMap.put("Carrier", easySimMod.getCarrier())
        deviceDataMap.put("SIM Network Locked", String.valueOf(easySimMod.isSimNetworkLocked()))
        deviceDataMap.put("Is Multi SIM", String.valueOf(easySimMod.isMultiSim()))
        deviceDataMap.put("Number of active SIM", String.valueOf(easySimMod.getNumberOfActiveSim()))

        if (easySimMod.isMultiSim()) {
            val activeMultiSimInfo = easySimMod.getActiveMultiSimInfo()
            if (activeMultiSimInfo != null) {
                val stringBuilder = StringBuilder()
                for (i in activeMultiSimInfo!!.indices) {
                    stringBuilder.append("\nSIM ")
                        .append(i)
                        .append(" Info")
                        .append("\nPhone Number :")
                        .append(activeMultiSimInfo!!.get(i).getNumber())
                        .append('\n')
                        .append("Carrier Name :")
                        .append(activeMultiSimInfo!!.get(i).getCarrierName())
                        .append('\n')
                        .append("Country :")
                        .append(activeMultiSimInfo!!.get(i).getCountryIso())
                        .append('\n')
                        .append("Roaming :")
                        .append(activeMultiSimInfo!!.get(i).getDataRoaming() == SubscriptionManager.DATA_ROAMING_ENABLE)
                        .append('\n')
                        .append("Display Name :")
                        .append(activeMultiSimInfo!!.get(i).getDisplayName())
                        .append('\n')
                        .append("Sim Slot  :")
                        .append(activeMultiSimInfo!!.get(i).getSimSlotIndex())
                        .append('\n')
                }
                deviceDataMap.put("Multi SIM Info", stringBuilder.toString())
            }
        }

        // Device Mod
        val easyDeviceMod = EasyDeviceMod(this)
        deviceDataMap.put("Language", easyDeviceMod.getLanguage())
        deviceDataMap.put("Android ID", easyIdMod.getAndroidID())
        deviceDataMap.put("IMEI", easyDeviceMod.getIMEI())
        deviceDataMap.put("User-Agent", easyIdMod.getUA())
        deviceDataMap.put("GSF ID", easyIdMod.getGSFID())
        deviceDataMap.put("Pseudo ID", easyIdMod.getPseudoUniqueID())
        deviceDataMap.put("Device Serial", easyDeviceMod.getSerial())
        deviceDataMap.put("Manufacturer", easyDeviceMod.getManufacturer())
        deviceDataMap.put("Model", easyDeviceMod.getModel())
        deviceDataMap.put("OS Codename", easyDeviceMod.getOSCodename())
        deviceDataMap.put("OS Version", easyDeviceMod.getOSVersion())
        deviceDataMap.put("Display Version", easyDeviceMod.getDisplayVersion())
        deviceDataMap.put("Phone Number", easyDeviceMod.getPhoneNo())
        deviceDataMap.put("Radio Version", easyDeviceMod.getRadioVer())
        deviceDataMap.put("Product ", easyDeviceMod.getProduct())
        deviceDataMap.put("Device", easyDeviceMod.getDevice())
        deviceDataMap.put("Board", easyDeviceMod.getBoard())
        deviceDataMap.put("Hardware", easyDeviceMod.getHardware())
        deviceDataMap.put("BootLoader", easyDeviceMod.getBootloader())
        deviceDataMap.put("Device Rooted", String.valueOf(easyDeviceMod.isDeviceRooted()))
        deviceDataMap.put("Fingerprint", easyDeviceMod.getFingerprint())
        deviceDataMap.put("Build Brand", easyDeviceMod.getBuildBrand())
        deviceDataMap.put("Build Host", easyDeviceMod.getBuildHost())
        deviceDataMap.put("Build Tag", easyDeviceMod.getBuildTags())
        deviceDataMap.put("Build Time", String.valueOf(easyDeviceMod.getBuildTime()))
        deviceDataMap.put("Build User", easyDeviceMod.getBuildUser())
        deviceDataMap.put("Build Version Release", easyDeviceMod.getBuildVersionRelease())
        deviceDataMap.put("Screen Display ID", easyDeviceMod.getScreenDisplayID())
        deviceDataMap.put("Build Version Codename", easyDeviceMod.getBuildVersionCodename())
        deviceDataMap.put("Build Version Increment", easyDeviceMod.getBuildVersionIncremental())
        deviceDataMap.put("Build Version SDK", String.valueOf(easyDeviceMod.getBuildVersionSDK()))
        deviceDataMap.put("Build ID", easyDeviceMod.getBuildID())

        @DeviceType val deviceType = easyDeviceMod.getDeviceType(this)
        when (deviceType) {
            DeviceType.WATCH -> deviceDataMap.put(this.getString(string.device_type), "watch")
            DeviceType.PHONE -> deviceDataMap.put(this.getString(string.device_type), "phone")
            DeviceType.PHABLET -> deviceDataMap.put(this.getString(string.device_type), "phablet")
            DeviceType.TABLET -> deviceDataMap.put(this.getString(string.device_type), "tablet")
            DeviceType.TV -> deviceDataMap.put(this.getString(string.device_type), "tv")
            else -> {
            }
        }//do nothing

        @PhoneType val phoneType = easyDeviceMod.getPhoneType()
        when (phoneType) {

            PhoneType.CDMA -> deviceDataMap.put(this.getString(string.phone_type), "CDMA")
            PhoneType.GSM -> deviceDataMap.put(this.getString(string.phone_type), "GSM")
            PhoneType.NONE -> deviceDataMap.put(this.getString(string.phone_type), "None")
            else -> deviceDataMap.put(this.getString(string.phone_type), "Unknown")
        }

        @OrientationType val orientationType = easyDeviceMod.getOrientation(this)
        when (orientationType) {
            OrientationType.LANDSCAPE -> deviceDataMap.put(this.getString(string.orientation), "Landscape")
            OrientationType.PORTRAIT -> deviceDataMap.put(this.getString(string.orientation), "Portrait")
            OrientationType.UNKNOWN -> deviceDataMap.put(this.getString(string.orientation), "Unknown")
            else -> deviceDataMap.put(this.getString(string.orientation), "Unknown")
        }

        // App Mod
        val easyAppMod = EasyAppMod(this)
        deviceDataMap.put("Installer Store", easyAppMod.getStore())
        deviceDataMap.put("App Name", easyAppMod.getAppName())
        deviceDataMap.put("Package Name", easyAppMod.getPackageName())
        deviceDataMap.put("Activity Name", easyAppMod.getActivityName())
        deviceDataMap.put("App version", easyAppMod.getAppVersion())
        deviceDataMap.put("App versioncode", easyAppMod.getAppVersionCode())
        deviceDataMap.put(
            "Does app have Camera permission?",
            String.valueOf(easyAppMod.isPermissionGranted(permission.CAMERA))
        )

        //Network Mod
        val easyNetworkMod = EasyNetworkMod(this)
        deviceDataMap.put("WIFI MAC Address", easyNetworkMod.getWifiMAC())
        deviceDataMap.put("WIFI LinkSpeed", easyNetworkMod.getWifiLinkSpeed())
        deviceDataMap.put("WIFI SSID", easyNetworkMod.getWifiSSID())
        deviceDataMap.put("WIFI BSSID", easyNetworkMod.getWifiBSSID())
        deviceDataMap.put("IPv4 Address", easyNetworkMod.getIPv4Address())
        deviceDataMap.put("IPv6 Address", easyNetworkMod.getIPv6Address())
        deviceDataMap.put("Network Available", String.valueOf(easyNetworkMod.isNetworkAvailable()))
        deviceDataMap.put("Wi-Fi enabled", String.valueOf(easyNetworkMod.isWifiEnabled()))

        @NetworkType val networkType = easyNetworkMod.getNetworkType()

        when (networkType) {
            NetworkType.CELLULAR_UNKNOWN -> deviceDataMap.put(this.getString(string.network_type), "Cellular Unknown")
            NetworkType.CELLULAR_UNIDENTIFIED_GEN -> deviceDataMap.put(
                this.getString(string.network_type),
                "Cellular Unidentified Generation"
            )
            NetworkType.CELLULAR_2G -> deviceDataMap.put(this.getString(string.network_type), "Cellular 2G")
            NetworkType.CELLULAR_3G -> deviceDataMap.put(this.getString(string.network_type), "Cellular 3G")
            NetworkType.CELLULAR_4G -> deviceDataMap.put(this.getString(string.network_type), "Cellular 4G")

            NetworkType.WIFI_WIFIMAX -> deviceDataMap.put(this.getString(string.network_type), "Wifi/WifiMax")
            NetworkType.UNKNOWN -> deviceDataMap.put(this.getString(string.network_type), "Unknown")
            else -> deviceDataMap.put(this.getString(string.network_type), "Unknown")
        }

        // Battery Mod
        val easyBatteryMod = EasyBatteryMod(this)
        deviceDataMap.put("Battery Percentage", String.valueOf(easyBatteryMod.getBatteryPercentage()) + '%')
        deviceDataMap.put("Is device charging", String.valueOf(easyBatteryMod.isDeviceCharging()))
        deviceDataMap.put("Battery present", String.valueOf(easyBatteryMod.isBatteryPresent()))
        deviceDataMap.put("Battery technology", String.valueOf(easyBatteryMod.getBatteryTechnology()))
        deviceDataMap.put(
            "Battery temperature",
            easyBatteryMod.getBatteryTemperature() + " deg C"
        )
        deviceDataMap.put(
            "Battery voltage",
            easyBatteryMod.getBatteryVoltage() + " mV"
        )

        @BatteryHealth val batteryHealth = easyBatteryMod.getBatteryHealth()
        when (batteryHealth) {
            BatteryHealth.GOOD -> deviceDataMap.put("Battery health", "Good")
            BatteryHealth.HAVING_ISSUES -> deviceDataMap.put("Battery health", "Having issues")
            else -> deviceDataMap.put("Battery health", "Having issues")
        }

        @ChargingVia val isChargingVia = easyBatteryMod.getChargingSource()
        when (isChargingVia) {
            ChargingVia.AC -> deviceDataMap.put(this.getString(string.device_charging_via), "AC")
            ChargingVia.USB -> deviceDataMap.put(this.getString(string.device_charging_via), "USB")
            ChargingVia.WIRELESS -> deviceDataMap.put(this.getString(string.device_charging_via), "Wireless")
            ChargingVia.UNKNOWN_SOURCE -> deviceDataMap.put(
                this.getString(R.string.device_charging_via),
                "Unknown Source"
            )
            else -> deviceDataMap.put(this.getString(R.string.device_charging_via), "Unknown Source")
        }

        //Bluetooth Mod
        val easyBluetoothMod = EasyBluetoothMod(this)
        deviceDataMap.put("BT MAC Address", easyBluetoothMod.getBluetoothMAC())

        // Display Mod
        val easyDisplayMod = EasyDisplayMod(this)
        deviceDataMap.put("Display Resolution", easyDisplayMod.getResolution())
        deviceDataMap.put("Screen Density", easyDisplayMod.getDensity())
        deviceDataMap.put("Screen Size", String.valueOf(easyDisplayMod.getPhysicalSize()))
        deviceDataMap.put(
            "Screen Refresh rate",
            easyDisplayMod.getRefreshRate() + " Hz"
        )

        deviceDataMap.put("Email ID", emailString.toString())

        // Location Mod
        val easyLocationMod = EasyLocationMod(this)
        val l = easyLocationMod.getLatLong()
        val lat = l[0].toString()
        val lon = l[1].toString()
        deviceDataMap.put("Latitude", lat)
        deviceDataMap.put("Longitude", lon)

        // Memory Mod
        val easyMemoryMod = EasyMemoryMod(this)
        deviceDataMap.put(
            "Total RAM",
            easyMemoryMod.convertToGb(easyMemoryMod.getTotalRAM()) + " Gb"
        )
        deviceDataMap.put(
            "Available Internal Memory",
            easyMemoryMod.convertToGb(easyMemoryMod.getAvailableInternalMemorySize()) + " Gb"
        )
        deviceDataMap.put(
            "Available External Memory",
            easyMemoryMod.convertToGb(easyMemoryMod.getAvailableExternalMemorySize()) + " Gb"
        )
        deviceDataMap.put(
            "Total Internal Memory",
            easyMemoryMod.convertToGb(easyMemoryMod.getTotalInternalMemorySize()) + " Gb"
        )
        deviceDataMap.put(
            "Total External memory",
            easyMemoryMod.convertToGb(easyMemoryMod.getTotalExternalMemorySize()) + " Gb"
        )

        // CPU Mod
        val easyCpuMod = EasyCpuMod()
        deviceDataMap.put("Supported ABIS", easyCpuMod.getStringSupportedABIS())
        deviceDataMap.put("Supported 32 bit ABIS", easyCpuMod.getStringSupported32bitABIS())
        deviceDataMap.put("Supported 64 bit ABIS", easyCpuMod.getStringSupported64bitABIS())

        // NFC Mod
        val easyNfcMod = EasyNfcMod(this)
        deviceDataMap.put("is NFC present", String.valueOf(easyNfcMod.isNfcPresent()))
        deviceDataMap.put("is NFC enabled", String.valueOf(easyNfcMod.isNfcEnabled()))

        for (key in deviceDataMap.keySet()) {
            data.add(key + " : " + deviceDataMap.get(key))
        }

        val lv = this.findViewById(R.id.listview)
        this.adapter = ArrayAdapter(this, layout.simple_list_item_1, data)
        lv.setAdapter(this.adapter)
    }
}
