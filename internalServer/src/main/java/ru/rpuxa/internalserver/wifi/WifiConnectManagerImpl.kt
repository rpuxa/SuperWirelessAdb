package ru.rpuxa.internalserver.wifi

import java.net.NetworkInterface

object WifiConnectManagerImpl : WifiConnectManager {

    override val ipAddresses: List<String>
        get() {
            val list = ArrayList<String>()
            val interfaces = NetworkInterface.getNetworkInterfaces()
            for (network in interfaces) {
                for (address in network.inetAddresses) {
                    val string = address.toString()
                    if (string.startsWith("/192.168.") || string.startsWith("/10."))
                        list.add(string.substring(1))
                }
            }

            return list
        }

    override fun isConnected(address: String) =
            address in ipAddresses

}