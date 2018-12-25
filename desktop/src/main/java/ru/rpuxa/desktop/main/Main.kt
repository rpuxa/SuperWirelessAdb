package ru.rpuxa.desktop.main

import ru.rpuxa.desktop.wireless.WirelessClient

fun main(args: Array<String>) {
    WirelessClient().start()
    println("запустили десктоп")
}