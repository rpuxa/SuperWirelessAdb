package ru.rpuxa.desktop.wireless

import ru.rpuxa.desktop.Actions
import ru.rpuxa.desktop.wifi.WifiClient
import ru.rpuxa.internalserver.wifi.WifiConnectManagerImpl
import ru.rpuxa.internalserver.wifi.WifiDevice
import ru.rpuxa.internalserver.wireless.AbstractWirelessDevice
import ru.rpuxa.internalserver.wireless.WirelessConnection

class WirelessClient : WirelessConnection(WifiClient(WifiConnectManagerImpl)) {

    override fun onConnected(device: WifiDevice) {
        Actions.log("Device connected. Ip: ${device.ip}")
        super.onConnected(device)
    }

    override fun onDisconnected(device: WifiDevice) {
        Actions.log("Device disconnected. Ip: ${device.ip}")
        super.onDisconnected(device)
    }

    override fun createWirelessDevice(device: WifiDevice): AbstractWirelessDevice =
            WirelessClientDevice(device)
}