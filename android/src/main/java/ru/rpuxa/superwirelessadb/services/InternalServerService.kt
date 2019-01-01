package ru.rpuxa.superwirelessadb.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Process
import android.os.Process.killProcess
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import org.jetbrains.anko.intentFor
import ru.rpuxa.internalserver.wireless.WirelessConnection
import ru.rpuxa.internalserver.wireless.WirelessDevice
import ru.rpuxa.superwirelessadb.R
import ru.rpuxa.superwirelessadb.view.activities.InfoActivity
import ru.rpuxa.superwirelessadb.view.activities.MainActivity
import ru.rpuxa.superwirelessadb.view.dataBase
import ru.rpuxa.superwirelessadb.wireless.Wireless
import kotlin.concurrent.thread

class InternalServerService : Service(), WirelessConnection.Listener {

    private val channelId by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel("super_wireless_adb", "Super wireless ADB")
        } else {
            ""
        }
    }

    private var destroyed = false

    override fun onCreate() {
        startForeground(SERVICE_ID, notification(null))
        Wireless.server.addListener(this)
        thread {
            var isWifiConnected: Boolean? = null
            var device: WirelessDevice? = null
            var deviceName: String? = null
            var isAdbConnected: Boolean? = null
            while (!destroyed) {
                val currentIsWifiConnected = Wireless.server.isWifiConnected
                val currentDevice = Wireless.myOnlineDevices(this).getOrNull(0)
                val currentDeviceName = currentDevice?.passport?.name
                val currentIsAdbConnected = currentDevice?.isAdbConnected

                if (
                        isWifiConnected != currentIsWifiConnected ||
                        device != currentDevice ||
                        isAdbConnected != currentIsAdbConnected ||
                        deviceName != currentDeviceName
                ) {
                    updateNotification(currentDevice)
                }

                isWifiConnected = currentIsWifiConnected
                device = currentDevice
                isAdbConnected = currentIsAdbConnected
                deviceName = currentDeviceName

                Thread.sleep(500)
            }
        }
    }

    override fun onBind(intent: Intent) = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Wireless.server.start()
        return START_STICKY
    }

    override fun onDestroy() {
        destroyed = true
        Wireless.server.removeListener(this)
        Wireless.server.stop()
        super.onDestroy()
        killProcess(Process.myPid())
    }

    private fun NotificationCompat.Builder.buildNotification(device: WirelessDevice?): Notification {
        setOngoing(true)
        setSmallIcon(R.drawable.connect_adb)
        setCategory(Notification.CATEGORY_SERVICE)
        setContentTitle("Super wireless ADB")
        val adbConnected = device?.isAdbConnected
        val text =
                when {
                    !Wireless.server.isWifiConnected -> "Вы не подключены к WiFi"
                    adbConnected == null -> "Ближайших устройств не найдено"
                    !adbConnected -> "Обнаружен компьютер ${device.passport.name}"
                    else -> "Соединение ADB с ${device.passport.name} установлено"
                }

        setContentText(text)

        if (adbConnected == false) {
            val intent = intentFor<AdbReceiver>(
                    AdbReceiver.DEVICE_ID to device.passport.id
            )
            val broadcast = PendingIntent.getBroadcast(
                    this@InternalServerService,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
            )

            addAction(R.drawable.connect_adb, "Подключить ADB", broadcast)
        }
        val intent = if (device == null)
            intentFor<MainActivity>()
        else
            intentFor<InfoActivity>(InfoActivity.DEVICE_PASSPORT to device.passport)

        val pendingIntent = PendingIntent.getActivity(
                this@InternalServerService,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
        )


        setContentIntent(pendingIntent)

        return build()
    }


    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (!dataBase.runServiceInBackground) {
            stopSelf(SERVICE_ID)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(channelId: String, channelName: String): String {
        val chan = NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE)
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        service.createNotificationChannel(chan)
        return channelId
    }

    private fun notification(device: WirelessDevice?): Notification? {
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
        return notificationBuilder.buildNotification(device)
    }

    private fun updateNotification(device: WirelessDevice?) {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        mNotificationManager.notify(SERVICE_ID, notification(device))
    }

    override fun onConnected(device: WirelessDevice, position: Int) {
        if (device.passport.id in dataBase.autoConnectedDevices)
            thread { device.connectAdb() }
    }

    override fun onDisconnected(device: WirelessDevice, position: Int) {
    }

    companion object {
        const val SERVICE_ID = -915561234
    }
}
