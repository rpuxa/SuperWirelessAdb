package ru.rpuxa.internalserver.wireless

import ru.rpuxa.internalserver.wifi.WifiDevice

abstract class AbstractWirelessDevice(val wifiDevice: WifiDevice) : WirelessDevice {

    override lateinit var passport: Passport

    override var isAdbConnected = false

    abstract fun updateDevicePassport(): WirelessPromise<Passport>

    abstract fun checkAdbConnection(): WirelessPromise<Boolean>

    protected open fun setPassport() {
        @Suppress("LeakingThis")
        passport = updateDevicePassport().getAnswerBlocking() ?: run {
            wifiDevice.close()
            return
        }
    }

    protected fun <T> sendMessage(command: Int, data: Any? = null): WirelessPromise<T> {
        val promise = WirelessPromiseImpl<T>()
        wifiDevice.stream.sendMessage<T>(command, data)
                .onMessage(promise::answer)
                .onTimeout { promise.error(WirelessErrors.TIMEOUT) }

        return promise
    }

    override fun equals(other: Any?): Boolean {
        if (other === this) return true
        if (other !is AbstractWirelessDevice) return false

        return other.wifiDevice.lastAddressByte == wifiDevice.lastAddressByte
    }

    override fun hashCode(): Int {
        return wifiDevice.lastAddressByte
    }
}

