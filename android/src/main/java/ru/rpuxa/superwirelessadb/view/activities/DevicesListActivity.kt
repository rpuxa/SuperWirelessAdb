package ru.rpuxa.superwirelessadb.view.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.TabHost
import kotlinx.android.synthetic.main.device_list.*
import ru.rpuxa.superwirelessadb.R
import ru.rpuxa.superwirelessadb.view.DeviceListAdapter
import ru.rpuxa.superwirelessadb.view.MyDeviceListAdapter
import ru.rpuxa.superwirelessadb.view.dataBase
import ru.rpuxa.superwirelessadb.wireless.Wireless

class DevicesListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_list)

        val adapter = DeviceListAdapter(Wireless.devices, dataBase.myDevices)
        device_recycler_view.adapter = adapter
        device_recycler_view.layoutManager = LinearLayoutManager(this)
        Wireless.server.addListener(adapter)

        val adapter2 = MyDeviceListAdapter(dataBase.myDevices)
        my_device_recycler_view.adapter = adapter2
        my_device_recycler_view.layoutManager = LinearLayoutManager(this)
        Wireless.server.addListener(adapter2)


        val tabHost = tabhost
        tabHost.setup()
        var tabSpec = tabHost.newTabSpec("tag1")
        tabSpec.setContent(R.id.my_devices_tab)
        tabSpec.setIndicator("Мои компьютеры")
        tabHost.addTab(tabSpec)
        tabSpec = tabHost.newTabSpec("tag2")
        tabSpec.setContent(R.id.devices_tab)
        tabSpec.setIndicator("Ближайшие компьютеры")
        tabHost.addTab(tabSpec)
        tabHost.currentTab = 0
    }

    override fun onPause() {
        super.onPause()
        dataBase.save(this)
    }

    override fun onStop() {
        super.onStop()
        Wireless.server.clearListeners()
    }
}
