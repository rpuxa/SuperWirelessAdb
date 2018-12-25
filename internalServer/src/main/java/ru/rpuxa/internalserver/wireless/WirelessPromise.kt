package ru.rpuxa.internalserver.wireless

interface WirelessPromise<T> {
    fun onAnswer(callback: (T) -> Unit): WirelessPromise<T>

    fun onError(callback: (WirelessErrors) -> Unit): WirelessPromise<T>
}