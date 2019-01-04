package ru.rpuxa.internalserver.wifi

import java.io.BufferedOutputStream
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
    protected val devices = ArrayList<WifiDevice>()
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

    protected companion object {
        private val WIFI_CLIENT_SERVER_IDENTIFIER = /* Random byte array. Dont edit! */
                "iG4GBsAjlfI0mqQw9jHgG4dTa2Xtov90PcXIsckaru2ncHDbZPZpm1BojlfI0mqQof2fM"
                        .toByteArray()
                        .map { it.toInt() }
                        .toIntArray()

        const val PORT = 7159

        fun checkDevice(output: OutputStream, input: InputStream): Boolean {
            try {
                val bufferedOutputStream = BufferedOutputStream(output, WIFI_CLIENT_SERVER_IDENTIFIER.size)
                for (byte in WIFI_CLIENT_SERVER_IDENTIFIER)
                    bufferedOutputStream.write(byte)
                bufferedOutputStream.flush()

                for (i in WIFI_CLIENT_SERVER_IDENTIFIER.indices)
                    if (input.read() != WIFI_CLIENT_SERVER_IDENTIFIER[i])
                        return false
                return true
            } catch (e: Exception) {
                e.printStackTrace()
                return false
            }
        }
    }
}