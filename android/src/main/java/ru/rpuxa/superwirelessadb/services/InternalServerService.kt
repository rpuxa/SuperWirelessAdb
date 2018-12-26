package ru.rpuxa.superwirelessadb.services

import android.app.Notification
import android.app.Service
import android.content.Intent
import ru.rpuxa.superwirelessadb.wireless.Wireless

class InternalServerService : Service() {

    override fun onCreate() {
        startForeground(SERVICE_ID, notification())
    }

    override fun onBind(intent: Intent) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Wireless.server.start()
        return START_STICKY
    }

    override fun onDestroy() {
        Wireless.server.stop()
        super.onDestroy()
    }


    private fun notification(): Notification? {
        return null
    }

    companion object {
        const val SERVICE_ID = -915561234
    }
}
