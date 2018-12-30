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
                GET_DEVICE_PASSPORT -> DesktopInternalServer.passport
                FIX_10061 -> Adb.fix10061(wifiDevice.ip)
                else -> throw IllegalStateException("Unknown command")
            }

            ans
        }
        wifiDevice.stream.open()

        setPassport()

        thread {
            var isAdbConnected: Boolean? = null
            while (!wifiDevice.isClosed) {
                val check = Adb.check(wifiDevice.ip)
                if (isAdbConnected != check) {
                    isAdbConnected = check
                    sendMessage<NothingReturn>(ADB_STATE, check)
                }

                Thread.sleep(500)
            }
        }
    }

    override fun checkAdbConnection(): WirelessPromise<Boolean> =
            toBlockingPromise(Adb.check(wifiDevice.ip))

    override fun connectAdb(): WirelessPromise<Int> =
            toBlockingPromise(Adb.connect(wifiDevice.ip))

    override fun disconnectAdb(): WirelessPromise<NothingReturn> =
            toBlockingPromise(run { Adb.disconnect(wifiDevice.ip); NothingReturn })

    override fun updateDevicePassport(): WirelessPromise<Passport> =
            sendMessage(GET_DEVICE_PASSPORT)

    override fun fixAdbError10061(): WirelessPromise<Boolean> =
            toBlockingPromise(Adb.fix10061(wifiDevice.ip))
}