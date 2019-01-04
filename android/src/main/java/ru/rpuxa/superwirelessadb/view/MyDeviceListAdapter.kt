package ru.rpuxa.superwirelessadb.view

import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.my_device_item.view.*
import org.jetbrains.anko.startActivity
import ru.rpuxa.internalserver.wireless.Passport
import ru.rpuxa.internalserver.wireless.WirelessConnection
import ru.rpuxa.internalserver.wireless.WirelessDevice
import ru.rpuxa.superwirelessadb.R
import ru.rpuxa.superwirelessadb.view.activities.InfoActivity
import ru.rpuxa.superwirelessadb.wireless.Wireless

class MyDeviceListAdapter(private val myDevices: MutableList<Passport>) :
        RecyclerView.Adapter<MyDeviceListAdapter.Holder>(),
        WirelessConnection.Listener {

    private val handler = Handler()

    class Holder(val view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.my_device_name
        val dot: ImageView = view.my_device_is_connected
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.my_device_item, parent, false)

        return Holder(view)
    }

    override fun getItemCount(): Int {
        return myDevices.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val passport = myDevices[position]

        val connected = Wireless.devices.any { it.passport.id == passport.id }

        holder.dot.visibility = if (connected) View.VISIBLE else View.INVISIBLE

        holder.name.text = passport.name

        holder.view.setOnClickListener {
            holder.view.context.startActivity<InfoActivity>(InfoActivity.DEVICE_PASSPORT to passport)
        }
    }

    fun update() {
        handler.post {
            notifyDataSetChanged()
        }
    }

    override fun onConnected(device: WirelessDevice) {
        var myDevice: Passport? = null
        var index = -1

        for (i in myDevices.indices) {
            if (myDevices[i].id == device.passport.id) {
                index = i
                myDevice = myDevices[i]
            }
        }

        if (myDevice != null) {
            myDevice.name = device.passport.name

            handler.post {
                notifyItemChanged(index)
            }
        }
    }

    override fun onDisconnected(device: WirelessDevice) {
        handler.post {
            val indexOf = myDevices.indexOf(device.passport)
            if (indexOf != -1)
                notifyItemChanged(indexOf)
        }
    }
}
