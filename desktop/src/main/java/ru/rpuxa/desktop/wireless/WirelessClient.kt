package ru.rpuxa.desktop.wireless

import ru.rpuxa.internalserver.wifi.WifiClient
import ru.rpuxa.internalserver.wifi.WifiConnectManagerImpl
import ru.rpuxa.internalserver.wifi.WifiDevice
import ru.rpuxa.internalserver.wireless.AbstractWirelessDevice
import ru.rpuxa.internalserver.wireless.WirelessConnection

class WirelessClient : WirelessConnection(WifiClient(WifiConnectManagerImpl)) {

    override fun createWirelessDevice(device: WifiDevice): AbstractWirelessDevice =
            WirelessClientDevice(device)
}