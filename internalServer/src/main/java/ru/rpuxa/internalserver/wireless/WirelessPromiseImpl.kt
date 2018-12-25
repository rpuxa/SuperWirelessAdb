package ru.rpuxa.internalserver.wireless

class WirelessPromiseImpl<T> : WirelessPromise<T> {
    private var answerCallback: ((T) -> Unit)? = null
    private var errorCallback: ((WirelessErrors) -> Unit)? = null

    override fun onAnswer(callback: (T) -> Unit): WirelessPromise<T> {
        answerCallback = callback
        return this
    }

    override fun onError(callback: (WirelessErrors) -> Unit): WirelessPromise<T> {
        errorCallback = callback
        return this
    }

    fun answer(t: T) {
        answerCallback?.invoke(t)
    }

    fun error(error: WirelessErrors) {
        errorCallback?.invoke(error)
    }
}