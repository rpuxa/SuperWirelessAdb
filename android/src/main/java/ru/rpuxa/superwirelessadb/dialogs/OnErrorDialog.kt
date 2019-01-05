package ru.rpuxa.superwirelessadb.dialogs

import android.app.AlertDialog
import android.app.FragmentManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import kotlinx.android.synthetic.main.on_error_dialog.view.*
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import ru.rpuxa.internalserver.wireless.WirelessDevice
import ru.rpuxa.superwirelessadb.R
import kotlin.concurrent.thread

class OnErrorDialog(
        private val code: Int,
        private val device: WirelessDevice,
        private val fm: FragmentManager,
        context: Context
) : AlertDialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = LayoutInflater.from(context).inflate(R.layout.on_error_dialog, null, false)
        setContentView(view)

        view.apply {
            error_code.text = code.toString()

            if (code == 10061) {
                error_fix.setOnClickListener {
                    val loadingDialog = LoadingDialog().apply {
                        arguments = bundleOf(LoadingDialog.LOADING_TEXT to getString(R.string.fixing))
                        show(fm, "ld")
                    }
                    thread {
                        device.fixAdbError10061()
                                .onAnswer { fixed ->
                                    context.runOnUiThread {
                                        if (fixed) {
                                            loadingDialog.dismiss()
                                            Handler().postDelayed({ this@OnErrorDialog.dismiss() }, 500)
                                        } else {
                                            error_message.text = getString(R.string.fix_error)
                                            loadingDialog.dismiss()
                                        }
                                    }
                                }
                                .onError {
                                    context.runOnUiThread {
                                        context.toast(getString(R.string.unknown_error))
                                        loadingDialog.dismiss()
                                    }
                                }
                    }
                }
            } else {
                error_message.visibility = View.GONE
                error_fix.visibility = View.INVISIBLE
            }

            error_back.setOnClickListener {
                dismiss()
            }
        }
    }
}