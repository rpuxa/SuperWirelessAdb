package ru.rpuxa.desktop

import java.awt.Component
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.InputStreamReader
import javax.swing.*
import kotlin.concurrent.thread

class MainPanel : JPanel() {

    private val mainSwitch = JCheckBox("Enable Super wireless ADB")
    private val autoLoading = JCheckBox("Enable on start Android Studio")

    private val apkButton = JButton("Install APK")
    private val namePicker = NamePicker()

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        mainSwitch.alignmentX = Component.LEFT_ALIGNMENT
        add(mainSwitch)

        autoLoading.alignmentX = Component.LEFT_ALIGNMENT
        add(autoLoading)

        namePicker.alignmentX = Component.LEFT_ALIGNMENT
        add(namePicker)

        add(Box.createVerticalStrut(10))

        add(apkButton)

    }


    //Listeners
    init {
        mainSwitch.addActionListener {

        }

        thread {
            while (true) {
                TODO("Обновление сервера")
            }
        }
    }

    private fun startServer() {
        val input = ClassLoader.getSystemResourceAsStream(SERVER_NAME)

        val folder = File(System.getenv("APPDATA") + "\\SuperWirelessADB")

        if (!folder.exists())
            folder.mkdir()

        val internalServer = File(folder, SERVER_NAME)

        FileOutputStream(internalServer).use {
            while (true) {
                val byte = input.read()
                if (byte == -1)
                    break
                it.write(byte)
            }
        }
        input.close()

        val reader = BufferedReader(InputStreamReader(
                ProcessBuilder(
                        "cmd.exe",
                        "/c",
                        "cd ${internalServer.absolutePath} && java -jar ${internalServer.name}")
                        .redirectErrorStream(true)
                        .start()
                        .inputStream
        ))

        thread(isDaemon = true) {
            var fullAnswer = ""
            while (true) {
                val line = reader.readLine() ?: break
                fullAnswer += line
                println(line)
            }
        }
    }

    companion object {
        const val SERVER_NAME = "internalServer.jar"

    }
}