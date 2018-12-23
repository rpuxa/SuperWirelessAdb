package ru.rpuxa.internalserver.wifi

interface WifiConnectManager {

    val ipAddresses: List<String>

    fun isConnected(address: String): Boolean
}