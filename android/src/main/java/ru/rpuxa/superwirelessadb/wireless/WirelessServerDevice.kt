package ru.rpuxa.superwirelessadb.wireless

import ru.rpuxa.internalserver.stream.NothingReturn
import ru.rpuxa.internalserver.wifi.WifiDevice
import ru.rpuxa.internalserver.wireless.*

class WirelessServerDevice(device: WifiDevice, private val myPassport: Passport) : AbstractWirelessDevice(device) {

    init {
        wifiDevice.stream.onMessage { command, data ->
            val ans: Any? = when (command) {
                GET_DEVICE_PASSPORT -> myPassport
                ADB_STATE -> {
                    isAdbConnected = data as Boolean
                    NothingReturn
                }
                else -> throw IllegalStateException("Unknown command $command, $data")
            }

            ans
        }

        wifiDevice.stream.open()

        setPassport()
    }

    override fun disconnectAdb(): WirelessPromise<NothingReturn> =
            sendMessage(DISCONNECT_ADB)

    override fun checkAdbConnection(): WirelessPromise<Boolean> =
            sendMessage(CHECK_ADB)

    override fun connectAdb(): WirelessPromise<Int> =
            sendMessage(CONNECT_ADB)

    override fun updateDevicePassport(): WirelessPromise<Passport> =
            sendMessage(GET_DEVICE_PASSPORT)

    override fun fixAdbError10061(): WirelessPromise<Boolean> =
            sendMessage(FIX_10061)
}