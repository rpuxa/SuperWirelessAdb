package ru.rpuxa.desktop

import ru.rpuxa.desktop.view.MainPanel
import ru.rpuxa.desktop.wireless.Adb
import javax.swing.JFrame
import javax.swing.WindowConstants

fun main(args: Array<String>) {
    val frame = JFrame("Wireless Adb")
    Adb.adbPathGetter = { "C:\\SDK\\platform-tools" }
    frame.add(MainPanel())
    frame.setSize(600, 600)
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    frame.isVisible = true
}


