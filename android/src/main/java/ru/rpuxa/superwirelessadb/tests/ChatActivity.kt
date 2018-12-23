package ru.rpuxa.superwirelessadb.tests

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.test_chat.*
import ru.rpuxa.internalserver.wifi.WifiConnect
import ru.rpuxa.internalserver.wifi.WifiConnectManagerImpl
import ru.rpuxa.internalserver.wifi.WifiDevice
import ru.rpuxa.internalserver.wifi.WifiServer
import ru.rpuxa.superwirelessadb.R
import java.io.DataInputStream
import java.io.DataOutputStream
import kotlin.concurrent.thread

class ChatActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.test_chat)

        var d: WifiDevice? = null

        val server = WifiServer(WifiConnectManagerImpl, object : WifiConnect.Listener() {
            override fun onConnected(device: WifiDevice) {
                d = device
                message("connected")

                thread {
                    try {
                        val data = DataInputStream(device.input)
                        while (true) {
                            message(data.readUTF())
                        }
                    } catch (e: Exception) {
                        message("disconnected")
                        device.close()
                    }
                }
            }
        })

        send.setOnClickListener {
            thread {
                val text = inputTextView.text.toString()
                if (d != null) {
                    try {
                        DataOutputStream(d!!.output).writeUTF(text)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }

        server.start()

    }


    fun message(m: String) {
        field.text = field.text.toString() + m + '\n'
    }
}
