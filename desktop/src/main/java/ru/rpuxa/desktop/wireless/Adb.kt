package ru.rpuxa.desktop.wireless

import ru.rpuxa.desktop.Actions
import ru.rpuxa.internalserver.wireless.ADB_NOT_FOUND
import ru.rpuxa.internalserver.wireless.UNKNOWN_ERROR
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.InetAddress

object Adb {

    lateinit var adbPathGetter: () -> String

    private val adbPath get() = adbPathGetter()

    fun check(ip: InetAddress, logging: Boolean = false): Boolean {
        if (adbNotFound)
            return false
        val answer = executeAdbCommand(logging, "adb devices") ?: return false
        val address = "${ip.toString().substring(1)}:5555"
        for (line in answer)
            if (line.contains(address))
                return true
        return false
    }

    fun connect(ip: InetAddress): Int {
        val res = change(ip, true)
        return when {
            res != 0 -> res
            check(ip, true) -> {
                0
            }
            else -> UNKNOWN_ERROR
        }
    }

    fun disconnect(ip: InetAddress) {
        change(ip, false)
    }

    private fun change(ip: InetAddress, connect: Boolean): Int {
        if (adbNotFound)
            return ADB_NOT_FOUND
        val cmd = if (connect) "connect" else "disconnect"
        val address = "${ip.toString().substring(1)}:5555"
        val answer = executeAdbCommand(true, "adb $cmd $address")
                ?: return UNKNOWN_ERROR

        val builder = StringBuilder()
        for (line in answer)
            builder.append(line)

        val openBracket = builder.lastIndexOf('(')
        val closeBracket = builder.lastIndexOf(')')
        return if (openBracket != -1) {
            builder.substring(openBracket + 1, closeBracket).toIntOrNull() ?: UNKNOWN_ERROR
        } else {
            0
        }
    }

    fun fix10061(ip: InetAddress): Boolean {
        if (adbNotFound)
            return false
        executeAdbCommand(true,
                "adb usb",
                "adb kill-server",
                "adb tcpip 5555",
                "adb connect ${ip.toString().substring(1)}:5555"
        )
        return check(ip)
    }

    private val adbNotFound: Boolean
        get() {
            val result = !containsAdb(adbPath)
            if (result) {
                Actions.log("Adb not founded, path - $adbPath")
            }
            return result
        }

    private fun containsAdb(path: String): Boolean {
        val answer = executeCommand(listOf("cd $path", "adb version"), false)
                ?: return false
        for (line in answer) {
            if (line.startsWith("Android Debug Bridge version"))
                return true
        }
        return false
    }

    private fun executeAdbCommand(logging: Boolean, vararg commands: String): List<String>? {
        val list = mutableListOf("cd $adbPath")
        list.addAll(commands)
        return executeCommand(list, logging)
    }

    private fun executeCommand(commands: List<String>, logging: Boolean = true): List<String>? {
        if (logging) {
            val log = mutableListOf("Executing command:")
            log.addAll(commands)
            Actions.log(log)
        }

        try {
            val stringBuilder = StringBuilder()
            for ((i, command) in commands.withIndex()) {
                stringBuilder.append(command)
                if (i != commands.lastIndex)
                    stringBuilder.append(" && ")
            }
            val builder = ProcessBuilder("cmd.exe", "/c", stringBuilder.toString())
            builder.redirectErrorStream(true)
            val reader = BufferedReader(InputStreamReader(builder.start().inputStream))
            val answer = ArrayList<String>()
            while (true) {
                val line = reader.readLine() ?: break
                answer.add(line)
            }
            reader.close()
            if (logging) {
                val log2 = StringBuilder()
                for (line in answer)
                    log2.append(line).append('\n')
                Actions.log("Command executed successful. Answer:\n$log2")
            }
            return answer
        } catch (e: IOException) {
            if (logging)
                Actions.log("Error while executing command. Error:\n${e.message}")
            e.printStackTrace()
            return null
        }
    }
}