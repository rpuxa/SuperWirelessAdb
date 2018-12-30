package ru.rpuxa.internalserver.wireless

import ru.rpuxa.internalserver.wifi.WifiConnection
import ru.rpuxa.internalserver.wifi.WifiDevice

abstract class WirelessConnection(val wifi: WifiConnection) : WifiConnection.Listener {

    init {
        @Suppress("LeakingThis")
        wifi.setListener(this)
    }

    val devices = ArrayList<AbstractWirelessDevice>()

    final override fun onConnected(device: WifiDevice) {
        println("connected")
        val wirelessDevice = createWirelessDevice(device)
        if (!wirelessDevice.wifiDevice.isClosed)
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

    val isWifiConnected: Boolean get() = wifi.isWifiConnected

    abstract fun createWirelessDevice(device: WifiDevice): AbstractWirelessDevice

    private var listeners = ArrayList<Listener>()

    fun addListener(listener: Listener) {
        synchronized(devices) {
            listeners.add(listener)
        }
    }

    fun removeListener(listener: Listener) {
        synchronized(devices) {
            listeners.remove(listener)
        }
    }

    interface Listener {
        fun onConnected(device: WirelessDevice, position: Int)

        fun onDisconnected(device: WirelessDevice, position: Int)
    }
}