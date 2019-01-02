package ru.rpuxa.superwirelessadb.view.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import org.jetbrains.anko.startActivity
import ru.rpuxa.superwirelessadb.view.dataBase

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val myDevices = dataBase.myDevices
        if (myDevices.size == 1)
            startActivity<InfoActivity>(InfoActivity.DEVICE_PASSPORT to myDevices[0])
        else
            startActivity<MainActivity>()
    }

}
