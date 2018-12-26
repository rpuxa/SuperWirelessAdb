package ru.rpuxa.internalserver.wireless

import ru.rpuxa.internalserver.wifi.WifiConnection
import ru.rpuxa.internalserver.wifi.WifiDevice

abstract class WirelessConnection(val wifi: WifiConnection) : WifiConnection.Listener {

    init {
        @Suppress("LeakingThis")
        wifi.setListener(this)
    }

    val devices = ArrayList<WirelessDevice>()

    final override fun onConnected(device: WifiDevice) {
        println("connected")
        val wirelessDevice = createWirelessDevice(device)
        synchronized(devices) {
            devices.add(wirelessDevice)
            listeners.forEach { it.onConnected(wirelessDevice, devices.lastIndex) }
        }
    }

    final override fun onDisconnected(device: WifiDevice) {
        println("disconnected")
        synchronized(devices) {
            for (i in devices.indices.reversed()) {
                if (devices[i].wifiDevice === device) {
                    val removed = devices.removeAt(i)
                    listeners.forEach { it.onDisconnected(removed, i) }
                    break
                }
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

    private var listeners = ArrayList<Listener>()

    fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    fun clearListeners() {
        listeners.clear()
    }

    interface Listener {
        fun onConnected(device: WirelessDevice, position: Int)

        fun onDisconnected(device: WirelessDevice, position: Int)
    }
}