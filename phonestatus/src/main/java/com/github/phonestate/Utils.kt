/*
 * Copyright (C) 2016 Jones Chi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.phonestate

import android.content.Context
import android.net.wifi.WifiManager
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * Created by yschi on 2015/5/27.
 */
object Utils {
    @Throws(IOException::class)
    fun getBroadcastAddress(context: Context): InetAddress? {
        val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val dhcp = wifi.dhcpInfo ?: return null

        val broadcast = dhcp.ipAddress and dhcp.netmask or dhcp.netmask.inv()
        val quads = ByteArray(4)
        for (k in 0..3) {
            quads[k] = (broadcast shr k * 8 and 0xFF).toByte()
        }
        return InetAddress.getByAddress(quads)
    }

    fun sendBroadcastMessage(context: Context, socket: DatagramSocket, port: Int, message: String): Boolean {

        try {
            val broadcastAddr = getBroadcastAddress(context) ?: return false
            socket.broadcast = true
            val packet = DatagramPacket(
                message.toByteArray(), message.length,
                broadcastAddr, port
            )
            socket.send(packet)
            return true
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return false
    }

}
