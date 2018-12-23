package ru.rpuxa.internalserver.main

import ru.rpuxa.internalserver.wifi.WifiClient
import ru.rpuxa.internalserver.wifi.WifiConnect
import ru.rpuxa.internalserver.wifi.WifiConnectManagerImpl
import ru.rpuxa.internalserver.wifi.WifiDevice
import java.io.BufferedReader
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
import kotlin.concurrent.thread


fun main(args: Array<String>) {
    start()
}

fun start() {
    var d: WifiDevice? = null

    WifiClient(WifiConnectManagerImpl, object : WifiConnect.Listener() {
        override fun onConnected(device: WifiDevice) {
            println("connected")
            d = device
            thread {
                try {
                    val data = DataInputStream(device.input)
                    while (true) {
                        println(data.readUTF())
                    }
                } catch (e: Exception) {
                    println("disconnected")
                    device.close()
                }
            }
        }
    }).start()

    val reader = BufferedReader(InputStreamReader(System.`in`))

    while (true) {
        try {
            val text = reader.readLine()
            if (d != null) {
                DataOutputStream(d!!.output).writeUTF(text)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}