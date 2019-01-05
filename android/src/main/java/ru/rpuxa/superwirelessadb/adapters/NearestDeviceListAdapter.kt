package ru.rpuxa.superwirelessadb.adapters

import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import kotlinx.android.synthetic.main.device_item.view.*
import ru.rpuxa.internalserver.wireless.Passport
import ru.rpuxa.internalserver.wireless.WirelessConnection
import ru.rpuxa.internalserver.wireless.WirelessDevice
import ru.rpuxa.superwirelessadb.R
import ru.rpuxa.superwirelessadb.wireless.Wireless

class NearestDeviceListAdapter(
        private val myDevices: MutableList<Passport>
) : RecyclerView.Adapter<NearestDeviceListAdapter.Holder>(),
        WirelessConnection.Listener {

    private val devices = ArrayList<WirelessDevice>(Wireless.devices)

    private val handler = Handler()

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.device_name
        val isMyDevice: CheckBox = view.device_is_my_checkbox
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.device_item, parent, false)

        return Holder(view)
    }

    override fun getItemCount(): Int {
        return devices.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val device = devices[position]

        holder.apply {
            isMyDevice.isChecked = myDevices.any { it.id == device.passport.id }

            isMyDevice.setOnClickListener {
                myDevices.remove(device.passport)

                if (isMyDevice.isChecked)
                    myDevices.add(device.passport)
            }

            name.text = device.passport.name
        }
    }

    override fun onConnected(device: WirelessDevice) {
        handler.post {
            synchronized(devices) {
                devices.add(device)
                notifyItemInserted(devices.lastIndex)
            }
        }
    }

    override fun onDisconnected(device: WirelessDevice) {
        handler.post {
            synchronized(devices) {
                for (i in devices.indices.reversed())
                    if (devices[i] == device) {
                        devices.removeAt(i)
                        notifyItemRemoved(i)
                        break
                    }
            }
        }
    }
}
