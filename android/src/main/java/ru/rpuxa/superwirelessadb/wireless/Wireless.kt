package ru.rpuxa.superwirelessadb.wireless

import android.content.Context
import ru.rpuxa.internalserver.wireless.WirelessConnection
import ru.rpuxa.internalserver.wireless.WirelessDevice
import ru.rpuxa.superwirelessadb.other.dataBase

object Wireless {

    fun getDeviceById(deviceId: Long): WirelessDevice? =
            devices.find { it.passport.id == deviceId }

    fun myOnlineDevices(context: Context): List<WirelessDevice> {
        val myDevices = context.dataBase.myDevices
        return devices.filter { device -> myDevices.any { it.id == device.passport.id } }
    }

    lateinit var server: WirelessConnection

    val devices get() = server.devices
}