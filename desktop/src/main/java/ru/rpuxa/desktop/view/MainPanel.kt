package ru.rpuxa.desktop.view

import ru.rpuxa.desktop.wireless.InternalServerController
import java.awt.Component
import java.awt.Cursor
import java.awt.Desktop
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.net.URI
import javax.swing.*
import kotlin.concurrent.thread

class MainPanel : JPanel() {

    private val mainSwitch = JCheckBox("Enable Super wireless ADB")
    private val autoLoading = JCheckBox("Enable with Android Studio")
    private val namePicker = NamePicker()
    private val appLabel = JLabel("Plugin requires android app")
    private val appLink = JLabel("<html>Download from <u>Google Play")
    private val gitLabel = JLabel("Check README on github")
    private val gitLink = JLabel("<html><u>$GITHUB_LINK")

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        mainSwitch.alignmentX = Component.LEFT_ALIGNMENT
        add(mainSwitch)

        autoLoading.alignmentX = Component.LEFT_ALIGNMENT
        add(autoLoading)

        namePicker.alignmentX = Component.LEFT_ALIGNMENT
        add(namePicker)

        add(Box.createVerticalStrut(40))

        appLabel.alignmentX = Component.LEFT_ALIGNMENT
        add(appLabel)

        appLink.alignmentX = Component.LEFT_ALIGNMENT
        appLink.cursor = Cursor(Cursor.HAND_CURSOR)
        add(appLink)

        add(Box.createVerticalStrut(20))

        gitLabel.alignmentX = Component.LEFT_ALIGNMENT
        add(gitLabel)

        gitLink.alignmentX = Component.LEFT_ALIGNMENT
        gitLink.cursor = Cursor(Cursor.HAND_CURSOR)
        add(gitLink)
    }


    init {
        mainSwitch.addActionListener {
            if (InternalServerController.isServerRunning()) {
                InternalServerController.terminateServer()
            } else {
                InternalServerController.runServer()
            }
        }

        autoLoading.isSelected = InternalServerController.autoLoading

        autoLoading.addItemListener {
            InternalServerController.autoLoading = autoLoading.isSelected
        }

        gitLink.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                openInBrowser(GITHUB_LINK)
            }
        })

        appLink.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(e: MouseEvent) {
                openInBrowser(APP_LINK)
            }
        })

        thread {
            while (true) {
                mainSwitch.isSelected = InternalServerController.isServerRunning()
                Thread.sleep(1000)
            }
        }
    }

    private fun openInBrowser(link: String) {
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
            Desktop.getDesktop().browse(URI(link))
        }
    }

    companion object {
        private const val GITHUB_LINK = "https://github.com/rpuxa/SuperWirelessADB"
        private const val APP_LINK = "https://play.google.com/store/apps/details?id=ru.rpuxa.superwirelessadb"
    }
}