package ru.rpuxa.superwirelessadb.wifi

import ru.rpuxa.internalserver.wifi.WifiConnectManager
import ru.rpuxa.internalserver.wifi.WifiConnection
import ru.rpuxa.internalserver.wifi.WifiException
import java.net.InetAddress
import java.net.ServerSocket
import java.net.SocketException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class WifiServer(
        wifiConnectManager: WifiConnectManager,
        listener: Listener? = null
) : WifiConnection(wifiConnectManager, listener) {

    override fun search(address: String): Search = SearchClient(address)

    private inner class SearchClient(override val address: String) : Search {


        private val running = AtomicBoolean(true)
        private var serverSocket: ServerSocket? = null

        init {
            start()
        }


        override fun start() {
            thread {
                while (running.get()) {
                    try {
                        val inetAddress = InetAddress.getByName(address)
                        serverSocket = ServerSocket(PORT, 0, inetAddress)
                        while (running.get()) {
                            val socket = serverSocket!!.accept()
                            val input = socket.getInputStream()
                            val output = socket.getOutputStream()
                            if (checkDevice(output, input))
                                addDevice(output, input, socket.inetAddress)
                        }
                    } catch (e: SocketException) {
                        e.printStackTrace()
                        Thread.sleep(300)
                    } catch (e: WifiException) {
                        Thread.sleep(500)
                    }
                }
            }
        }

        override fun stop() {
            running.set(false)

            try {
                serverSocket?.close()
            } catch (e: Exception) {
            }
        }
    }
}