package ru.rpuxa.superwirelessadb.dialogs

import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.loading_dialog.view.*
import org.jetbrains.anko.custom.onUiThread
import ru.rpuxa.superwirelessadb.R
import kotlin.concurrent.thread

class LoadingDialog : DialogFragment() {

    private var dismissed = false


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.loading_dialog, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        isCancelable = false
        val text = arguments[LOADING_TEXT] as CharSequence
        var pointsCount = 1
        thread {
            while (!dismissed) {
                val points = StringBuilder(text)
                repeat(pointsCount) { points.append('.') }
                onUiThread {
                    view.loading_text.text = points
                }
                pointsCount++
                if (pointsCount > 3)
                    pointsCount = 1
                Thread.sleep(400)
            }


        }
    }

    override fun onDismiss(dialog: DialogInterface?) {
        dismissed = true
        super.onDismiss(dialog)
    }

    companion object {
        const val LOADING_TEXT = "text"
    }
}