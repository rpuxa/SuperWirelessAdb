package ru.rpuxa.superwirelessadb.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.device_info.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast
import ru.rpuxa.internalserver.Monitoring
import ru.rpuxa.internalserver.wireless.Passport
import ru.rpuxa.internalserver.wireless.WirelessConnection
import ru.rpuxa.internalserver.wireless.WirelessDevice
import ru.rpuxa.internalserver.wireless.WirelessPromise
import ru.rpuxa.superwirelessadb.R
import ru.rpuxa.superwirelessadb.dialogs.LoadingDialog
import ru.rpuxa.superwirelessadb.dialogs.OnErrorDialog
import ru.rpuxa.superwirelessadb.other.dataBase
import ru.rpuxa.superwirelessadb.wireless.Wireless

class InfoActivity : AppCompatActivity(), WirelessConnection.Listener {

    private val devicePassport by lazy { intent.extras.getSerializable(DEVICE_PASSPORT) as Passport }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.device_info)

        Wireless.server.addListener(this)

        setSupportActionBar(info_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        is_adb_connected_layout.setOnClickListener {
            doAsync {
                val isAdbConnected = is_adb_connected.isChecked
                val dialog = LoadingDialog()
                dialog.arguments = bundleOf(
                        LoadingDialog.LOADING_TEXT to if (isAdbConnected) getString(R.string.disconnect) else getString(R.string.connect)
                )
                dialog.show(fragmentManager, LOADING_DIALOG_TAG)

                fun WirelessPromise<*>.onError() {
                    onError {
                        runOnUiThread { toast(getString(R.string.error_try_again)) }
                        dialog.dismiss()
                    }
                }

                val device = Wireless.getDeviceById(devicePassport.id)!!

                if (isAdbConnected) {
                    device.disconnectAdb()
                            .onAnswer {
                                runOnUiThread {
                                    toast(getString(R.string.disconnected))
                                    dialog.dismiss()
                                }
                            }
                            .onError()
                } else {
                    device.connectAdb()
                            .onAnswer { code ->
                                runOnUiThread { onConnectAdb(code, dialog, device) }
                            }
                            .onError()
                }
            }

        }

        autoconnect_adb.isChecked = devicePassport.id in dataBase.autoConnectedDevices

        autoconnect_adb.setOnClickListener {
            if (autoconnect_adb.isChecked)
                dataBase.autoConnectedDevices.add(devicePassport.id)
            else
                dataBase.autoConnectedDevices.remove(devicePassport.id)
        }

        remove_from_my_devices.setOnClickListener {
            val myDevices = dataBase.myDevices
            for (i in myDevices.indices) {
                if (myDevices[i].id == devicePassport.id) {
                    myDevices.removeAt(i)
                    dataBase.autoConnectedDevices.remove(devicePassport.id)
                    break
                }
            }
            startActivity<MainActivity>()
        }

        val device = Wireless.getDeviceById(devicePassport.id)

        if (device == null)
            disconnected()
        else
            connected(device)

        info_device_name.text = devicePassport.name
    }

    private var monitoring: Monitoring<Boolean>? = null

    private fun connected(device: WirelessDevice) {
        runOnUiThread {
            info_device_name.text = device.passport.name
            is_adb_connected.isEnabled = true
            info_online_dot.visibility = View.VISIBLE
            monitoring = Monitoring(
                    { device.isAdbConnected },
                    { connected -> runOnUiThread { is_adb_connected.isChecked = connected } }
            ).start()
        }
    }

    private fun disconnected() {
        runOnUiThread {
            is_adb_connected.isEnabled = false
            info_online_dot.visibility = View.INVISIBLE
            monitoring?.stop()
        }
    }

    private fun onConnectAdb(code: Int, dialog: LoadingDialog, device: WirelessDevice) {
        when {
            code == 0 -> toast(getString(R.string.connected))
            code < 0 -> toast(getString(R.string.unknown_error))
            else -> {
                dialog.dismiss()
                OnErrorDialog(code, device, fragmentManager, this).show()
            }
        }

        dialog.dismiss()
    }

    override fun onConnected(device: WirelessDevice) {
        if (device.passport.id == devicePassport.id)
            connected(device)
    }

    override fun onDisconnected(device: WirelessDevice) {
        if (device.passport.id == devicePassport.id)
            disconnected()
    }

    override fun onPause() {
        dataBase.save(this)
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        Wireless.server.removeListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        startActivity<MainActivity>()
        return true
    }

    companion object {
        const val DEVICE_PASSPORT = "id"
        private const val LOADING_DIALOG_TAG = "loading_dialog"
    }
}
