package ru.rpuxa.superwirelessadb.wireless

import ru.rpuxa.internalserver.wifi.WifiConnectManagerImpl
import ru.rpuxa.internalserver.wifi.WifiDevice
import ru.rpuxa.internalserver.wireless.AbstractWirelessDevice
import ru.rpuxa.internalserver.wireless.Passport
import ru.rpuxa.internalserver.wireless.WirelessConnection
import ru.rpuxa.superwirelessadb.wifi.WifiServer

class WirelessServer(private val passport: Passport) : WirelessConnection(WifiServer(WifiConnectManagerImpl)) {

    override fun createWirelessDevice(device: WifiDevice): AbstractWirelessDevice =
            WirelessServerDevice(device, passport)
}