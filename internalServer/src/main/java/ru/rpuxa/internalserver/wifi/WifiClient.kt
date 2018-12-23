package ru.rpuxa.internalserver.wifi

import java.io.BufferedOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class WifiClient(
        wifiConnectManager: WifiConnectManager,
        listener: WifiConnect.Listener
) : WifiConnect(wifiConnectManager, listener) {

    override fun search(address: String): Search = SearchServer(address)

    private inner class SearchServer(override val address: String) : Search {
        val running = AtomicBoolean(true)

        init {
            start()
        }

        override fun start() {
            try {
                val address = InetAddress.getByName(address).address

                for (byte in 1..254) {
                    if (byte != address[3].toInt())
                        thread {
                            while (running.get()) {
                                val newAddress = address.clone()
                                newAddress[3] = byte.toByte()
                                val inetAddress = InetAddress.getByAddress(newAddress)
                                if (inetAddress.isReachable(1000))
                                    checkAddress(inetAddress)
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
                socket.connect(InetSocketAddress(address, PORT), 500)
                val output = socket.getOutputStream()
                val input = socket.getInputStream()
                if (checkDevice(output, input))
                    addDevice(output, input, address)
            } catch (e: Exception) {
            }
        }
    }


    companion object {
        private val WIFI_CLIENT_SERVER_IDENTIFIER = /* Random byte array. Dont edit! */
                "iG4GBsAjlfI0mqQw9jHgG4dTa2Xtov90PcXIsckaru2ncHDbZPZpm1BojlfI0mqQof2fM"
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