package ru.rpuxa.superwirelessadb.adapters

import android.support.v7.widget.RecyclerView

class AdapterListener(val onUpdate: () -> Unit) : RecyclerView.AdapterDataObserver() {

    override fun onChanged() {
        onUpdate()
    }

    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        onUpdate()

    }

    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        onUpdate()

    }

    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        onUpdate()

    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        onUpdate()

    }

    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        onUpdate()

    }
}