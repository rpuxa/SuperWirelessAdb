package ru.rpuxa.superwirelessadb.wireless

import android.content.Context
import ru.rpuxa.internalserver.wifi.WifiDevice
import ru.rpuxa.internalserver.wireless.*
import ru.rpuxa.superwirelessadb.view.dataBase

class WirelessServerDevice(device: WifiDevice, private val myPassport: Passport) : WirelessDevice(device) {

    init {
        wifiDevice.stream.onMessage { command, data ->
            val ans: Any? = when (command) {
                GET_DEVICE_PASSPORT -> myPassport
                else -> throw IllegalStateException("Unknown command")
            }

            ans
        }

        wifiDevice.stream.open()

        setPassport()
    }

    override fun checkAdbConnection(): WirelessPromise<Boolean> =
            sendMessage(CHECK_ADB)

    override fun connectAdb(): WirelessPromise<Int> =
            sendMessage(CONNECT_ADB)

    override fun updateDevicePassport(): WirelessPromise<Passport> =
            sendMessage(GET_DEVICE_PASSPORT)


}