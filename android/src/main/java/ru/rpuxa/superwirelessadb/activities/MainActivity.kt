package ru.rpuxa.superwirelessadb.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.device_list.*
import kotlinx.android.synthetic.main.settings.*
import org.jetbrains.anko.browse
import ru.rpuxa.superwirelessadb.R
import ru.rpuxa.superwirelessadb.adapters.AdapterListener
import ru.rpuxa.superwirelessadb.adapters.MyDeviceListAdapter
import ru.rpuxa.superwirelessadb.adapters.NearestDeviceListAdapter
import ru.rpuxa.superwirelessadb.other.dataBase
import ru.rpuxa.superwirelessadb.wireless.Wireless

class MainActivity : AppCompatActivity() {

    private val nearestDevicesAdapter by lazy { NearestDeviceListAdapter(dataBase.myDevices) }

    private val myDevicesAdapter by lazy { MyDeviceListAdapter(dataBase.myDevices) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_list)

        nearestDevicesAdapter.registerAdapterDataObserver(AdapterListener {
            nearest_devices_list_empty.visibility =
                    if (nearestDevicesAdapter.itemCount == 0) View.VISIBLE else View.GONE
        })
        device_recycler_view.adapter = nearestDevicesAdapter
        device_recycler_view.layoutManager = LinearLayoutManager(this)
        Wireless.server.addListener(nearestDevicesAdapter)

        myDevicesAdapter.registerAdapterDataObserver(AdapterListener {
            my_devices_list_empty.visibility =
                    if (myDevicesAdapter.itemCount == 0) View.VISIBLE else View.GONE
        })
        my_device_recycler_view.adapter = myDevicesAdapter
        my_device_recycler_view.layoutManager = LinearLayoutManager(this)
        Wireless.server.addListener(myDevicesAdapter)


        val tabHost = tabhost
        tabHost.setup()


        tabHost.addTab(tabHost.newTabSpec(MY_DEVICES_TAB_TAG).apply {
            setContent(R.id.my_devices_tab)
            setIndicator(getString(R.string.my_computers))
        })


        tabHost.addTab(tabHost.newTabSpec(NEAREST_DEVICES_TAB_TAG).apply {
            setContent(R.id.devices_tab)
            setIndicator(getString(R.string.nearest_computers))
        })


        tabHost.addTab(tabHost.newTabSpec(SETTINGS_TAB_TAG).apply {
            setContent(R.id.settings_tab)
            setIndicator(getString(R.string.settings))
        })

        tabHost.currentTab = if (dataBase.myDevices.size == 0) 1 else 0

        tabHost.setOnTabChangedListener {
            if (it == MY_DEVICES_TAB_TAG)
                myDevicesAdapter.update()
        }

        start_in_background.isChecked = dataBase.isServiceRunInBackground

        start_in_background.setOnClickListener {
            dataBase.isServiceRunInBackground = start_in_background.isChecked
        }

        github_link.setOnClickListener {
            browse(getString(R.string.github_link), true)
        }
    }

    override fun onPause() {
        super.onPause()
        dataBase.save(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        Wireless.server.removeListener(nearestDevicesAdapter)
        Wireless.server.removeListener(myDevicesAdapter)
    }

    companion object {
        private const val MY_DEVICES_TAB_TAG = "my_devices"
        private const val NEAREST_DEVICES_TAB_TAG = "devices"
        private const val SETTINGS_TAB_TAG = "settings"
    }
}
