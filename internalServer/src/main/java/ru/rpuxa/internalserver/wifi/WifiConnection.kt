package ru.rpuxa.internalserver.wifi

import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

abstract class WifiConnection(
        private val wifiConnectManager: WifiConnectManager,
        private var listener: Listener? = null
) {
    val running = AtomicBoolean(false)
    private val searchers = ArrayList<Search>()
    private val devices = ArrayList<WifiDevice>()
    val isWifiConnected: Boolean
        get() = searchers.isNotEmpty()

    fun start() {
        if (running.get())
            return

        running.set(true)

        thread {
            while (running.get()) {
                val addresses = wifiConnectManager.ipAddresses

                for (a in addresses)
                    if (searchers.count { it.address == a } == 0)
                        searchers.add(search(a))

                for (i in searchers.indices.reversed())
                    if (addresses.count { it == searchers[i].address } == 0) {
                        searchers.removeAt(i).stop()
                    }

                removeDisconnectedDevices()

                Thread.sleep(500)
            }

            searchers.forEach { it.stop() }
            devices.forEach { it.close() }
            removeDisconnectedDevices()
        }
    }

    private fun removeDisconnectedDevices() {
        for (i in devices.indices.reversed()) {
            val device = devices[i]
            if (device.isClosed) {
                devices.removeAt(i)
                listener?.onDisconnected(device)
            }
        }
    }

    protected fun addDevice(output: OutputStream, input: InputStream, address: InetAddress) {
        val lastByte = address.address.last().toInt()

        if (!devices.any { it.lastAddressByte == lastByte }) {
            val device = WifiDevice(input, output, address)
            devices.add(device)
            listener?.onConnected(device)
        }
    }

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun stop() {
        running.set(false)
    }

    protected abstract fun search(address: String): Search

    interface Listener {
        fun onConnected(device: WifiDevice)

        fun onDisconnected(device: WifiDevice)
    }

    protected interface Search {
        val address: String

        fun start()

        fun stop()
    }
}