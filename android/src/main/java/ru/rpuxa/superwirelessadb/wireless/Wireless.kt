package ru.rpuxa.superwirelessadb.wireless

object Wireless {

    lateinit var server: WirelessServer

    val devices get() = server.devices
}