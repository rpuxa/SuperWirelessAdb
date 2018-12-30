package ru.rpuxa.superwirelessadb.services

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ru.rpuxa.superwirelessadb.wireless.Wireless
import kotlin.concurrent.thread

class AdbReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val id = intent.extras.getLong(DEVICE_ID)
        thread {
            val device = Wireless.device(id)
            device?.connectAdb()
        }
    }

    companion object {
        const val DEVICE_ID = "id"
    }
}