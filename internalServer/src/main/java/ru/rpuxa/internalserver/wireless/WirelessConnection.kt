package ru.rpuxa.internalserver.wireless

import ru.rpuxa.internalserver.wifi.WifiConnection
import ru.rpuxa.internalserver.wifi.WifiDevice

abstract class WirelessConnection(val wifi: WifiConnection) : WifiConnection.Listener {

    init {
        @Suppress("LeakingThis")
        wifi.setListener(this)
    }

    val devices = HashSet<AbstractWirelessDevice>()

    final override fun onConnected(device: WifiDevice) {
        println("connected")
        val wirelessDevice = createWirelessDevice(device)
        if (!wirelessDevice.wifiDevice.isClosed)
            synchronized(devices) {
                if (devices.add(wirelessDevice))
                    listeners.forEach { it.onConnected(wirelessDevice) }
            }
    }

    final override fun onDisconnected(device: WifiDevice) {
        println("disconnected")
        synchronized(devices) {
            for (wirelessDevice in devices) {
                if (wirelessDevice.wifiDevice.lastAddressByte == device.lastAddressByte) {
                    devices.remove(wirelessDevice)
                    listeners.forEach { it.onDisconnected(wirelessDevice) }
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
        fun onConnected(device: WirelessDevice)

        fun onDisconnected(device: WirelessDevice)
    }
}