package ru.rpuxa.superwirelessadb.view.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.device_list.*
import ru.rpuxa.superwirelessadb.R
import ru.rpuxa.superwirelessadb.view.DeviceListAdapter
import ru.rpuxa.superwirelessadb.wireless.Wireless

class DevicesListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_list)

        val adapter = DeviceListAdapter(Wireless.devices)
        device_recycler_view.adapter = adapter
        device_recycler_view.layoutManager = LinearLayoutManager(this)


        Wireless.server.setListener(adapter)
        Wireless.server.start()
    }
}
