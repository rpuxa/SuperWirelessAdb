package ru.rpuxa.desktop.wireless

import ru.rpuxa.internalserver.stream.NothingReturn
import ru.rpuxa.internalserver.wifi.WifiDevice
import ru.rpuxa.internalserver.wireless.*
import kotlin.concurrent.thread

class WirelessClientDevice(device: WifiDevice) : AbstractWirelessDevice(device) {

    init {
        wifiDevice.stream.onMessage { command, data ->
            val ans: Any? = when (command) {
                CHECK_ADB -> Adb.check(wifiDevice.ip)
                CONNECT_ADB -> Adb.connect(wifiDevice.ip)
                DISCONNECT_ADB -> {
                    Adb.disconnect(wifiDevice.ip)
                    NothingReturn
                }
                GET_DEVICE_PASSPORT -> InternalServerController.passport
                FIX_10061 -> Adb.fix10061(wifiDevice.ip)
                else -> throw IllegalStateException("Unknown command $command - $data")
            }

            ans
        }
        wifiDevice.stream.open()

        setPassport()

        thread {
            var isAdbConnected: Boolean? = null
            var cycles = 0
            while (!wifiDevice.isClosed) {
                val check = Adb.check(wifiDevice.ip)
                if (isAdbConnected != check || cycles >= 10) {
                    isAdbConnected = check
                    cycles = 0
                    sendMessage<NothingReturn>(ADB_STATE, check)
                }
                cycles++
                Thread.sleep(500)
            }
        }
    }

    override fun setPassport() {
    }

    override fun checkAdbConnection(): WirelessPromise<Boolean> =
            fail()

    override fun connectAdb(): WirelessPromise<Int> =
            fail()

    override fun disconnectAdb(): WirelessPromise<NothingReturn> =
            fail()

    override fun updateDevicePassport(): WirelessPromise<Passport> =
            fail()

    override fun fixAdbError10061(): WirelessPromise<Boolean> =
            fail()

    private fun fail(): Nothing = throw UnsupportedOperationException("$javaClass")
}