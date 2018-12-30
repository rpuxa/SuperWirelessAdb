package ru.rpuxa.desktop.wireless

import ru.rpuxa.internalserver.stream.NothingReturn
import ru.rpuxa.internalserver.stream.TwoWayMessageStream
import ru.rpuxa.internalserver.wireless.Passport
import java.io.*
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.ServerSocket
import java.util.*

object DesktopInternalServer {

    private const val SERVER_PORT = 7160

    private val computerSeed: Long
        get() {
            val nameHash = System.getProperty("user.name").hashCode()
            val macHash = Arrays.hashCode(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).hardwareAddress)
            return Random((nameHash.toLong() shl 31) or macHash.toLong()).nextLong()
        }

    lateinit var passport: Passport

    private val folderPath = System.getenv("APPDATA") + "\\SuperWirelessADB"
    private const val FILE_NAME = "settings.dat"


    fun start() {
        try {
            load()
            WirelessClient().start()

            val serverSocket = ServerSocket(SERVER_PORT, 0, InetAddress.getByName("localhost"))
            while (true) {
                val socket = serverSocket.accept()
                val stream = TwoWayMessageStream(socket.getInputStream(), socket.getOutputStream())
                stream.onMessage { command, data ->
                    val ans = when (command) {
                        TERMINATE -> {
                            throw Exception("Exit")
                        }
                        SET_NAME -> {
                            passport.name = data as String
                            NothingReturn
                        }
                        else -> throw UnsupportedOperationException("Unsupported command $command!")
                    }

                    ans
                }
            }
        } finally {
            save()
            System.exit(0)
        }
    }

    private fun save() {
        val folder = File(folderPath)
        if (!folder.exists())
            folder.mkdir()
        ObjectOutputStream(FileOutputStream(File(folderPath, FILE_NAME))).use {
            it.writeObject(passport)
        }
    }

    private fun load() {
        passport =
                try {
                    ObjectInputStream(FileInputStream(File(folderPath, FILE_NAME))).use {
                        it.readObject() as Passport
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Passport(
                            computerSeed,
                            "Adb_${System.getProperty("user.name")}"
                    )
                }
    }


    private const val TERMINATE = 1
    private const val SET_NAME = 2
}