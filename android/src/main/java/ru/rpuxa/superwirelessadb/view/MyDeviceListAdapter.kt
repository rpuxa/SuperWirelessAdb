package ru.rpuxa.superwirelessadb.view

import android.os.Handler
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.device_item.view.*
import ru.rpuxa.internalserver.wireless.Passport
import ru.rpuxa.internalserver.wireless.WirelessConnection
import ru.rpuxa.internalserver.wireless.WirelessDevice
import ru.rpuxa.superwirelessadb.R
import ru.rpuxa.superwirelessadb.wireless.Wireless

class MyDeviceListAdapter(private val list: MutableList<Passport>) :
        RecyclerView.Adapter<MyDeviceListAdapter.Holder>(),
        WirelessConnection.Listener {

    private val handler = Handler()

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.device_name
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.device_item, parent, false)

        return Holder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val passport = list[position]

        val connected = Wireless.devices.any { it.passport.id == passport.id }

        TODO("")

        holder.name.text = passport.name
    }

    override fun onConnected(device: WirelessDevice, position: Int) {
        handler.post {
            notifyItemChanged(position)
        }
    }

    override fun onDisconnected(device: WirelessDevice, position: Int) {
        handler.post {
            notifyItemChanged(position)
        }
    }
}
