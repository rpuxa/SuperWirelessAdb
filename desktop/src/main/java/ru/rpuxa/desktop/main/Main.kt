package ru.rpuxa.desktop.main

import ru.rpuxa.desktop.wireless.DesktopInternalServer

fun main(args: Array<String>) {
    println("Start desktop server")
    DesktopInternalServer.start()
}