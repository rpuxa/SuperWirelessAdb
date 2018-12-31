package ru.rpuxa.desktop.wireless

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetAddress

object Adb {

    private const val UNKNOWN_ERROR = -1

    lateinit var adbPathGetter: () -> String

    private val adbPath get() = adbPathGetter()

    fun check(ip: InetAddress): Boolean {
        try {
            val address = "${ip.toString().substring(1)}:5555"
            val builder = ProcessBuilder("cmd.exe", "/c", "cd $adbPath && adb devices")
            val reader = BufferedReader(InputStreamReader(builder.start().inputStream))
            while (true) {
                val line = reader.readLine() ?: return false
                if (line.contains(address))
                    return true
            }
        } catch (e: IOException) {
            return false
        }
    }

    fun connect(ip: InetAddress) = change(ip, true)

    fun disconnect(ip: InetAddress) = change(ip, false)

    private fun change(ip: InetAddress, connect: Boolean): Int {
        try {
            val address = "${ip.toString().substring(1)}:5555"
            val builder = ProcessBuilder("cmd.exe", "/c", "cd $adbPath && adb ${if (connect) "connect" else "disconnect"} $address")
            builder.redirectErrorStream(true)
            val reader = BufferedReader(InputStreamReader(builder.start().inputStream))
            var fullAnswer = ""
            while (true) {
                val line = reader.readLine() ?: break
                fullAnswer += line
            }
            val openBracket = fullAnswer.lastIndexOf('(')
            val closeBracket = fullAnswer.lastIndexOf(')')
            return if (openBracket != -1) {
                fullAnswer.substring(openBracket + 1, closeBracket).toInt()
            } else {
                0
            }
        } catch (e: IOException) {
            return UNKNOWN_ERROR
        }
    }

    fun fix10061(ip: InetAddress): Boolean {
        return try {
            val builder = ProcessBuilder(
                    "cmd.exe",
                    "/c",
                    "cd $adbPath && " +
                            "adb usb && " +
                            "adb kill-server && " +
                            "adb tcpip 5555 && " +
                            "adb connect ${ip.toString().substring(1)}:5555"
            )
            BufferedReader(InputStreamReader(builder.start().inputStream)).readLine()
            check(ip)
        } catch (e: IOException) {
            false
        }
    }

    fun containsAdb(path: String): Boolean {
        try {
            val builder = ProcessBuilder("cmd.exe", "/c", "cd $path && adb version")
            builder.redirectErrorStream(true)
            val reader = BufferedReader(InputStreamReader(builder.start().inputStream))
            while (true) {
                val line = reader.readLine() ?: return false
                if (line.startsWith("Android Debug Bridge version"))
                    return true
            }
        } catch (e: IOException) {
            return false
        }
    }


}