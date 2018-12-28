package ru.rpuxa.internalserver.wireless

import ru.rpuxa.internalserver.wifi.WifiDevice

abstract class WirelessDevice(val wifiDevice: WifiDevice) {

    lateinit var passport: Passport


    fun setPassport() {
        @Suppress("LeakingThis")
        passport = updateDevicePassport().getAnswerBlocking()
    }

    abstract fun checkAdbConnection(): WirelessPromise<Boolean>

    abstract fun connectAdb(): WirelessPromise<Int>

    abstract fun updateDevicePassport(): WirelessPromise<Passport>

    protected fun <T> sendMessage(command: Int, data: Any? = null): WirelessPromise<T> {
        val promise = WirelessPromiseImpl<T>()
        wifiDevice.stream.sendMessage<T>(command, data)
                .onMessage(promise::answer)
                .onTimeout { promise.error(WirelessErrors.TIMEOUT) }

        return promise
    }


    protected fun <T> toBlockingPromise(value: T) =
            WirelessPromiseImpl<T>().apply {
                answer(value)
            }
}
