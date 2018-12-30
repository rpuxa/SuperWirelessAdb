package ru.rpuxa.superwirelessadb.view

import android.app.Application
import android.content.ComponentCallbacks2
import android.content.Intent
import android.os.Build
import org.jetbrains.anko.startService
import ru.rpuxa.superwirelessadb.services.InternalServerService
import ru.rpuxa.superwirelessadb.wireless.Wireless
import ru.rpuxa.superwirelessadb.wireless.WirelessServer

class App : Application(), ComponentCallbacks2 {

    override fun onCreate() {
        super.onCreate()
        Wireless.server = WirelessServer(dataBase.passport)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startForegroundService(Intent(this, InternalServerService::class.java))
        else
            startService<InternalServerService>()
    }


}