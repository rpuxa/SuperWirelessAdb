package ru.rpuxa.internalserver.wifi

import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class WifiClient(
        wifiConnectManager: WifiConnectManager,
        listener: WifiConnection.Listener? = null
) : WifiConnection(wifiConnectManager, listener) {

    override fun search(address: String): Search = SearchServer(address)

    private inner class SearchServer(override val address: String) : Search {
        val running = AtomicBoolean(true)

        init {
            start()
        }

        override fun start() {
            try {
                val address = InetAddress.getByName(address).address

                for (byte in LAST_IP_BYTE_RANGE) {
                    if (byte != address.last().toInt())
                        thread {
                            while (running.get()) {
                                if (!devices.any { it.lastAddressByte == byte }) {
                                    val newAddress = address.clone()
                                    newAddress[3] = byte.toByte()
                                    val inetAddress = InetAddress.getByAddress(newAddress)
                                    if (inetAddress.isReachable(1000))
                                        checkAddress(inetAddress)
                                }
                                Thread.sleep(500)
                            }
                        }
                }

            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: WifiException) {
                Thread.sleep(300)
            }
        }

        override fun stop() {
            running.set(false)
        }

        private fun checkAddress(address: InetAddress) {
            try {
                val socket = Socket()
                socket.connect(InetSocketAddress(address, PORT), 2000)
                val output = socket.getOutputStream()
                val input = socket.getInputStream()
                if (checkDevice(output, input))
                    addDevice(output, input, address)
            } catch (e: IOException) {
            }
        }
    }

    companion object {
        @JvmStatic
        private val LAST_IP_BYTE_RANGE = 1..254
    }
}