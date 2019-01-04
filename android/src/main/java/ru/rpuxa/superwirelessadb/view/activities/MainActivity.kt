package ru.rpuxa.superwirelessadb.view.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.device_list.*
import kotlinx.android.synthetic.main.settings.*
import ru.rpuxa.superwirelessadb.R
import ru.rpuxa.superwirelessadb.view.DeviceListAdapter
import ru.rpuxa.superwirelessadb.view.MyDeviceListAdapter
import ru.rpuxa.superwirelessadb.view.dataBase
import ru.rpuxa.superwirelessadb.wireless.Wireless

class MainActivity : AppCompatActivity() {

    private val devicesAdapter by lazy { DeviceListAdapter(dataBase.myDevices) }

    private val myDevicesAdapter by lazy { MyDeviceListAdapter(dataBase.myDevices) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_list)

        device_recycler_view.adapter = devicesAdapter
        device_recycler_view.layoutManager = LinearLayoutManager(this)
        Wireless.server.addListener(devicesAdapter)

        my_device_recycler_view.adapter = myDevicesAdapter
        my_device_recycler_view.layoutManager = LinearLayoutManager(this)
        Wireless.server.addListener(myDevicesAdapter)


        val tabHost = tabhost
        tabHost.setup()


        tabHost.addTab(tabHost.newTabSpec(MY_DEVICES_TAB_TAG).apply {
            setContent(R.id.my_devices_tab)
            setIndicator("Мои компьютеры")
        })


        tabHost.addTab(tabHost.newTabSpec("devices").apply {
            setContent(R.id.devices_tab)
            setIndicator("Ближайшие компьютеры")
        })


        tabHost.addTab(tabHost.newTabSpec("settings").apply {
            setContent(R.id.settings_tab)
            setIndicator("Настройки")
        })

        tabHost.currentTab =
                if (dataBase.myDevices.size == 0) {
                    1
                } else {
                    0
                }

        tabHost.setOnTabChangedListener {
            if (it == MY_DEVICES_TAB_TAG)
                myDevicesAdapter.update()
        }

        start_in_background.isChecked = dataBase.runServiceInBackground

        start_in_background.setOnClickListener {
            dataBase.runServiceInBackground = start_in_background.isChecked
        }
    }

    override fun onPause() {
        super.onPause()
        dataBase.save(this)
    }

    override fun onStop() {
        super.onStop()
        Wireless.server.removeListener(devicesAdapter)
        Wireless.server.removeListener(myDevicesAdapter)
    }

    companion object {
        private const val MY_DEVICES_TAB_TAG = "my_devices"

    }
}
