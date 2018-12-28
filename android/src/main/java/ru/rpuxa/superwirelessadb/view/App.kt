package ru.rpuxa.superwirelessadb.view

import android.app.Application
import ru.rpuxa.superwirelessadb.wireless.Wireless
import ru.rpuxa.superwirelessadb.wireless.WirelessServer

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        Wireless.server = WirelessServer(dataBase.passport)
    }

}