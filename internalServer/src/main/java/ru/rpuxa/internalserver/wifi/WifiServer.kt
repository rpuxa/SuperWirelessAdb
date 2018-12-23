package ru.rpuxa.internalserver.wifi

import java.net.InetAddress
import java.net.ServerSocket
import java.net.SocketException
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

class WifiServer(
       wifiConnectManager: WifiConnectManager,
       listener: WifiConnect.Listener
) : WifiConnect(wifiConnectManager, listener) {

    override fun search(address: String): Search = SearchClient(address)

    private inner class SearchClient(override val address: String) : Search {


        private val running = AtomicBoolean(true)
        private var serverSocket: ServerSocket? = null

        init {
            start()
        }


        override fun start() {
            while (running.get()) {
                try {
                    val inetAddress = InetAddress.getByName(address)
                    serverSocket = ServerSocket(WifiClient.PORT, 0, inetAddress)
                    while (running.get()) {
                        val socket = serverSocket!!.accept()
                        val input = socket.getInputStream()
                        val output = socket.getOutputStream()

                        thread {
                            if (WifiClient.checkDevice(output, input))
                                addDevice(output, input, inetAddress)
                        }
                    }
                } catch (e: SocketException) {
                    e.printStackTrace()
                } catch (e: WifiException) {
                    Thread.sleep(500)
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