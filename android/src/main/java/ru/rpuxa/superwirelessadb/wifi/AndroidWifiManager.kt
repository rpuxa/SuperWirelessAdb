package ru.rpuxa.superwirelessadb.wifi

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import ru.rpuxa.internalserver.wifi.WifiConnectManager

class AndroidWifiManager(context: Context) : WifiConnectManager {
    private val manager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    override val ipAddresses: List<String>
        get() = listOf(
                Formatter.formatIpAddress(manager.connectionInfo.ipAddress)
        )

    override fun isConnected(address: String): Boolean =
            manager.isWifiEnabled
}