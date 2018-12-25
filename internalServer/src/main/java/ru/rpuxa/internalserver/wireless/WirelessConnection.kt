package ru.rpuxa.internalserver.wireless

import ru.rpuxa.internalserver.wifi.WifiConnection
import ru.rpuxa.internalserver.wifi.WifiDevice

abstract class WirelessConnection(val wifi: WifiConnection) : WifiConnection.Listener {

    init {
        wifi.setListener(this)
    }

    val devices = ArrayList<WirelessDevice>()

    final override fun onConnected(device: WifiDevice) {
        val wirelessDevice = createWirelessDevice(device)
        devices.add(wirelessDevice)
        listener?.onConnected(wirelessDevice)
    }

    final override fun onDisconnected(device: WifiDevice) {
        for (i in devices.indices.reversed()) {
            if (devices[i].wifiDevice === device) {
                listener?.onDisconnected(devices.removeAt(i))
                break
            }
        }
    }

    fun start() {
        wifi.start()
    }

    fun stop() {
        wifi.stop()
    }

    abstract fun createWirelessDevice(device: WifiDevice): WirelessDevice

    private var listener: Listener? = null

    fun setListener(listener: Listener) {
        this.listener = listener
    }

    interface Listener {
        fun onConnected(device: WirelessDevice)

        fun onDisconnected(device: WirelessDevice)
    }
}