package ru.rpuxa.superwirelessadb.wireless

object Wireless {

    val server = WirelessServer()

    val devices get() = server.devices
}