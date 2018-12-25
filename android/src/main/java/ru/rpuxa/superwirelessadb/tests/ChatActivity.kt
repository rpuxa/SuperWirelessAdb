package ru.rpuxa.superwirelessadb.tests

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.test_chat.*
import ru.rpuxa.internalserver.wifi.WifiDevice
import ru.rpuxa.superwirelessadb.R

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_chat)

        var d: WifiDevice? = null

        /*    val server = WifiServer(WifiConnectManagerImpl, object : WifiConnection.Adapter() {
                override fun onConnected(device: WifiDevice) {
                    d = device
                    message("connected")
                    device.input.onMessage {
                        message(it as String)
                    }
                }

                override fun onDisconnected(device: WifiDevice) {
                    d = null
                    message("disconnected")
                }
            })

            send.setOnClickListener {
                thread {
                    val text = inputTextView.text.toString()
                    if (d != null) {
                        d!!.output.sendMessage(text)
                    }
                }
            }

            server.start()
    */
    }


    fun message(m: String) {
        field.text = field.text.toString() + m + '\n'
    }
}
