package ru.rpuxa.desktop.wireless

import ru.rpuxa.internalserver.wireless.Passport
import java.io.*
import java.net.InetAddress
import java.net.NetworkInterface
import java.util.*
import kotlin.concurrent.thread

object InternalServerController {

    private val computerSeed: Long
        get() {
            val nameHash = System.getProperty("user.name").hashCode()
            val macHash = Arrays.hashCode(NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).hardwareAddress)
            return Random((nameHash.toLong() shl 31) or macHash.toLong()).nextLong()
        }

    var autoLoading = true
    lateinit var passport: Passport
    private val folderPath = System.getenv("APPDATA") + "\\SuperWirelessADB"
    private const val FILE_NAME = "settings.dat"
    private var server: WirelessClient? = null

    init {
        load()
    }

    fun runServer() {
        thread {
            server?.stop()
            server = WirelessClient()
            server!!.start()
            save()
        }
    }

    fun renameServer(name: String) {
        passport.name = name
        save()
    }

    fun terminateServer() {
        server?.stop()
        server = null
        save()
    }

    private fun save() {
        val folder = File(folderPath)
        if (!folder.exists())
            folder.mkdir()
        ObjectOutputStream(FileOutputStream(File(folderPath, FILE_NAME))).use {
            it.writeObject(passport)
            it.writeObject(autoLoading)
            it.flush()
        }
    }

    private fun load() {
        try {
            val file = File(folderPath, FILE_NAME)
            ObjectInputStream(FileInputStream(file)).use {
                passport = it.readObject() as Passport
                autoLoading = it.readObject() as Boolean
            }
        } catch (e: Exception) {
            e.printStackTrace()
            passport = Passport(
                    computerSeed,
                    "ADB_${System.getProperty("user.name")}"
            )
        }
    }

    fun isServerRunning(): Boolean = server != null && server!!.wifi.running.get()
}