package ru.rpuxa.superwirelessadb.tests

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ru.rpuxa.superwirelessadb.R
import ru.rpuxa.superwirelessadb.services.MyService

class ServiceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_service)

        startService(Intent(this, MyService::class.java))
    }
}
