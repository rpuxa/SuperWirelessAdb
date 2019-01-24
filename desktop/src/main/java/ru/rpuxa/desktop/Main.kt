package ru.rpuxa.desktop

import ru.rpuxa.desktop.view.MainPanel
import ru.rpuxa.desktop.wireless.Adb
import javax.swing.JFrame
import javax.swing.WindowConstants

fun main(args: Array<String>) {
    val frame = JFrame("Super wireless ADB")
    Adb.adbPathGetter = { "C:\\SDK\\platform-tools" }
    setActions(PCActions)
    frame.add(MainPanel())
    frame.setSize(600, 600)
    frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
    frame.isVisible = true
}

object PCActions : IActions {
    override fun showToast(msg: String) {
        println("Toast:  $msg")
    }

    override fun log(lines: Iterable<String>) {
        for (line in lines)
            println(line)
    }
}


