package ru.rpuxa.desktop.view

import ru.rpuxa.desktop.wireless.InternalServerController
import java.awt.Component
import javax.swing.BoxLayout
import javax.swing.JCheckBox
import javax.swing.JPanel
import kotlin.concurrent.thread

class MainPanel : JPanel() {

    private val mainSwitch = JCheckBox("Enable Super wireless ADB")
    private val autoLoading = JCheckBox("Enable Super wireless ADB with Android Studio")
    private val namePicker = NamePicker()

    init {
        layout = BoxLayout(this, BoxLayout.Y_AXIS)

        mainSwitch.alignmentX = Component.LEFT_ALIGNMENT
        add(mainSwitch)

        autoLoading.alignmentX = Component.LEFT_ALIGNMENT
        add(autoLoading)

        namePicker.alignmentX = Component.LEFT_ALIGNMENT
        add(namePicker)
    }


    //Listeners
    init {
        mainSwitch.addActionListener {
            if (InternalServerController.isServerRunning())
                InternalServerController.terminateServer()
            else
                InternalServerController.runServer()
        }

        autoLoading.isSelected = InternalServerController.autoLoading

        autoLoading.addActionListener {
            InternalServerController.autoLoading = autoLoading.isSelected
        }

        thread {
            while (true) {
                mainSwitch.isSelected = InternalServerController.isServerRunning()
                Thread.sleep(1000)
            }
        }
    }
}