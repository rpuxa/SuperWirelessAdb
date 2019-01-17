package ru.rpuxa.superwirelessadb.wireless

import ru.rpuxa.internalserver.wifi.WifiConnectManager
import ru.rpuxa.internalserver.wifi.WifiDevice
import ru.rpuxa.internalserver.wireless.AbstractWirelessDevice
import ru.rpuxa.internalserver.wireless.Passport
import ru.rpuxa.internalserver.wireless.WirelessConnection
import ru.rpuxa.superwirelessadb.wifi.WifiServer

class WirelessServer(private val passport: Passport, manager: WifiConnectManager) : WirelessConnection(WifiServer(manager)) {

    override fun createWirelessDevice(device: WifiDevice): AbstractWirelessDevice =
            WirelessServerDevice(device, passport)
}