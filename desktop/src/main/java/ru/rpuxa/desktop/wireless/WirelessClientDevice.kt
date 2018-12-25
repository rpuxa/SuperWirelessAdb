package ru.rpuxa.desktop.wireless

import ru.rpuxa.internalserver.wifi.WifiDevice
import ru.rpuxa.internalserver.wireless.*

class WirelessClientDevice(device: WifiDevice) : WirelessDevice(device) {

    init {
        wifiDevice.stream.onMessage { command, data ->
            val ans: Any? = when (command) {
                CHECK_ADB -> Adb.check(wifiDevice.ip)
                CONNECT_ADB -> Adb.connect(wifiDevice.ip)
                GET_DEVICE_PASSPORT -> DesktopPassport.passport
                else -> throw IllegalStateException("Unknown command")
            }

            ans
        }
        wifiDevice.stream.open()

        setPassport()
    }

    override fun checkAdbConnection(): WirelessPromise<Boolean> =
            toBlockingPromise(Adb.check(wifiDevice.ip))

    override fun connectAdb(): WirelessPromise<Int> =
            toBlockingPromise(Adb.connect(wifiDevice.ip))

    override fun updateDevicePassport(): WirelessPromise<Passport> =
            sendMessage(GET_DEVICE_PASSPORT)

}