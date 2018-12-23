package ru.rpuxa.internalserver.wifi

import ru.rpuxa.internalserver.stream.MessageInputStream
import ru.rpuxa.internalserver.stream.MessageOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

abstract class WifiConnect(
        val wifiConnectManager: WifiConnectManager,
        var listener: Listener
) {
    private val searchers = ArrayList<Search>()
    private val running = AtomicBoolean(false)
    protected val devices = ArrayList<WifiDevice>()

    fun start() {
        if (running.get())
            throw IllegalStateException("Already started!")

        running.set(true)

        thread {
            while (running.get()) {
                val addresses = wifiConnectManager.ipAddresses

                for (a in addresses)
                    if (searchers.count { it.address == a } == 0)
                        searchers.add(search(a))

                for (s in searchers)
                    if (addresses.count { it == s.address } == 0) {
                        searchers.remove(s)
                        s.stop()
                    }

                Thread.sleep(500)
            }

            searchers.forEach { it.stop() }
        }
    }

    protected fun addDevice(output: OutputStream, input: InputStream, address: InetAddress) {
        for (i in devices.indices.reversed()) {
            val device = devices[i]
            if (device.isClosed) {
                devices.removeAt(i)
                listener.onDisconnected(device)
            }
        }

        val lastByte = address.address.last().toInt()

        if (!devices.any { it.lastAddressByte == lastByte }) {
            val device = WifiDevice(MessageOutputStream(output), MessageInputStream(input), lastByte)
            devices.add(device)
            listener.onConnected(device)
        }
    }

    fun stop() {
        running.set(false)
    }

    protected abstract fun search(address: String): Search

    open class Listener {
        open fun onConnected(device: WifiDevice) {
        }

        open fun onDisconnected(device: WifiDevice) {
        }
    }

    protected interface Search {
        val address: String

        fun start()

        fun stop()
    }
}