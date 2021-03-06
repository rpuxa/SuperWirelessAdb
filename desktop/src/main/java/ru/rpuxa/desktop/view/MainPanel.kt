package ru.rpuxa.desktop.view

import ru.rpuxa.desktop.wireless.InternalServerController
import java.awt.Component
import java.awt.Cursor
import java.awt.Desktop
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.net.URI
import javax.swing.*
import kotlin.concurrent.thread

class MainPanel : JPanel() {

    private val mainSwitch = JCheckBox("Enable Super wireless ADB")
    private val autoLoading = JCheckBox("Enable with Android Studio")
    private val namePicker = NamePicker()
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

        gitLabel.alignmentX = Component.LEFT_ALIGNMENT
        add(gitLabel)

        gitLink.alignmentX = Component.LEFT_ALIGNMENT
        gitLink.cursor = Cursor(Cursor.HAND_CURSOR)
        add(gitLink)
    }


    init {
        mainSwitch.addActionListener {
            if (InternalServerController.isServerRunning())
                InternalServerController.terminateServer()
            else
                InternalServerController.runServer()
        }

        autoLoading.isSelected = InternalServerController.autoLoading

        autoLoading.addItemListener {
            InternalServerController.autoLoading = autoLoading.isSelected
        }

        gitLink.addMouseListener(object : MouseListener {
            override fun mouseReleased(p0: MouseEvent?) {
            }

            override fun mouseEntered(p0: MouseEvent?) {
            }

            override fun mouseClicked(p0: MouseEvent?) {
                if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                    Desktop.getDesktop().browse(URI(GITHUB_LINK))
                }
            }

            override fun mouseExited(p0: MouseEvent?) {
            }

            override fun mousePressed(p0: MouseEvent?) {
            }
        })

        thread {
            while (true) {
                mainSwitch.isSelected = InternalServerController.isServerRunning()
                Thread.sleep(1000)
            }
        }
    }

    companion object {
        const val GITHUB_LINK = "https://www.github.com/rpuxa/SuperWirelessADB"
    }
}