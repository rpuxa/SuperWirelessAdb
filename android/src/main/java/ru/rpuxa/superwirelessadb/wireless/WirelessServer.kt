package ru.rpuxa.superwirelessadb.wireless

import ru.rpuxa.internalserver.wifi.WifiConnectManagerImpl
import ru.rpuxa.internalserver.wifi.WifiDevice
import ru.rpuxa.internalserver.wireless.Passport
import ru.rpuxa.internalserver.wireless.WirelessConnection
import ru.rpuxa.internalserver.wireless.WirelessDevice
import ru.rpuxa.superwirelessadb.wifi.WifiServer

class WirelessServer(val passport: Passport) : WirelessConnection(WifiServer(WifiConnectManagerImpl)) {

    override fun createWirelessDevice(device: WifiDevice): WirelessDevice =
            WirelessServerDevice(device, passport)
}