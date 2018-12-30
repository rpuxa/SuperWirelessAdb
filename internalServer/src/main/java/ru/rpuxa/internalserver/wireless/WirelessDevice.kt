package ru.rpuxa.internalserver.wireless

import ru.rpuxa.internalserver.stream.NothingReturn

interface WirelessDevice {

    val passport: Passport

    val isAdbConnected: Boolean

    fun connectAdb(): WirelessPromise<Int>

    fun disconnectAdb(): WirelessPromise<NothingReturn>

    fun fixAdbError10061(): WirelessPromise<Boolean>

    override fun equals(other: Any?): Boolean
}